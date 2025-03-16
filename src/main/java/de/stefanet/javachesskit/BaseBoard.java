package de.stefanet.javachesskit;

import static de.stefanet.javachesskit.bitboard.Bitboard.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_2;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_7;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_8;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.*;

import de.stefanet.javachesskit.bitboard.Bitboard;
import de.stefanet.javachesskit.bitboard.BitboardUtils;
import de.stefanet.javachesskit.core.Color;
import de.stefanet.javachesskit.core.Piece;
import de.stefanet.javachesskit.core.PieceType;
import de.stefanet.javachesskit.core.Square;
import de.stefanet.javachesskit.core.SquareSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Board representing the position of chess pieces.
 *
 * <p>The board is initialized with the standard chess starting position,
 * unless otherwise specified in the board fen.
 * If the board fen is null, an empty board is created.
 *
 * @see Board
 */
public class BaseBoard {
    private static final String STARTING_BOARD_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    protected long pawns;
    protected long knights;
    protected long bishops;
    protected long rooks;
    protected long queens;
    protected long kings;

    protected long promoted;
    protected long[] occupiedColor = new long[2];
    protected long occupied;

    /**
     * Create a new board with the standard starting position.
     */
    public BaseBoard() {
        this(STARTING_BOARD_FEN);
    }

    /**
     * Create a new board with the given board fen.
     *
     * @param fen The board fen.
     */
    public BaseBoard(String fen) {
        if (fen == null) {
            this.clearBitboards();
        } else if (fen.equals(STARTING_BOARD_FEN)) {
            resetBoard();
        } else {
            setBoardFen(fen);
        }
    }

    /**
     * Clears the board.
     */
    protected void clearBoard() {
        clearBitboards();
    }

    /**
     * Clears all bitboards.
     */
    protected void clearBitboards() {
        this.pawns = 0;
        this.knights = 0;
        this.bishops = 0;
        this.rooks = 0;
        this.queens = 0;
        this.kings = 0;

        this.promoted = 0;

        this.occupiedColor = new long[2];
        this.occupied = 0;
    }

    /**
     * Resets the board to the standard starting position.
     */
    protected void resetBoard() {
        this.pawns = RANK_2 | RANK_7;
        this.knights = B1 | G1 | B8 | G8;
        this.bishops = C1 | F1 | C8 | F8;
        this.rooks = A1 | H1 | A8 | H8;
        this.queens = D1 | D8;
        this.kings = E1 | E8;

        this.promoted = 0;

        this.occupiedColor[Color.WHITE.ordinal()] = RANK_1 | RANK_2;
        this.occupiedColor[Color.BLACK.ordinal()] = RANK_7 | RANK_8;
        this.occupied = RANK_1 | RANK_2 | RANK_7 | RANK_8;
    }

    /**
     * Gets the bitboard of the given piece type and color.
     *
     * @param type  The piece type.
     * @param color The piece color.
     * @return The bitboard of the given piece type and color.
     */
    public long pieceMask(PieceType type, Color color) {
        long pieceMask = 0L;

        switch (type) {
            case PAWN:
                pieceMask = this.occupiedColor[color.ordinal()] & this.pawns;
                break;
            case KNIGHT:
                pieceMask = this.occupiedColor[color.ordinal()] & this.knights;
                break;
            case BISHOP:
                pieceMask = this.occupiedColor[color.ordinal()] & this.bishops;
                break;
            case ROOK:
                pieceMask = this.occupiedColor[color.ordinal()] & this.rooks;
                break;
            case QUEEN:
                pieceMask = this.occupiedColor[color.ordinal()] & this.queens;
                break;
            case KING:
                pieceMask = this.occupiedColor[color.ordinal()] & this.kings;
                break;
            default:
                break;
        }

        return pieceMask;
    }

