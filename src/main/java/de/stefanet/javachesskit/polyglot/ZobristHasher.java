package de.stefanet.javachesskit.polyglot;

import de.stefanet.javachesskit.BaseBoard;
import de.stefanet.javachesskit.Board;
import de.stefanet.javachesskit.bitboard.Bitboard;
import de.stefanet.javachesskit.bitboard.BitboardUtils;
import de.stefanet.javachesskit.core.Color;
import de.stefanet.javachesskit.core.Piece;
import de.stefanet.javachesskit.core.Square;

/**
 * Class for hashing chess positions using the Zobrist hashing algorithm.
 */
public class ZobristHasher {
    private static final int SIZE = 781;
    private final long[] randomArray;

    /**
     * Creates a new ZobristHasher with a given seed.
     *
     * @param seed The seed to use for generating random numbers.
     */
    public ZobristHasher(int seed) {
        PseudoRandomNumberGenerator prng = new PseudoRandomNumberGenerator(seed);
        this.randomArray = new long[SIZE];

        for (int i = 0; i < SIZE; i++) {
            this.randomArray[i] = prng.rand64();
        }
    }

    public ZobristHasher() {
        this.randomArray = Polyglot.POLYGLOT_RANDOM_ARRAY;
    }

    private long hashBoard(BaseBoard board) {
        long hash = 0;

        int[] occupiedIndices = BitboardUtils.scanReversed(board.getOccupied());
        for (int squareIndex : occupiedIndices) {
            Square square = Square.fromIndex(squareIndex);
            Piece piece = board.pieceAt(square);
            int index = piece.getType().ordinal() * 2 + piece.getColor().other().ordinal();
            hash ^= this.randomArray[64 * index + squareIndex];
        }

        return hash;
    }

    private long hashCastling(Board board) {
        long hash = 0;

        if (board.hasKingsideCastlingRights(Color.WHITE)) {
            hash ^= this.randomArray[768];
        }

        if (board.hasQueensideCastlingRights(Color.WHITE)) {
            hash ^= this.randomArray[769];
        }

        if (board.hasKingsideCastlingRights(Color.BLACK)) {
            hash ^= this.randomArray[770];
        }

        if (board.hasQueensideCastlingRights(Color.BLACK)) {
            hash ^= this.randomArray[771];
        }
        return hash;
    }

    private long hashEpSquare(Board board) {
        if (board.getEpSquare() != null) {
            long epMask;
            if (board.getTurn() == Color.WHITE) {
                epMask = BitboardUtils.shiftDown(Bitboard.SQUARES[board.getEpSquare().ordinal()]);
            } else {
                epMask = BitboardUtils.shiftUp(Bitboard.SQUARES[board.getEpSquare().ordinal()]);
            }

            epMask = BitboardUtils.shiftLeft(epMask) | BitboardUtils.shiftRight(epMask);
            long colorMask = board.getTurn() == Color.WHITE ? board.getWhitePieces() : board.getBlackPieces();

            if ((epMask & board.getPawns() & colorMask) != 0) {
                return this.randomArray[772 + board.getEpSquare().getFileIndex()];
            }
        }
        return 0;
    }

    private long hashTurn(Board board) {
        return board.getTurn() == Color.WHITE ? this.randomArray[780] : 0;
    }

    public long hash(Board board) {
        return this.hashBoard(board) ^ this.hashCastling(board) ^ this.hashEpSquare(board) ^ this.hashTurn(board);
    }
}
