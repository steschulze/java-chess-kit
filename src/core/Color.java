package core;

public enum Color {
    WHITE, BLACK;

    Color other(){
        if (this == WHITE) return BLACK;
        else return WHITE;
    }

    String shortName(){
        return name().toLowerCase().substring(0, 1);
    }

    String fullName(){
        return name().toLowerCase();
    }

    static Color fromSymbol(char symbol){
        if(symbol == 'w') return WHITE;
        else if (symbol == 'b') return BLACK;
        else throw new IllegalArgumentException("No Color for symbol " + symbol);
    }

    public int forwardDirection() {
        if (this == WHITE) return 1;
        else return -1;
    }
}