    /**
     * Returns a copy of the actual board.
     *
     * @return A copy of the actual board.
     */
    public BaseBoard copy() {
        BaseBoard board = new BaseBoard();
        board.pawns = this.pawns;
        board.knights = this.knights;
        board.bishops = this.bishops;
        board.rooks = this.rooks;
        board.queens = this.queens;
        board.kings = this.kings;

        board.occupiedColor[Color.WHITE.ordinal()] = this.occupiedColor[Color.WHITE.ordinal()];
        board.occupiedColor[Color.BLACK.ordinal()] = this.occupiedColor[Color.BLACK.ordinal()];
        board.occupied = this.occupied;
        board.promoted = this.promoted;

        return board;
    }

    /**
     * Returns the piece at the given square.
     *
     * @param square The given square
     * @return The piece at the given square. If there is no piece at the square, it returns null.
     */
    public Piece pieceAt(Square square) {
        long mask = SQUARES[square.ordinal()];
        PieceType type = pieceTypeAt(square);
        if (type != null) {
            Color color = Color.fromBoolean((this.occupiedColor[Color.WHITE.ordinal()] & mask) != 0);
            return Piece.fromTypeAndColor(type, color);
        }
        return null;
    }

    /**
     * Returns the piece type at the given square.
     *
     * @param square The given square
     * @return The piece type at the given square. If there is no piece at the square, it returns null.
     */
    public PieceType pieceTypeAt(Square square) {
        long mask = SQUARES[square.ordinal()];
        if ((this.occupied & mask) == 0) {
            return null;
        } else if ((this.pawns & mask) != 0) {
            return PieceType.PAWN;
        } else if ((this.knights & mask) != 0) {
            return PieceType.KNIGHT;
        } else if ((this.bishops & mask) != 0) {
            return PieceType.BISHOP;
        } else if ((this.rooks & mask) != 0) {
            return PieceType.ROOK;
        } else if ((this.queens & mask) != 0) {
            return PieceType.QUEEN;
        } else {
            return PieceType.KING;
        }
    }

    /**
     * Sets the piece at the given square.
     *
     * @param square The given square
     * @param piece  The piece to set
     */
    public void set(Square square, Piece piece) {
        if (piece == null) {
            removePiece(square);
        } else {
            setPiece(square, piece.getType(), piece.getColor());
        }
    }

    /**
     * Sets the piece with a given color and type at the given square.
     *
     * @param square The given square
     * @param type   The piece type
     * @param color  The piece color
     */
    protected void setPiece(Square square, PieceType type, Color color) {
        setPiece(square, type, color, false);
    }

    /**
     * Sets the piece with a given color, type and promotion status at the given square.
     *
     * @param square   The given square
     * @param type     The piece type
     * @param color    The piece color
     * @param promoted The promotion status
     */
    protected void setPiece(Square square, PieceType type, Color color, boolean promoted) {
        long mask = SQUARES[square.ordinal()];
        removePiece(square);

        if (type == PieceType.PAWN) {
            this.pawns |= mask;
        } else if (type == PieceType.KNIGHT) {
            this.knights |= mask;
        } else if (type == PieceType.BISHOP) {
            this.bishops |= mask;
        } else if (type == PieceType.ROOK) {
            this.rooks |= mask;
        } else if (type == PieceType.QUEEN) {
            this.queens |= mask;
        } else if (type == PieceType.KING) {
            this.kings |= mask;
        } else {
            return;
        }

        this.occupied ^= mask;
        this.occupiedColor[color.ordinal()] ^= mask;

        if (promoted) {
            this.promoted ^= mask;
        }
    }

    /**
     * Removes the piece at the given square.
     *
     * @param square The given square
     * @return The removed piece. If there is no piece at the square, it returns null.
     */
    public Piece removePiece(Square square) {
        Color color = Color.fromBoolean((this.occupiedColor[Color.WHITE.ordinal()] & SQUARES[square.ordinal()]) != 0);
        PieceType pieceType = removePieceType(square);

        if (pieceType != null) {
            return Piece.fromTypeAndColor(pieceType, color);
        }

        return null;
    }

