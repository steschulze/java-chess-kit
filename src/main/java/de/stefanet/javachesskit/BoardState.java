package de.stefanet.javachesskit;

import de.stefanet.javachesskit.core.Color;
import de.stefanet.javachesskit.core.Square;
import java.util.Arrays;

/**
 * BoardState is a snapshot of the board state.
 */
public class BoardState {
    private final long pawns;
    private final long knights;
    private final long bishops;
    private final long rooks;
    private final long queens;
    private final long kings;

    private final long promoted;
    private final long[] occupiedColor;
    protected final long occupied;

    private final Color turn;
    private final long castlingRights;

    private final Square epSquare;

    private final int fullMoveNumber;
    private final int halfMoveClock;

    /**
     * Create a snapshot of the board state.
     *
     * @param board the board to snapshot
     */
    public BoardState(Board board) {
        this.pawns = board.pawns;
        this.knights = board.knights;
        this.bishops = board.bishops;
        this.rooks = board.rooks;
        this.queens = board.queens;
        this.kings = board.kings;

        this.occupied = board.occupied;
        this.occupiedColor = Arrays.copyOf(board.occupiedColor, 2);

        this.promoted = board.promoted;
        this.turn = board.turn;
        this.castlingRights = board.castlingRights;
        this.epSquare = board.epSquare;
        this.halfMoveClock = board.halfMoveClock;
        this.fullMoveNumber = board.fullMoveNumber;
    }

    /**
     * Restore the board state.
     *
     * @param board the board to restore
     */
    public void restore(Board board) {
        board.pawns = this.pawns;
        board.knights = this.knights;
        board.bishops = this.bishops;
        board.rooks = this.rooks;
        board.queens = this.queens;
        board.kings = this.kings;

        board.occupied = this.occupied;
        board.occupiedColor = Arrays.copyOf(this.occupiedColor, 2);

        board.promoted = this.promoted;
        board.turn = this.turn;
        board.castlingRights = this.castlingRights;
        board.epSquare = this.epSquare;
        board.halfMoveClock = this.halfMoveClock;
        board.fullMoveNumber = this.fullMoveNumber;
    }
}
