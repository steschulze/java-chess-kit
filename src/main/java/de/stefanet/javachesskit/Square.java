package de.stefanet.javachesskit;

public enum Square {
	A1, B1, C1, D1, E1, F1, G1, H1,
	A2, B2, C2, D2, E2, F2, G2, H2,
	A3, B3, C3, D3, E3, F3, G3, H3,
	A4, B4, C4, D4, E4, F4, G4, H4,
	A5, B5, C5, D5, E5, F5, G5, H5,
	A6, B6, C6, D6, E6, F6, G6, H6,
	A7, B7, C7, D7, E7, F7, G7, H7,
	A8, B8, C8, D8, E8, F8, G8, H8;

	public String getName() {
		return name().toLowerCase();
	}

	public char getFile() {
		return this.name().toLowerCase().charAt(0);
	}

	public int getRank() {
		return Character.getNumericValue(this.name().charAt(1));
	}

	public int getFileIndex() {
		return this.ordinal() & 7;
	}

	public int getRankIndex() {
		return this.ordinal() >> 3;
	}

	public int get0x88Index() {
		return this.getFileIndex() + 16 * this.getRankIndex();
	}

	public boolean isCornerSquare() {
		return this == A1 || this == A8 || this == H1 || this == H8;
	}

	public boolean isBackrank() {
		return getRank() == 1 || getRank() == 8;
	}

	public boolean isDark() {
		return (getRankIndex() + getFileIndex()) % 2 == 0;
	}

	public boolean isLight() {
		return !isLight();
	}

	public Square mirrorVertically() {
		return values()[this.ordinal() ^ 56];
	}

	public Square mirrorHorizontally() {
		return values()[this.ordinal() ^ 7];
	}

	public static Square fromIndex(int index) {
		return values()[index];
	}

	public static Square from0x88Index(int index) {
		if (index < 0 || index >= 128)
			throw new IndexOutOfBoundsException("Index must be between 0 and 127, but was " + index);
		if ((index & 0x88) != 0) throw new IllegalArgumentException("Index is off the board");

		int fileIndex = index & 7;
		int rankIndex = index >> 4;

		return getSquare(fileIndex, rankIndex);
	}

	public static Square parseSquare(String name) {
		return valueOf(name);
	}

	public static Square getSquare(int fileIndex, int rankIndex) {
		if (fileIndex < 0 || fileIndex > 7 || rankIndex < 0 || rankIndex > 7) {
			throw new IllegalArgumentException("Invalid file or rank index");
		}
		return values()[rankIndex * 8 + fileIndex];
	}

	public static Square getSquare(char file, int rank) {
		if (file < 'a' || file > 'h' || rank < 1 || rank > 8) {
			throw new IllegalArgumentException("Invalid file or rank index");
		}
		int index = (rank - 1) * 8 + (file - 'a');
		return values()[index];
	}

	public static int distance(Square square, Square other) {
		return Math.max(
				Math.abs(square.getFileIndex() - other.getFileIndex()),
				Math.abs(square.getRankIndex() - other.getRankIndex()));
	}

	public static int manhattanDistance(Square square, Square other) {
		return Math.abs(square.getFileIndex() - other.getFileIndex()) +
				Math.abs(square.getRankIndex() - other.getRankIndex());
	}

	public static int knightDistance(Square square, Square other) {
		int fileDiff = Math.abs(square.getFileIndex() - other.getFileIndex());
		int rankDiff = Math.abs(square.getRankIndex() - other.getRankIndex());

		if (fileDiff + rankDiff == 1) {
			return 3;
		} else if (fileDiff == rankDiff && fileDiff == 2) {
			return 4;
		} else if (fileDiff == rankDiff && fileDiff == 1) {
			if (square.isCornerSquare() || other.isCornerSquare()) {
				return 4;
			}
		}

		int m = (int) Math.ceil(Math.max(Math.max(fileDiff / 2.0, rankDiff / 2.0), (fileDiff + rankDiff) / 3.0));
		return m + ((m + fileDiff + rankDiff) % 2);
	}

}
