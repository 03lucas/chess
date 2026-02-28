# Java Chess Engine & GUI

This project applies Object-Oriented Programming (OOP) pillars, has a robust state management architecture and implements an AI algorithm.

![chessGIF](https://github.com/user-attachments/assets/33a30f67-6aa8-40ef-9396-d1545bd5185c)

## Core Architecture

### Move Generation & UI Integration
Move validation, algebraic notation, and match history are built without external chess libraries:
- **Move Tracking:** The `GameHistoryPanel` UI is populated by a `DataModel` that reads from `MoveLog`, tracking the game state using a standard `List<Move>`. 
- **Dynamic Notation:** Strings like "Nf3" or check/checkmate hashes ("+", "#") are computed dynamically during runtime by verifying the target `tile` and the `board` status after a `MoveTransition`.

### State Immutability
- `Board` instances are immutable utilizing Google Guava (`ImmutableList`, `ImmutableMap`) to ensure that the current board state cannot be modified.
- Every executed `Move` uses the `builder pattern` to generate a completely new `board` instance preventing side-effects when the AI simulates possibilities.

### UI Responsiveness
- To prevent the graphical interface from freezing during AI calculations the MiniMax search tree is isolated in a background thread using SwingWorker (`AIThinkTank`).
- The `Table` class acts as an Observer, updating the GUI only when the calculation is complete.

### AI algorithm
The AI evaluates future plays using the **MiniMax** algorithm. The `StandardBoardEvaluator` class determines the most advantageous move by scoring the board based on:
- `Material advantage` (raw sum of active piece values), `mobility`, `king safety`, `threat detection` (depth multiplier prioritize checkmates found earlier in the simulation tree).

## Tech Stack
- **Java 8+**
- **Swing (javax.swing):** UI layout, graphics rendering, and mouse event listeners.
- **Google Guava (33.2.1):** Data integrity via immutable collections.
- **JUnit:** Logic validation (e.g., verifying Fool's Mate scenario in `TestBoard`).

## Quick Start
1. After Java Development Kit (JDK) is installed.
2. Add Guava and JUnit to your project's classpath.
3. Run the `BChess.java` main class.