    /**
     * Removes the piece at the given square.
     *
     * @param square The given square
     * @return The removed piece type. If there is no piece at the square, it returns null.
     */
    protected PieceType removePieceType(Square square) {
        long mask = SQUARES[square.ordinal()];
        PieceType type = pieceTypeAt(square);

        if (type == PieceType.PAWN) {
            this.pawns ^= mask;
        } else if (type == PieceType.KNIGHT) {
            this.knights ^= mask;
        } else if (type == PieceType.BISHOP) {
            this.bishops ^= mask;
        } else if (type == PieceType.ROOK) {
            this.rooks ^= mask;
        } else if (type == PieceType.QUEEN) {
            this.queens ^= mask;
        } else if (type == PieceType.KING) {
            this.kings ^= mask;
        } else {
            return null;
        }

        this.occupied ^= mask;
        this.occupiedColor[Color.WHITE.ordinal()] &= ~mask;
        this.occupiedColor[Color.BLACK.ordinal()] &= ~mask;
        this.promoted &= ~mask;

        return type;
    }

    /**
     * Returns the color of the piece at the given square.
     *
     * @param square The given square
     * @return The color of the piece at the given square. If there is no piece at the square, it returns null.
     */
    public Color colorAt(Square square) {
        long mask = SQUARES[square.ordinal()];
        if ((this.occupiedColor[Color.WHITE.ordinal()] & mask) != 0) {
            return Color.WHITE;
        } else if ((this.occupiedColor[Color.BLACK.ordinal()] & mask) != 0) {
            return Color.BLACK;
        } else {
            return null;
        }
    }

    /**
     * Returns the square of the king of the given color.
     *
     * @param color The given color
     * @return The square of the king of the given color. If there is no king of the given color, it returns null.
     */
    public Square getKingSquare(Color color) {
        long kingMask = this.kings & this.occupiedColor[color.ordinal()] & ~this.promoted;
        if (kingMask != 0) {
            return Square.fromIndex(BitboardUtils.msb(kingMask));
        }


        return null;
    }

    /**
     * Returns a set of squares with the given piece type and color.
     *
     * @param type  The given piece type
     * @param color The given piece color
     * @return A set of squares with the given piece type and color.
     * @see SquareSet
     */
    public SquareSet pieces(PieceType type, Color color) {
        return new SquareSet(pieceMask(type, color));
    }

    /**
     * Returns a bitboard with the squares attacked by the piece at the given square.
     *
     * @param square The given square
     * @return A bitboard with the squares attacked by the piece at the given square.
     */
    public long attackMask(Square square) {
        long mask = SQUARES[square.ordinal()];

        if ((mask & this.pawns) != 0) {
            int color = (mask & this.occupiedColor[Color.WHITE.ordinal()]) != 0 ? 0 : 1;
            return PAWN_ATTACKS[color][square.ordinal()];
        } else if ((mask & this.knights) != 0) {
            return KNIGHT_ATTACKS[square.ordinal()];
        } else if ((mask & this.kings) != 0) {
            return KING_ATTACKS[square.ordinal()];
        } else {
            return longRangeAttacks(square, mask);
        }
    }

    /**
     * Returns a bitboard with the squares attacked
     * by the long range piece at the given square.
     *
     * @param square The given square
     * @param mask   The mask of the given square
     * @return A bitboard with the squares attacked by the long range piece at the given square.
     */
    private long longRangeAttacks(Square square, long mask) {
        long attacks = 0;
        if ((mask & this.bishops) != 0 || (mask & this.queens) != 0) {
            Map<Long, Long> attackMap = DIAGONAL_ATTACKS.get(square.ordinal());
            attacks = attackMap.get(DIAGONAL_MASKS[square.ordinal()] & this.occupied);
        }
        if ((mask & this.rooks) != 0 || (mask & this.queens) != 0) {
            Map<Long, Long> rankAttackMap = RANK_ATTACKS.get(square.ordinal());
            Map<Long, Long> fileAttackMap = FILE_ATTACKS.get(square.ordinal());

            attacks |= rankAttackMap.get(RANK_MASKS[square.ordinal()] & this.occupied) |
                       fileAttackMap.get(FILE_MASKS[square.ordinal()] & this.occupied);
        }
        return attacks;
    }

