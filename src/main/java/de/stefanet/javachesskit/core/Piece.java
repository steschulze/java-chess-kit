package de.stefanet.javachesskit.core;


import java.util.Objects;

public class Piece {
    private final PieceType type;
    private final Color color;

    public Piece(char symbol) {
        if ("pnbrkqPNBRKQ".indexOf(symbol) == -1) {
            throw new IllegalArgumentException("Invalid piece symbol: " + symbol);
        }

        char lowerCaseSymbol = Character.toLowerCase(symbol);
        this.type = PieceType.fromSymbol(lowerCaseSymbol);

        this.color = lowerCaseSymbol == symbol ? Color.BLACK : Color.WHITE;
    }

    private Piece(PieceType type, Color color) {
        this.type = type;
        this.color = color;
    }

    public static Piece fromTypeAndColor(PieceType type, Color color) {
        return new Piece(type, color);
    }

    public PieceType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public char getSymbol() {
        return color == Color.WHITE ? Character.toUpperCase(type.getSymbol()) : type.getSymbol();
    }

    @Override
    public String toString() {
        return String.format("Piece.parseSymbol('%s')", getSymbol());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Piece piece = (Piece) o;
        return getSymbol() == piece.getSymbol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getColor());
    }
}
