# Engineering Calculator

A Java 21 desktop scientific and engineering calculator for students, developers, and engineers. The application combines a safe expression parser with an engineering formula workspace, unit conversion, searchable reference material, and recent calculation history.

## Overview

The calculator has distinct Normal and Scientific modes. Scientific mode adds function keys above the same numeric keypad. The application also provides categorized engineering cards with labeled inputs and units, a converter for six common unit categories, a searchable formula guide, and a collapsible history drawer.

## Features

- Expression evaluation with precedence, parentheses, power, modulo, postfix percent and factorial, constants `pi` and `e`, and previous answer `ans`.
- Scientific functions: trigonometry and inverse trigonometry, logarithms, roots, absolute value, exponential, rounding functions, and factorial.
- Visible DEG/RAD selection that controls trig and inverse trig.
- Electrical, mechanics, physics, error analysis, and quadratic formula cards with validation and reset actions.
- Length, mass, temperature, time, area, and speed conversions.
- Searchable help topics with syntax, formula, example, description, units, and shortcut information.
- Clickable recent calculator expressions; engineering and conversion results remain available as references.
- Keyboard input, Enter to calculate, Escape to clear, and Backspace to delete.

## Screenshots

Add real screenshots captured from the running application to `docs/screenshots/`. Suggested views: Normal Calculator, Scientific Calculator, Engineering workspace, Unit Converter, and Help Guide.

## Architecture

The business layer remains independent of Swing. `core` contains the recursive-descent parser and calculator facade. `engineering` and `conversion` contain reusable calculation services. `history` provides a small in-memory history API. `gui` contains the application shell and screen panels; `gui.components` contains reusable formula cards. `util` provides result formatting.

## Project structure

```text
src/main/java/com/shalab/calculator/
  core/          expression parser and calculator engine
  engineering/   electrical, mechanics, physics and formula services
  conversion/    unit conversion registry
  history/       in-memory calculation history
  gui/           application shell and feature panels
    components/  reusable engineering formula card
  util/          number formatting
src/test/java/   JUnit 5 tests for core, engineering and conversion
```

## Tech stack

Java 21, Swing, Maven, JUnit 5. No third-party UI framework is required.

## Download

**macOS Apple Silicon:** `EngineeringCalculator-1.0.0.dmg` (generated in `dist/` by the packaging script below).

The native macOS application bundle includes its own Java runtime, so end users do not need to install Java separately. The DMG is built for Apple Silicon on macOS.

## Build from source

A Java 25 JDK is used for macOS packaging. The project includes the official Maven Wrapper, which downloads the pinned Maven distribution on its first run; a separate Maven installation is not required.

```bash
./mvnw clean test
./mvnw package
```

Run the packaged application JAR:

```bash
java -jar target/engineering-calculator-1.0.0.jar
```

A graphical desktop session is required.

## Build macOS installer

On an Apple Silicon Mac with Java 25 and `jpackage` available, run:

```bash
./scripts/package-macos.sh
```

The script tests and packages the project, creates `dist/Engineering Calculator.app` with a bundled runtime, creates and verifies `dist/EngineeringCalculator-1.0.0.dmg`, and uses `src/main/resources/icons/EngineeringCalculator.icns` if that icon is added. Native packages must be created on their target platform.
## Testing

`mvn clean test` runs the JUnit 5 unit suite covering expression arithmetic and precedence, scientific functions and invalid domains, engineering formulas and validation, quadratic roots, and unit conversions.

## Calculator examples

- `2 + 3 * 4` → `14`
- `(10 + 5) * 2` → `30`
- `50%` → `0.5`
- `5%2` → `1`
- DEG `sin(90)` → `1`
- RAD `sin(pi/2)` → `1`
- `sqrt(144)` → `12`

## Engineering formulas

The workspace includes Ohm's law, electrical power, series and parallel resistance, voltage division, force, kinetic and potential energy, density, momentum, work, mechanical power, error calculations, and a real/complex quadratic solver. See [docs/formulas.md](docs/formulas.md).

## Keyboard shortcuts

| Key | Action |
| --- | --- |
| `0`–`9`, decimal, operators, parentheses | Enter expression |
| `Enter` | Calculate |
| `Escape` | Clear expression and result |
| `Backspace` | Delete selection or previous character |

## Error handling

Invalid expressions and mathematical domains are shown in the calculator result area. Engineering forms report field and domain issues next to the corresponding form. No expression is evaluated using a scripting engine or arbitrary code execution.

## Future improvements

Graph plotting, matrix calculations, extended complex arithmetic, persistent history, calculation export, additional engineering domains, and configurable themes.

## Author

Shalab Kumar Shrivastava  
B.Tech CSE / IoT, Cybersecurity & Blockchain Technology  
[GitHub](https://github.com/aiverseofshalab) · [LinkedIn](https://linkedin.com/in/aiverseofshalab)