    /**
     * Returns a set of squares attacked by the piece at the given square.
     *
     * @param square The given square
     * @return A set of squares attacked by the piece at the given square.
     * @see SquareSet
     */
    public SquareSet attacks(Square square) {
        return new SquareSet(attackMask(square));
    }

    /**
     * Returns a bitboard with all attackers from the given color that are attacking the given square.
     *
     * @param color    The given color
     * @param square   The given square
     * @param occupied The occupied squares
     * @return A bitboard with all attackers from the given color that are attacking the given square.
     */
    protected long attackersMask(Color color, Square square, long occupied) {
        long rankPieces = RANK_MASKS[square.ordinal()] & occupied;
        long filePieces = FILE_MASKS[square.ordinal()] & occupied;
        long diagPieces = DIAGONAL_MASKS[square.ordinal()] & occupied;

        long queensAndRooks = this.queens | this.rooks;
        long queensAndBishops = this.queens | this.bishops;

        long attackers = (KING_ATTACKS[square.ordinal()] & this.kings) |
                         KNIGHT_ATTACKS[square.ordinal()] & this.knights |
                         RANK_ATTACKS.get(square.ordinal()).get(rankPieces) & queensAndRooks |
                         FILE_ATTACKS.get(square.ordinal()).get(filePieces) & queensAndRooks |
                         DIAGONAL_ATTACKS.get(square.ordinal()).get(diagPieces) & queensAndBishops |
                         PAWN_ATTACKS[color.other().ordinal()][square.ordinal()] & this.pawns;
        return attackers & this.occupiedColor[color.ordinal()];
    }

    /**
     * Returns a bitboard with all attackers from the given color that are attacking the given square.
     *
     * @param color  The given color
     * @param square The given square
     * @return A bitboard with all attackers from the given color that are attacking the given square.
     */
    public long attackersMask(Color color, Square square) {
        return attackersMask(color, square, this.occupied);
    }

    /**
     * Checks if the given square is attacked by the given color.
     *
     * @param color  The given color
     * @param square The given square
     * @return True if the given square is attacked by the given color, false otherwise.
     */
    public boolean isAttackedBy(Color color, Square square) {
        return attackersMask(color, square) != 0;
    }

    /**
     * Returns a set of squares with all attackers from the given color that are attacking the given square.
     *
     * @param color  The given color
     * @param square The given square
     * @return A set of squares with all attackers from the given color that are attacking the given square.
     * @see SquareSet
     */
    public SquareSet attackers(Color color, Square square) {
        return new SquareSet(attackersMask(color, square));
    }

    /**
     * Gets the board fen.
     *
     * <p>The board fen is a string representing the position of the pieces on the board,
     * e.g. "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR".
     *
     * @return The board fen.
     */
    public String getBoardFen() {
        StringBuilder sb = new StringBuilder();
        int empty = 0;

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                Piece piece = pieceAt(Square.getSquare(x, y));

                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        sb.append(empty);
                        empty = 0;
                    }

