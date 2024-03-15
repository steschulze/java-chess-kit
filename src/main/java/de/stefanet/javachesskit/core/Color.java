package de.stefanet.javachesskit.core;

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
    static Color fromSymbol(char symbol) {
        if (symbol == 'w') return WHITE;
        else if (symbol == 'b') return BLACK;
        else throw new IllegalArgumentException("No Color for symbol " + symbol);
    }

    /**
     * Returns the opposite color
     * @return WHITE if the color is BLACK, otherwise BLACK
     */
    Color other() {
        if (this == WHITE) return BLACK;
        else return WHITE;
    }

    /**
     * Returns the short name of the color ('w' or 'b').
     * @return The short color name.
     */
    String shortName() {
        return name().toLowerCase().substring(0, 1);
    }

    /**
     * Returns the full name of the color in lowercase ('white' or 'black').
     * @return The full color name.
     */
    String fullName() {
        return name().toLowerCase();
    }

    /**
     * Returns an integer indicating the moving direction of the pawns.
     * @return 1 for WHITE, -1 for BLACK.
     */
    public int forwardDirection() {
        if (this == WHITE) return 1;
        else return -1;
    }
}
