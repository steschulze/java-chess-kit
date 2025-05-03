package de.stefanet.javachesskit.move;

import de.stefanet.javachesskit.Board;
import java.util.Iterator;

/**
 * A generator for pseudo-legal moves.
 *
 * <p>This class is iterable and can be used in a for-each loop.
 * Example:
 * <pre>
 * {@code
 * PseudoLegalMoveGenerator generator = new PseudoLegalMoveGenerator(board);
 * for (Move move : generator) {
 *     System.out.println(move);
 * }
 * }
 * </pre>
 */
public class PseudoLegalMoveGenerator implements Iterable<Move> {
    private final Board board;

    /**
     * Create a new pseudo-legal move generator for the given board.
     *
     * @param board The board to generate pseudo-legal moves for.
     */
    public PseudoLegalMoveGenerator(Board board) {
        this.board = board;
    }

    @Override
    public Iterator<Move> iterator() {
        return board.generatePseudoLegalMoves().iterator();
    }

    /**
     * Check if the given object is a pseudo-legal move.
     *
     * @param obj The object to check.
     * @return True if the object is a pseudo-legal move, false otherwise.
     */
    public boolean contains(Object obj) {
        if (!(obj instanceof Move)) {
            return false;
        }
        Move move = (Move) obj;
        return board.isPseudoLegal(move);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Move move : this) {
            if (board.isLegal(move)) {
                sb.append(board.san(move));
            } else {
                sb.append(move.getUciMove());
            }
            sb.append(", ");
        }
        return String.format("<PseudoLegalMoveGenerator (%s)>", sb);
    }

    /**
     * Check if there are any pseudo-legal moves.
     *
     * @return True if there is at least one pseudo-legal move, false otherwise.
     */
    public boolean any() {
        return iterator().hasNext();
    }

    /**
     * Count the number of pseudo-legal moves.
     *
     * @return The number of pseudo-legal moves.
     */
    public int count() {
        int count = 0;
        for (Move ignored : this) {
            count++;
        }
        return count;
    }
}
