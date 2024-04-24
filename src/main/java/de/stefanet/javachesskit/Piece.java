package de.stefanet.javachesskit;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents chess pieces consisting of a PieceType and a Color.
 */
public class Piece {
    private final PieceType type;
    private final Color color;

    private static final Map<Character, Character> UNICODE_PIECE_SYMBOLS = new HashMap<>();

    static {
        UNICODE_PIECE_SYMBOLS.put('p', '♟');
        UNICODE_PIECE_SYMBOLS.put('n', '♞');
        UNICODE_PIECE_SYMBOLS.put('b', '♝');
        UNICODE_PIECE_SYMBOLS.put('r', '♜');
        UNICODE_PIECE_SYMBOLS.put('q', '♛');
        UNICODE_PIECE_SYMBOLS.put('k', '♚');
        UNICODE_PIECE_SYMBOLS.put('P', '♙');
        UNICODE_PIECE_SYMBOLS.put('N', '♘');
        UNICODE_PIECE_SYMBOLS.put('B', '♗');
        UNICODE_PIECE_SYMBOLS.put('R', '♖');
        UNICODE_PIECE_SYMBOLS.put('Q', '♕');
        UNICODE_PIECE_SYMBOLS.put('K', '♔');
    }

    /**
     * Constructs a Piece based on the given symbol.
     * An uppercase symbol is used for the white pieces, a lowercase symbol for the black pieces.
     *
     * @param symbol The symbol representing the piece.
     * @throws IllegalArgumentException If the symbol is invalid.
     */
    public Piece(char symbol) {
        if ("pnbrkqPNBRKQ".indexOf(symbol) == -1) {
            throw new IllegalArgumentException("Invalid piece symbol: " + symbol);
        }

        char lowerCaseSymbol = Character.toLowerCase(symbol);
        this.type = PieceType.fromSymbol(lowerCaseSymbol);

        this.color = lowerCaseSymbol == symbol ? Color.BLACK : Color.WHITE;
    }

    /**
     * Constructs a Piece with the specified type and color.
     *
     * @param type  The type of the piece.
     * @param color The color of the piece.
     */
    private Piece(PieceType type, Color color) {
        this.type = type;
        this.color = color;
    }

    /**
     * Creates a Piece from the given type and color.
     *
     * @param type  The {@link PieceType} of the piece.
     * @param color The {@link Color} of the piece.
     * @return A Piece object with the specified type and color.
     */
    public static Piece fromTypeAndColor(PieceType type, Color color) {
        return new Piece(type, color);
    }

    /**
     * Gets the type of the piece.
     *
     * @return The type of the piece.
     */
    public PieceType getType() {
        return type;
    }

    /**
     * Gets the color of the piece.
     *
     * @return The color of the piece.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the symbol representing the piece.
     *
     * @return The symbol representing the piece.
     */
    public char getSymbol() {
        return color == Color.WHITE ? Character.toUpperCase(type.getSymbol()) : type.getSymbol();
    }

    @Override
    public String toString() {
        return String.format("Piece.parseSymbol('%s')", getSymbol());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Piece piece = (Piece) o;
        return getSymbol() == piece.getSymbol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getColor());
    }

    public char getUnicodeSymbol(boolean invertColor) {
        char symbol = (invertColor ^ color == Color.WHITE) ? Character.toUpperCase(type.getSymbol()) : type.getSymbol();
        return UNICODE_PIECE_SYMBOLS.get(symbol);
    }
}
