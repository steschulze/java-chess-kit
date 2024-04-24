package de.stefanet.javachesskit;

/**
 * Enum representing the color of chess pieces. This can be either WHITE or BLACK.
 */
public enum Color {
    WHITE, BLACK;

    /**
     * Gets the color from the symbol.
     *
     * @param symbol The symbol representing the color. It must be either 'w' for WHITE or 'b' for BLACK.
     * @return The corresponding color.
     * @throws IllegalArgumentException If the symbol is neither 'w' nor 'b'.
     */
    public static Color fromSymbol(char symbol) {
        if (symbol == 'w') {
            return WHITE;
        } else if (symbol == 'b') {
            return BLACK;
        } else {
            throw new IllegalArgumentException("No Color for symbol " + symbol);
        }
    }

    /**
     * Gets the color from a boolean value.
     *
     * @param b The boolean value
     * @return White if b is true, otherwise Black
     */
    public static Color fromBoolean(boolean b) {
        return b ? WHITE : BLACK;
    }

    /**
     * Returns the opposite color
     * @return WHITE if the color is BLACK, otherwise BLACK
     */
    public Color other() {
        if (this == WHITE) {
            return BLACK;
        } else {
            return WHITE;
        }
    }

    /**
     * Returns the symbol of the color ('w' or 'b').
     * @return The symbol of the color.
     */
    public char getSymbol() {
        return this == WHITE ? 'w' : 'b';
    }

    /**
     * Returns the full name of the color in lowercase ('white' or 'black').
     * @return The full color name.
     */
    public String fullName() {
        return name().toLowerCase();
    }

    /**
     * Returns an integer indicating the moving direction of the pawns.
     * @return 1 for WHITE, -1 for BLACK.
     */
    public int forwardDirection() {
        if (this == WHITE) {
            return 1;
        } else {
            return -1;
        }
    }
}
