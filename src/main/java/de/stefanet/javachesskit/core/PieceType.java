package de.stefanet.javachesskit.core;

public enum PieceType {
    PAWN('p'), KNIGHT('n'), BISHOP('b'), ROOK('r'),QUEEN('q'), KING('k');

    private char symbol;
    PieceType(char symbol) {
        this.symbol = symbol;
    }

    public static PieceType fromSymbol(char symbol) {
        switch (symbol){
            case 'p' : return PAWN;
            case 'r' : return ROOK;
            case 'n' : return KNIGHT;
            case 'b' : return BISHOP;
            case 'q' : return QUEEN;
            case 'k': return KING;
            default: throw new IllegalArgumentException("No piece with symbol " + symbol);
        }
    }

    public static PieceType[] promotionTypes(){
        return new PieceType[]{ROOK, KNIGHT, BISHOP, QUEEN};
    }

    public char getSymbol() {
        return symbol;
    }
}
