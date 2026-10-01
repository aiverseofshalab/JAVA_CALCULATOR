# macOS application icon

To use a custom application icon, add a macOS `.icns` file named `EngineeringCalculator.icns` to this directory. `scripts/package-macos.sh` detects that file and passes it to `jpackage`; when it is absent, macOS uses its default application icon.
