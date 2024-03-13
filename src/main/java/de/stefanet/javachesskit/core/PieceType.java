package de.stefanet.javachesskit.core;

import java.util.Arrays;
import java.util.List;

public enum PieceType {
    PAWN('p'), KNIGHT('n'), BISHOP('b'), ROOK('r'), QUEEN('q'), KING('k');

    private final char symbol;

    PieceType(char symbol) {
        this.symbol = symbol;
    }

    public static PieceType fromSymbol(char symbol) {
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

    public static List<PieceType> promotionTypes() {
        return Arrays.asList(ROOK, KNIGHT, BISHOP, QUEEN);
    }

    public char getSymbol() {
        return symbol;
    }
}
