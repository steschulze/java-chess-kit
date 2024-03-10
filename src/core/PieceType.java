package core;

public enum PieceType {
    PAWN('p'), ROOK('r'), KNIGHT('n'), BISHOP('b'), QUEEN('q'), KING('k');

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

    public char getSymbol() {
        return symbol;
    }
}
