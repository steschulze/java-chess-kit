package de.stefanet.javachesskit.core;

public class MoveInfo {
    private Move move;
    private Piece movedPiece;
    private Piece capturedPiece;
    private String san;
    private boolean isEnpassant;
    private boolean isKingSideCastle;
    private boolean isQueenSideCastle;
    private boolean isCheck;
    private boolean isCheckmate;

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

    public Move getMove() {
        return move;
    }

    public Piece getMovedPiece() {
        return movedPiece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public String getSan() {
        return san;
    }

    public boolean isEnpassant() {
        return isEnpassant;
    }

    public boolean isKingSideCastle() {
        return isKingSideCastle;
    }

    public boolean isQueenSideCastle() {
        return isQueenSideCastle;
    }

    public boolean isCheck() {
        return isCheck;
    }

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
}
