package de.stefanet.javachesskit.core;

/**
 * Enum representing the squares on a chessboard.
 *
 * <p>The squares are named from A1 to H8. The squares are numbered from 0 to 63, where A1 is 0 and H8 is 63.
 */
public enum Square {
    A1, B1, C1, D1, E1, F1, G1, H1,
    A2, B2, C2, D2, E2, F2, G2, H2,
    A3, B3, C3, D3, E3, F3, G3, H3,
    A4, B4, C4, D4, E4, F4, G4, H4,
    A5, B5, C5, D5, E5, F5, G5, H5,
    A6, B6, C6, D6, E6, F6, G6, H6,
    A7, B7, C7, D7, E7, F7, G7, H7,
    A8, B8, C8, D8, E8, F8, G8, H8;

    /**
     * Gets the name of the square in lowercase.
     *
     * @return The name of the square in lowercase.
     */
    public String getName() {
        return name().toLowerCase();
    }

    /**
     * Gets the file of the square from 'a' to 'h'.
     *
     * @return The file of the square.
     */
    public char getFile() {
        return this.name().toLowerCase().charAt(0);
    }

    /**
     * Gets the rank of the square from 1 to 8.
     *
     * @return The rank of the square.
     */
    public int getRank() {
        return Character.getNumericValue(this.name().charAt(1));
    }

    /**
     * Gets the file index of the square from 0 to 7.
     *
     * @return The file index of the square.
     */
    public int getFileIndex() {
        return this.ordinal() & 7;
    }

    /**
     * Gets the rank index of the square from 0 to 7.
     *
     * @return The rank index of the square.
     */
    public int getRankIndex() {
        return this.ordinal() >> 3;
    }

    /**
     * Gets the index of the square in 0x88 board representation.
     *
     * @return The index of the square.
     */
    public int get0x88Index() {
        return this.getFileIndex() + 16 * this.getRankIndex();
    }

    /**
     * Checks if the square is a corner square (A1, A8, H1, H8).
     *
     * @return True if the square is a corner square, false otherwise.
     */
    public boolean isCornerSquare() {
        return this == A1 || this == A8 || this == H1 || this == H8;
    }

    /**
     * Checks if the square is a backrank square (rank 1 or 8).
     *
     * @return True if the square is a backrank square, false otherwise.
     */
    public boolean isBackrank() {
        return getRank() == 1 || getRank() == 8;
    }

    /**
     * Checks if the square is a dark square.
     *
     * @return True if the square is a dark square, false otherwise.
     */
    public boolean isDark() {
        return (getRankIndex() + getFileIndex()) % 2 == 0;
    }

    /**
     * Checks if the square is a light square.
     *
     * @return True if the square is a light square, false otherwise.
     */
    public boolean isLight() {
        return !isDark();
    }

    /**
     * Gets the square that is mirrored vertically.
     *
     * @return The square that is mirrored vertically.
     */
    public Square mirrorVertically() {
        return values()[this.ordinal() ^ 56];
    }

    /**
     * Gets the square that is mirrored horizontally.
     *
     * @return The square that is mirrored horizontally.
     */
    public Square mirrorHorizontally() {
        return values()[this.ordinal() ^ 7];
    }

    /**
     * Gets the square by index.
     *
     * @param index The index of the square.
     * @return The square with the given index.
     */
    public static Square fromIndex(int index) {
        return values()[index];
    }

    /**
     * Gets the square by 0x88 index.
     *
     * @param index The 0x88 index of the square.
     * @return The square with the given 0x88 index.
     */
    public static Square from0x88Index(int index) {
        if (index < 0 || index >= 128) {
            throw new IndexOutOfBoundsException("Index must be between 0 and 127, but was " + index);
        }
        if ((index & 0x88) != 0) {
            throw new IllegalArgumentException("Index is off the board");
        }

        int fileIndex = index & 7;
        int rankIndex = index >> 4;

        return getSquare(fileIndex, rankIndex);
    }

    /**
     * Gets the square by name.
     *
     * <p>The name is case-insensitive.
     *
     * @param name The name of the square.
     * @return The square with the given name.
     * @throws IllegalArgumentException if there is no square with the specified name
     */
    public static Square parseSquare(String name) {
        return valueOf(name.toUpperCase());
    }

    /**
     * Gets the square by file and rank index.
     *
     * @param fileIndex The file index of the square from 0 to 7.
     * @param rankIndex The rank index of the square from 0 to 7.
     * @return The square with the given file and rank index.
     * @throws IllegalArgumentException if the file or rank index is invalid
     */
    public static Square getSquare(int fileIndex, int rankIndex) {
        if (fileIndex < 0 || fileIndex > 7 || rankIndex < 0 || rankIndex > 7) {
            throw new IllegalArgumentException("Invalid file or rank index");
        }
        return values()[rankIndex * 8 + fileIndex];
    }

    /**
     * Gets the square by file and rank.
     *
     * @param file The file of the square from 'a' to 'h'.
     * @param rank The rank of the square from 1 to 8.
     * @return The square with the given file and rank.
     * @throws IllegalArgumentException if the file or rank is invalid
     */
    public static Square getSquare(char file, int rank) {
        if (file < 'a' || file > 'h' || rank < 1 || rank > 8) {
            throw new IllegalArgumentException("Invalid file or rank");
        }
        int index = (rank - 1) * 8 + (file - 'a');
        return values()[index];
    }

    /**
     * Gets the Chebyshev distance (number of king steps) between two squares.
     *
     * @param square The first square.
     * @param other  The second square.
     * @return The Chebyshev distance between the two squares.
     */
    public static int distance(Square square, Square other) {
        return Math.max(
                Math.abs(square.getFileIndex() - other.getFileIndex()),
                Math.abs(square.getRankIndex() - other.getRankIndex()));
    }

    /**
     * Gets the Manhattan distance (number of orthogonal king steps) between two squares.
     *
     * @param square The first square.
     * @param other  The second square.
     * @return The Manhattan distance between the two squares.
     */
    public static int manhattanDistance(Square square, Square other) {
        return Math.abs(square.getFileIndex() - other.getFileIndex()) +
               Math.abs(square.getRankIndex() - other.getRankIndex());
    }

    /**
     * Gets the Knight distance (number of knight moves) between two squares.
     *
     * @param square The first square.
     * @param other  The second square.
     * @return The Knight distance between the two squares.
     */
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
