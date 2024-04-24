package de.stefanet.javachesskit;

import java.util.Iterator;

public class LegalMoveGenerator implements Iterable<Move> {
    private final Board board;

    public LegalMoveGenerator(Board board) {
        this.board = board;
    }

    @Override
    public Iterator<Move> iterator() {
        return board.generateLegalMoves().iterator();
    }

    public boolean contains(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }
        Move move = (Move) obj;
        return board.isLegal(move);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Move move : this) {
            sb.append(board.san(move)).append(", ");
        }
        return String.format("<LegalMoveGenerator (%s)>", sb);
    }

    public boolean any() {
        return iterator().hasNext();
    }

    public int count() {
        int count = 0;
        for (Move move : this) {
            count++;
        }
        return count;
    }
}
