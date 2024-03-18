package de.stefanet.javachesskit.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a square on a chessboard.
 */
public class Square {
    private final int x;
    private final int y;

    /**
     * Constructs a Square with given x and y coordinates.
     *
     * @param x The x coordinate, starting with 0 for the a-file
     * @param y The y coordinate, starting with 0 for the first rank
     */
    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a Square from its name.
     *
     * @param name The algebraic notation of the square like "a1".
     * @return The Square object representing the specified square.
     * @throws IllegalArgumentException If the square name is invalid
     */
    public static Square fromName(String name) {
        if (name.length() != 2)
            throw new IllegalArgumentException("Length of square name must be 2");
        if ("abcdefgh".indexOf(name.charAt(0)) == -1)
            throw new IllegalArgumentException("First character of square name must be between a and h");
        if ("12345678".indexOf(name.charAt(1)) == -1)
            throw new IllegalArgumentException("Second character of square name must be between 1 and 8");

        int x = name.charAt(0) - 'a';
        int y = name.charAt(1) - '1';

        return new Square(x, y);
    }

    /**
     * Creates a Square from its 0x88 index.
     *
     * @param index The index of the 0x88 board representation.
     * @return The Square object representing the specified square.
     * @throws IndexOutOfBoundsException If the index is not between 0 and 127
     * @throws IllegalArgumentException If the index is off the board
     * @see <a href="https://www.chessprogramming.org/0x88">0x88 board representation</a> for more details
     */
    public static Square from0x88Index(int index) {
        if (index < 0 || index >= 128)
            throw new IndexOutOfBoundsException("Index must be between 0 and 127, but was " + index);
        if ((index & 0x88) != 0) throw new IllegalArgumentException("Index is off the board");

        int x = index & 7;
        int y = index >> 4;

        return new Square(x, y);
    }

    /**
     * Creates a Square from its rank and file.
     *
     * @param rank The rank of the square, an integer between 1 and 8.
     * @param file The file of the square, a char between 'a' and 'h'
     * @return The Square object representing the specified square.
     * @throws IllegalArgumentException If the rank or file is out off range
     */
    public static Square fromRankAndFile(int rank, char file) {
        if (rank < 1 || rank > 8) throw new IllegalArgumentException("Rank must be between 1 and 8");
        if ("abcdefgh".indexOf(file) == -1) throw new IllegalArgumentException("File must be between a and h");

        int x = file - 'a';
        int y = rank - 1;

        return new Square(x, y);
    }

    /**
     * Gets a list of all squares on the chessboard.
     *
     * @return A list containing all squares.
     */
    public static List<Square> getAll() {
        List<Square> squareList = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Square square = new Square(x, y);
                squareList.add(square);
            }

        }

        return squareList;
    }

    public static Square fromIndex(int index) {
        if (index < 0 || index > 63)
            throw new IndexOutOfBoundsException("Index must be between 0 and 63, but was " + index);

        int x = index / 8;
        int y = index % 8;

        return new Square(x, y);
    }

    /**
     * Gets the x coordinate of the square, starting with 0 for the a-file.
     *
     * @return The x coordinate, an integer between 0 and 7
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate of the square, starting with 0 for the first rank.
     *
     * @return The y coordinate, an integer between 0 and 7
     */
    public int getY() {
        return y;
    }

    /**
     * Checks if the square is dark.
     *
     * @return {@code True} if the square is dark, otherwise {@code false}.
     */
    public boolean isDark() {
        return (this.x + this.y) % 2 == 0;
    }

    /**
     * Checks if the square is light.
     *
     * @return {@code True} if the square is light, otherwise {@code false}.
     */
    public boolean isLight() {
        return !isDark();
    }

    /**
     * Gets the rank of the square.
     *
     * @return The rank, an integer between 1 and 8
     */
    public int getRank() {
        return y + 1;
    }

    /**
     * Gets the file of the square.
     *
     * @return The file, a character between 'a' and 'h'
     */
    public char getFile() {
        return (char) (this.x + 'a');
    }

    /**
     * Gets the algebraic notation of the square.
     *
     * @return The algebraic notation like "a1"
     */
    public String getName() {
        return String.valueOf(getFile()) + getRank();
    }

    /**
     * Gets the 0x88 index of the square.
     *
     * @return The 0x88 index, an integer between 0 and 119
     * @see <a href="https://www.chessprogramming.org/0x88">0x88 board representation</a> for more details
     */
    public int get0x88Index() {
        return this.x + 16 * this.y;
    }

    /**
     * Gets the index of the square.
     * Square a1 has index 0 and square h8 has index 63.
     *
     * @return The index of the square, an integer between 0 and 63
     */
    public int getIndex() {
        return this.x + 8 * this.y;
    }

    /**
     * Checks if the square is on the backrank (1 or 8).
     *
     * @return True if the square is on the backrank, otherwise false.
     */
    public boolean isBackrank() {
        return this.y == 0 || this.y == 7;
    }

    @Override
    public String toString() {
        return "Square.fromName('" + this.getName() + "')";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Square other = (Square) obj;
        return this.getX() == other.getX() && this.getY() == other.getY();
    }

    @Override
    public int hashCode() {
        return this.get0x88Index();
    }

}
