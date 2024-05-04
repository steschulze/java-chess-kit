package de.stefanet.javachesskit;

import java.util.Iterator;

/**
 * A generator for legal moves.
 *
 * <p>This class is iterable and can be used in a for-each loop.
 * Example:
 * <pre>
 * {@code
 * LegalMoveGenerator generator = new LegalMoveGenerator(board);
 * for (Move move : generator) {
 *     System.out.println(move);
 * }
 * }
 * </pre>
 */
public class LegalMoveGenerator implements Iterable<Move> {
    private final Board board;

    /**
     * Create a new legal move generator for the given board.
     *
     * @param board The board to generate legal moves for.
     */
    public LegalMoveGenerator(Board board) {
        this.board = board;
    }

    @Override
    public Iterator<Move> iterator() {
        return board.generateLegalMoves().iterator();
    }

    /**
     * Check if the given object is a legal move.
     *
     * @param obj The object to check.
     * @return True if the object is a legal move, false otherwise.
     */
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

    /**
     * Count the number of legal moves.
     *
     * @return The number of legal moves.
     */
    public int count() {
        int count = 0;
        for (Move move : this) {
            count++;
        }
        return count;
    }
}
