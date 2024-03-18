package de.stefanet.javachesskit.board0x88;

public class ZobristHasher {
	private long[] randomArray;
	private static int SIZE = 781;

	public ZobristHasher(int seed) {
		PRNG prng = new PRNG(seed);
		this.randomArray = new long[SIZE];

		for (int i = 0; i < SIZE; i++) {
			this.randomArray[i] = prng.rand64();
		}
	}

	public ZobristHasher() {
		this.randomArray = BoardUtility.POLYGLOT_RANDOM;
	}

	public long hashPosition(Position position) {
		long key = 0;

		for (Square square : Square.getAll()) {
			Piece piece = position.get(square);

			if (piece != null) {
				int pieceIndex = "pPnNbBrRqQkK".indexOf(piece.getSymbol());
				key ^= randomArray[64 * pieceIndex + 8 * square.getY() + square.getX()];
			}
		}

		if (position.getCastlingRight('K')) {
			key ^= randomArray[768];
		}
		if (position.getCastlingRight('Q')) {
			key ^= randomArray[769];
		}
		if (position.getCastlingRight('k')) {
			key ^= randomArray[770];
		}
		if (position.getCastlingRight('q')) {
			key ^= randomArray[771];
		}

		if (position.getEpFile() != null && position.checkEnPassant(position.getEpFile())) {
			key ^= randomArray[772 + position.getEpFile() - 'a'];
		}

		if (position.getTurn() == Color.WHITE) {
			key ^= randomArray[780];
		}

		return key;
	}
}
