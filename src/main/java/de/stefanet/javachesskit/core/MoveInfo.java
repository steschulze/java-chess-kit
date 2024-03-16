package de.stefanet.javachesskit.core;

import java.util.Objects;

/**
 * Represents additional information about a chess move.
 */
public class MoveInfo {
    private final Move move;
    private final Piece movedPiece;
    private final Piece capturedPiece;
    private final String san;
    private final boolean isEnpassant;
    private final boolean isKingSideCastle;
    private final boolean isQueenSideCastle;
    private final boolean isCheck;
    private final boolean isCheckmate;

    /**
     * Constructs a MoveInfo with the specified parameters.
     *
     * @param move              The move.
     * @param movedPiece        The piece that was moved.
     * @param capturedPiece     The piece that was captured (if any).
     * @param san               The standard algebraic notation (SAN) of the move.
     * @param isEnpassant       Whether the move is an en passant capture.
     * @param isKingSideCastle  Whether the move is a kingside castle.
     * @param isQueenSideCastle Whether the move is a queenside castle.
     * @param isCheck           Whether the move puts the opponent's king in check.
     * @param isCheckmate       Whether the move results in checkmate.
     */
    public MoveInfo(Move move, Piece movedPiece, Piece capturedPiece, String san, boolean isEnpassant, boolean isKingSideCastle, boolean isQueenSideCastle, boolean isCheck, boolean isCheckmate) {
        this.move = move;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.san = san;
        this.isEnpassant = isEnpassant;
        this.isKingSideCastle = isKingSideCastle;
        this.isQueenSideCastle = isQueenSideCastle;
        this.isCheck = isCheck;
        this.isCheckmate = isCheckmate;
    }

    /**
     * Gets the move associated with this MoveInfo.
     *
     * @return The move.
     */
    public Move getMove() {
        return move;
    }

    /**
     * Gets the piece that was moved.
     *
     * @return The moved piece.
     */
    public Piece getMovedPiece() {
        return movedPiece;
    }

    /**
     * Gets the piece that was captured (if any).
     *
     * @return The captured piece, or null if no piece was captured.
     */
    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    /**
     * Gets the standard algebraic notation (SAN) of the move.
     *
     * @return The SAN of the move.
     * @see <a href="https://www.chessprogramming.org/Algebraic_Chess_Notation">SAN</a>
     */
    public String getSan() {
        return san;
    }

    /**
     * Checks if the move is an en passant capture.
     *
     * @return true if the move is an en passant capture, false otherwise.
     */
    public boolean isEnpassant() {
        return isEnpassant;
    }

    /**
     * Checks if the move is a kingside castle.
     *
     * @return true if the move is a kingside castle, false otherwise.
     */
    public boolean isKingSideCastle() {
        return isKingSideCastle;
    }

    /**
     * Checks if the move is a queenside castle.
     *
     * @return true if the move is a queenside castle, false otherwise.
     */
    public boolean isQueenSideCastle() {
        return isQueenSideCastle;
    }

    /**
     * Checks if the move is a castle (either kingside or queenside).
     *
     * @return true if the move is a castle, false otherwise.
     */
    public boolean isCastle() {
        return isKingSideCastle || isQueenSideCastle;
    }

    /**
     * Checks if the move puts the opponent's king in check.
     *
     * @return true if the move puts the opponent's king in check, false otherwise.
     */
    public boolean isCheck() {
        return isCheck;
    }

    /**
     * Checks if the move results in checkmate.
     *
     * @return true if the move results in checkmate, false otherwise.
     */
    public boolean isCheckmate() {
        return isCheckmate;
    }

    @Override
    public String toString() {
        return "MoveInfo{" +
                "move=" + move +
                ", movedPiece=" + movedPiece +
                ", capturedPiece=" + capturedPiece +
                ", san='" + san + '\'' +
                ", isEnpassant=" + isEnpassant +
                ", isKingSideCastle=" + isKingSideCastle +
                ", isQueenSideCastle=" + isQueenSideCastle +
                ", isCheck=" + isCheck +
                ", isCheckmate=" + isCheckmate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoveInfo moveInfo = (MoveInfo) o;
        return isEnpassant() == moveInfo.isEnpassant()
                && isKingSideCastle() == moveInfo.isKingSideCastle()
                && isQueenSideCastle() == moveInfo.isQueenSideCastle()
                && isCheck() == moveInfo.isCheck()
                && isCheckmate() == moveInfo.isCheckmate()
                && Objects.equals(getMove(), moveInfo.getMove())
                && Objects.equals(getMovedPiece(), moveInfo.getMovedPiece())
                && Objects.equals(getCapturedPiece(), moveInfo.getCapturedPiece())
                && Objects.equals(getSan(), moveInfo.getSan());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMove(), getMovedPiece(), getCapturedPiece(), getSan(), isEnpassant(), isKingSideCastle(), isQueenSideCastle(), isCheck(), isCheckmate());
    }
}
