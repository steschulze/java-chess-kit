package de.stefanet.javachesskit;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representing the type of chess pieces.
 * <p>
 * There are 6 different types: Pawn, Knight, Bishop, Rook, Queen and King
 */
public enum PieceType {
    PAWN('p'), KNIGHT('n'), BISHOP('b'), ROOK('r'), QUEEN('q'), KING('k');

    private final char symbol;

    /**
     * Constructs a PieceType with the specified symbol.
     *
     * @param symbol The symbol representing the piece type.
     */
    PieceType(char symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the PieceType corresponding to the given symbol.
     *
     * @param symbol The symbol representing the piece type.
     * @return The PieceType corresponding to the given symbol.
     * @throws IllegalArgumentException If the symbol does not correspond to any piece type.
     */
    public static PieceType fromSymbol(char symbol) {
        symbol = Character.toLowerCase(symbol);
        switch (symbol) {
            case 'p':
                return PAWN;
            case 'r':
                return ROOK;
            case 'n':
                return KNIGHT;
            case 'b':
                return BISHOP;
            case 'q':
                return QUEEN;
            case 'k':
                return KING;
            default:
                throw new IllegalArgumentException("No piece with symbol " + symbol);
        }
    }

    /**
     * Returns a list of piece types available for promotion of pawns.
     * <p>
     * There are 4 different promotion types:
     * <ul>
     *     <li>Rook</li>
     *     <li>Knight</li>
     *     <li>Bishop</li>
     *     <li>Queen</li>
     * </ul>
     *
     * @return A list containing piece types available for promotion.
     */
    public static List<PieceType> promotionTypes() {
        return Arrays.asList(ROOK, KNIGHT, BISHOP, QUEEN);
    }

    /**
     * Gets the symbol representing the piece type.
     *
     * @return The symbol representing the piece type.
     */
    public char getSymbol() {
        return symbol;
    }
}