                    sb.append(piece.getSymbol());
                }
            }
            if (empty > 0) {
                sb.append(empty);
                empty = 0;
            }
            if (y > 0) {
                sb.append('/');
            }
        }

        return sb.toString();
    }

    /**
     * Sets the board fen.
     *
     * <p>The board fen is a string representing the position of the pieces on the board,
     * e.g. "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR".
     *
     * @param fen The board fen.
     */
    protected void setBoardFen(String fen) {
        fen = fen.trim();
        if (fen.contains(" ")) {
            throw new InvalidFenException("Invalid board FEN: Contains space");
        }

        String[] rows = fen.split("/");
        if (rows.length != 8) {
            throw new InvalidFenException("Invalid board FEN: Expected 8 rows in board fen");
        }

        for (String row : rows) {
            int rowSum = 0;
            boolean previousWasNumber = false;

            for (char c : row.toCharArray()) {
                if (c >= '1' && c <= '8') {
                    if (previousWasNumber) {
                        throw new InvalidFenException("Invalid board FEN:: Several numbers in a row");
                    }

                    rowSum += Character.getNumericValue(c);
                    previousWasNumber = true;
                } else if ("pnbrkqPNBRKQ".indexOf(c) != -1) {
                    rowSum++;
                    previousWasNumber = false;
                } else {
                    throw new InvalidFenException("Invalid board FEN:: Invalid character " + c);
                }
            }

            if (rowSum != 8) {
                throw new InvalidFenException("Invalid board FEN: Invalid row length");
            }
        }

        clearBoard();
        int index = 56;
        for (char c : fen.toCharArray()) {
            if (c >= '1' && c <= '8') {
                index += Character.getNumericValue(c);
            } else if ("pnbrkqPNBRKQ".indexOf(c) != -1) {
                Piece piece = new Piece(c);
                setPiece(Square.fromIndex(index), piece.getType(), piece.getColor());
                index++;
            } else {
                index -= 16;
            }
        }

    }

    /**
     * Gets a map of squares (key) and pieces (value) from the given mask.
     *
     * @param mask The given mask
     * @return A map of squares (key) and pieces (value) from the given mask.
     */
    public Map<Square, Piece> getPieceMap(long mask) {
        Map<Square, Piece> result = new HashMap<>();

        for (int index : BitboardUtils.scanReversed(mask)) {
            Square square = Square.fromIndex(index);
            result.put(square, pieceAt(square));
        }

        return result;
    }

    /**
     * Gets a map of squares (key) and pieces (value) for all squares.
     *
     * @return A map of squares (key) and pieces (value) for all squares.
     */
    public Map<Square, Piece> getPieceMap() {
        return getPieceMap(Bitboard.ALL);
    }

    /**
     * Sets up the board from the given piece map.
     *
     * @param pieceMap The given piece map.
     */
    public void setPieceMap(Map<Square, Piece> pieceMap) {
        clearBitboards();
        for (Map.Entry<Square, Piece> entry : pieceMap.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseBoard board = (BaseBoard) o;
        return this.pawns == board.pawns &&
               this.knights == board.knights &&
               this.bishops == board.bishops &&
               this.rooks == board.rooks &&
               this.queens == board.queens &&
               this.kings == board.kings &&
               this.promoted == board.promoted &&
               this.occupiedColor[Color.WHITE.ordinal()] == board.occupiedColor[Color.WHITE.ordinal()] &&
               this.occupiedColor[Color.BLACK.ordinal()] == board.occupiedColor[Color.BLACK.ordinal()] &&
               this.occupied == board.occupied;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pawns, knights, bishops, rooks, queens, kings, promoted, occupied,
                            this.occupiedColor[Color.WHITE.ordinal()],
                            this.occupiedColor[Color.BLACK.ordinal()]);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int rank = 8; rank >= 1; rank--) {
            for (char file = 'a'; file <= 'h'; file++) {
                Piece piece = pieceAt(Square.getSquare(file, rank));
                sb.append(piece == null ? '.' : piece.getSymbol());
                if (file < 'h') {
                    sb.append(' ');
                }
            }
            if (rank > 1) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Returns a Unicode representation of the board.
     *
     * @return A Unicode representation of the board.
     */
    public String unicode() {
        return unicode(false, false, '·', Color.WHITE);
    }

    /**
     * Returns a Unicode representation of the board with the option to invert colors,
     * display a border and set the empty square character.
     *
     * @param invertColor If true, the board is displayed with inverted colors.
     * @param border      If true, the board is displayed with a border.
     * @param emptySquare The character used for an empty square.
     * @param orientation The orientation of the board. The starting color is at the bottom.
     * @return A Unicode representation of the board.
     */
    public String unicode(boolean invertColor, boolean border, char emptySquare, Color orientation) {
        StringBuilder sb = new StringBuilder();

        int start = orientation == Color.WHITE ? 7 : 0;
        int end = orientation == Color.WHITE ? -1 : 8;
        int step = orientation == Color.WHITE ? -1 : 1;

        for (int rankIndex = start; rankIndex != end; rankIndex += step) {
            if (border) {
                sb.append(" ");
                sb.append("-----------------");
                sb.append("\n");

                sb.append(rankIndex + 1);
                sb.append(" ");
            }

            for (int i = 0; i < 8; i++) {
                int fileIndex = orientation == Color.WHITE ? i : 7 - i;

                if (border) {
                    sb.append("|");
                } else if (i > 0) {
                    sb.append(" ");
                }

                Piece piece = pieceAt(Square.getSquare(fileIndex, rankIndex));

                if (piece == null) {
                    sb.append(emptySquare);
                } else {
                    sb.append(piece.getUnicodeSymbol(invertColor));
                }
            }

            if (border) {
                sb.append("|");
            }

            if (border || (orientation == Color.WHITE ? rankIndex > 0 : rankIndex < 7)) {
                sb.append("\n");
            }
        }

        if (border) {
            sb.append(" ");
            sb.append("-----------------");
            sb.append("\n");
            String letters = orientation == Color.WHITE ? "a b c d e f g h" : "h g f e d c b a";
            sb.append("   ").append(letters);
        }
        return sb.toString();
    }

    public long getPawns() {
        return pawns;
    }

    public long getKnights() {
        return knights;
    }

    public long getBishops() {
        return bishops;
    }

    public long getRooks() {
        return rooks;
    }

    public long getQueens() {
        return queens;
    }

    public long getKings() {
        return kings;
    }

    public long getPromoted() {
        return promoted;
    }

    public long getWhitePieces() {
        return this.occupiedColor[Color.WHITE.ordinal()];
    }

    public long getBlackPieces() {
        return this.occupiedColor[Color.BLACK.ordinal()];
    }

    public long getOccupied() {
        return occupied;
    }

    /**
     * Mirrors the board.
     *
     * <p>The board is mirrored along the vertical axis, e.g. A1 is mirrored to A8 and H1 to H8.
     */
    public void applyMirror() {
        long temp = this.occupiedColor[Color.BLACK.ordinal()];
        this.occupiedColor[Color.BLACK.ordinal()] = this.occupiedColor[Color.WHITE.ordinal()];
        this.occupiedColor[Color.WHITE.ordinal()] = temp;
        this.applyTransform(BitboardUtils::flipVertical);
    }

    /**
     * Applies a transformation to the board.
     *
     * <p>Available transforms:
     * {@link BitboardUtils#flipVertical}, {@link BitboardUtils#flipHorizontal},
     * {@link BitboardUtils#flipDiagonal}, {@link BitboardUtils#flipAntiDiagonal},
     * {@link BitboardUtils#shiftUp}, {@link BitboardUtils#shiftDown},
     * {@link BitboardUtils#shiftLeft}, {@link BitboardUtils#shiftRight}
     *
     * @param transform The transform function.
     */
    public void applyTransform(Function<Long, Long> transform) {
        this.pawns = transform.apply(this.pawns);
        this.knights = transform.apply(this.knights);
        this.bishops = transform.apply(this.bishops);
        this.rooks = transform.apply(this.rooks);
        this.queens = transform.apply(this.queens);
        this.kings = transform.apply(this.kings);

        this.occupiedColor[Color.WHITE.ordinal()] = transform.apply(this.occupiedColor[Color.WHITE.ordinal()]);
        this.occupiedColor[Color.BLACK.ordinal()] = transform.apply(this.occupiedColor[Color.BLACK.ordinal()]);
        this.occupied = transform.apply(this.occupied);
        this.promoted = transform.apply(this.promoted);
    }
}
