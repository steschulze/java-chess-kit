package core;

public enum Color {
    WHITE, BLACK;

    Color other(){
        if (this == WHITE) return BLACK;
        else return WHITE;
    }
}
