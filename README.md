# java-chess-kit: a chess library for Java

The java-chess-kit is a simple java chess library for generating legal moves, checking the state of the game
and parsing a PGN or FEN.

## Installing from source

```
git clone https://github.com/Stefan3004/java-chess-kit.git
cd java-chess-kit/
gradlew build
```

## Usage

### Scholar's mate

```
Board board = new Board();

board.pushSan("e4");
board.pushSan("e5");
board.pushSan("Qh5");
board.pushSan("Nc6");
board.pushSan("Bc4");
board.pushSan("Nf6");
board.pushSan("Qxf7");

System.out.println(board.outcome());
```

Output: `Outcome{termination=CHECKMATE, winner=WHITE}`

### Making moves

There are several options for making moves:

1. `board.pushSan("e4")`
2. `board.pushUci("e2e4")`
3. `board.push(Move.fromUci("e2e4"))`
4. `board.push(new Move(Square.E2, Square.E4))`

For unmaking moves use `board.pop()`. This method also returns the last move.

### Parsing and creating FENs

There are two options for FEN parsing:

1. Constructor `new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2")`
2. Setter `board.setFen("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2")`

For getting the FEN use `board.getFen()`.

Example:

```
Board board = new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");
String fen = board.getFen();
System.out.println(fen);
```

Output: `rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2`

## License

This project is licensed under the terms of the GPL 3 license. Check out `LICENSE.txt` for the full text.