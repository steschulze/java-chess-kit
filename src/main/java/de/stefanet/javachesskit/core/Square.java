package de.stefanet.javachesskit.core;

import java.util.ArrayList;
import java.util.List;

public class Square {
    private int x;
    private int y;

    public Square(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDark(){
        return (this.x + this.y) % 2 == 0;
    }

    public boolean isLight(){
        return !isDark();
    }

    public int getRank(){
        return y + 1;
    }

    public char getFile(){
        return (char) (this.x + 'a');
    }

    public String getName(){
        return String.valueOf(getFile()) + String.valueOf(getRank());
    }

    public int get0x88Index(){
        return this.x + 16 * this.y;
    }

    public boolean isBackrank(){
        return this.y == 0 || this.y == 7;
    }

    @Override
    public String toString() {
        return "Square.fromName('" + this.getName() + "')";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Square other = (Square) obj;
        return this.getX() == other.getX() && this.getY() == other.getY();
    }

    public static Square fromName(String name) {
        assert name.length() == 2;
        assert "abcdefgh".indexOf(name.charAt(0)) != -1;
        assert "12345678".indexOf(name.charAt(1)) != -1;

        int x = name.charAt(0) - 'a';
        int y = name.charAt(1) - '1';

        return new Square(x, y);
    }

    public static Square from0x88Index(int index) {
        assert index > 0 && index <= 128;
        assert (index & 0x88) == 0;

        int x = index & 7;
        int y = index >> 4;

        return new Square(x, y);
    }

    public static Square fromRankAndFile(int rank, char file) {
        assert rank >= 1 && rank <= 8;
        assert "abcdefgh".indexOf(file) != -1;

        int x = file - 'a';
        int y = rank - 1;

        return new Square(x, y);
    }

    public static List<Square> getAll(){
        List<Square> squareList = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Square square = new Square(x, y);
                squareList.add(square);
            }

        }

        return squareList;
    }

}
