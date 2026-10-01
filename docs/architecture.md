# Architecture

The application separates calculation rules from presentation. The expression, engineering, conversion, and history packages do not depend on Swing.

## Packages

- `core`: `CalculatorEngine` is the expression evaluation facade. `ExpressionParser` implements a recursive-descent grammar and reports safe, readable `ExpressionException` messages. `AngleMode` represents DEG/RAD.
- `engineering`: stateless formula services validate domains and compute electrical, mechanics, physics, error, and quadratic results. `EngineeringFormula` provides formula metadata.
- `conversion`: `UnitConverter` stores category unit definitions as base-unit scales, with explicit affine handling for temperature.
- `history`: `CalculationHistory` stores immutable entries in memory and exposes snapshots for the presentation layer.
- `gui`: `MainFrame` composes the tabs, status area, and optional history drawer. `CalculatorPanel` presents separate expression and result displays, and hosts mutually exclusive normal/scientific keypad cards. `NormalCalculatorPanel` owns the common keys; `ScientificCalculatorPanel` adds function keys while reusing the same common-key behavior. Both delegate tokens to `CalculatorController`.
- `gui.components`: `FormulaCard` is a reusable labeled input/result component that handles parsing, user feedback, reset, and history callback wiring.
- `util`: reusable numeric display formatting.

## Expression evaluation

The parser applies expression → term → unary → power → postfix → primary precedence. It supports grouped expressions, unary signs, right-associative exponentiation, percent and factorial, constants, and single-argument scientific calls. No scripting or dynamic code evaluation is used. Invalid domains and non-finite results become friendly errors.

## UI flow

The calculator's `CalculatorController` is the single source of expression, result, previous answer, mode, angle mode, and error state. Button and window-level keyboard actions go through `handleInput`; after each token, the panel renders controller state. A `CardLayout` switches between the normal and scientific keypad panels without replacing controller state or displays. A separate segmented angle selection drives direct and inverse trigonometric functions. Engineering forms pass labeled values to service methods; presentation code does not implement formulas. History is shared in memory across calculator, engineering, and converter features and can be opened as a compact side drawer.
