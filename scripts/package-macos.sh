#!/usr/bin/env bash
set -euo pipefail

APP_NAME="Engineering Calculator"
APP_VERSION="1.0.0"
BUNDLE_ID="com.shalab.engineeringcalculator"
EXPECTED_DMG="EngineeringCalculator-${APP_VERSION}.dmg"
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST_DIR="$PROJECT_ROOT/dist"
PACKAGE_DIR="$PROJECT_ROOT/target/macos-package"
INPUT_DIR="$PACKAGE_DIR/input"
IMAGE_DIR="$DIST_DIR"
DMG_STAGING="$PACKAGE_DIR/dmg"
APP_IMAGE="$DIST_DIR/${APP_NAME}.app"
DMG_FINAL="$DIST_DIR/$EXPECTED_DMG"
MAIN_JAR="engineering-calculator-${APP_VERSION}.jar"

fail() { printf 'package-macos: %s\n' "$1" >&2; exit 1; }

[[ "$(uname -s)" == "Darwin" ]] || fail "Run this script on macOS; jpackage creates packages for the host platform."
[[ "$(uname -m)" == "arm64" ]] || fail "This packaging target is Apple Silicon (arm64)."

# Make the selected JDK explicit so Maven and jpackage use the same Java 25 toolchain.
if [[ -z "${JAVA_HOME:-}" || ! -x "${JAVA_HOME}/bin/jpackage" ]]; then
  [[ -x /usr/libexec/java_home ]] || fail "Set JAVA_HOME to a Java 25 JDK containing jpackage."
  JAVA_HOME="$(/usr/libexec/java_home -v 25 2>/dev/null || true)"
fi
[[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/java" && -x "$JAVA_HOME/bin/jpackage" ]] || fail "Java 25 JDK with jpackage was not found. Set JAVA_HOME and retry."
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"
JAVA_VERSION="$("$JAVA_HOME/bin/java" -version 2>&1 | head -n 1)"
JPACKAGE_VERSION="$("$JAVA_HOME/bin/jpackage" --version)"
[[ "$JAVA_VERSION" == *'25.'* || "$JAVA_VERSION" == *'version "25"'* ]] || fail "Expected Java 25, found: $JAVA_VERSION"
[[ "$JPACKAGE_VERSION" == 25* ]] || fail "Expected Java 25 jpackage, found version $JPACKAGE_VERSION"
JAVA_ARCH="$("$JAVA_HOME/bin/java" -XshowSettings:properties -version 2>&1 | awk '/os.arch =/{print $3; exit}')"
[[ "$JAVA_ARCH" == "aarch64" || "$JAVA_ARCH" == "arm64" ]] || fail "Expected an Apple Silicon JDK; Java reports architecture '$JAVA_ARCH'."

mkdir -p "$DIST_DIR"
rm -rf "$APP_IMAGE" "$PACKAGE_DIR" "$DMG_FINAL"

cd "$PROJECT_ROOT"
printf 'Building and testing with Maven Wrapper (%s)\n' "$JAVA_VERSION"
./mvnw clean test
./mvnw package

JAR_PATH="$PROJECT_ROOT/target/$MAIN_JAR"
[[ -f "$JAR_PATH" ]] || fail "Expected executable JAR was not produced: $JAR_PATH"
mkdir -p "$INPUT_DIR" "$DMG_STAGING"
MANIFEST="$($JAVA_HOME/bin/jar --list --file "$JAR_PATH" >/dev/null && /usr/bin/unzip -p "$JAR_PATH" META-INF/MANIFEST.MF)"
grep -Fq 'Main-Class: com.shalab.calculator.App' <<< "$MANIFEST" || fail "The Maven JAR does not declare com.shalab.calculator.App as Main-Class."
"$JAVA_HOME/bin/java" -Djava.awt.headless=true -jar "$JAR_PATH" || fail "The packaged JAR entry point failed its headless launch check."
cp "$JAR_PATH" "$INPUT_DIR/$MAIN_JAR"

JPACKAGE_ARGS=(
  --name "$APP_NAME"
  --app-version "$APP_VERSION"
  --vendor "Shalab Kumar Shrivastava"
  --description "Scientific and engineering calculator"
  --input "$INPUT_DIR"
  --main-jar "$MAIN_JAR"
  --main-class com.shalab.calculator.App
  --mac-package-identifier "$BUNDLE_ID"
)
ICON_PATH="$PROJECT_ROOT/src/main/resources/icons/EngineeringCalculator.icns"
if [[ -f "$ICON_PATH" ]]; then
  JPACKAGE_ARGS+=(--icon "$ICON_PATH")
else
  printf 'No .icns file found; jpackage will use the macOS default icon. Add one at %s to customize it.\n' "$ICON_PATH"
fi

printf 'Creating self-contained .app with bundled Java runtime...\n'
"$JAVA_HOME/bin/jpackage" --type app-image --dest "$IMAGE_DIR" "${JPACKAGE_ARGS[@]}"
[[ -d "$APP_IMAGE" ]] || fail "jpackage did not create the application bundle: $APP_IMAGE"

printf 'Creating DMG installer...\n'
if ! "$JAVA_HOME/bin/jpackage" --type dmg --dest "$DMG_STAGING" "${JPACKAGE_ARGS[@]}"; then
  fail "jpackage could not create the DMG. Check the hdiutil diagnostics and ensure macOS can create a writable disk image."
fi
GENERATED_DMG="$DMG_STAGING/${APP_NAME}-${APP_VERSION}.dmg"
[[ -f "$GENERATED_DMG" ]] || fail "jpackage did not create expected DMG: $GENERATED_DMG"
mv "$GENERATED_DMG" "$DMG_FINAL"

command -v hdiutil >/dev/null 2>&1 || fail "hdiutil is required to verify the DMG."
hdiutil verify "$DMG_FINAL"
[[ -x "$APP_IMAGE/Contents/MacOS/Engineering Calculator" ]] || fail "Application launcher is missing from the .app bundle."
[[ -d "$APP_IMAGE/Contents/runtime/Contents/Home" ]] || fail "Bundled Java runtime is missing from the .app bundle."
JAVA_TOOL_OPTIONS=-Djava.awt.headless=true "$APP_IMAGE/Contents/MacOS/$APP_NAME" || fail "The packaged app launcher failed its headless smoke check."
printf '\nPackaging succeeded.\nApp: %s\nDMG: %s\n' "$APP_IMAGE" "$DMG_FINAL"
