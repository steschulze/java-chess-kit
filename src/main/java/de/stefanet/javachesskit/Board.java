package de.stefanet.javachesskit;

import static de.stefanet.javachesskit.bitboard.Bitboard.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_A;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_C;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_D;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_F;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_G;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_H;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_8;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.A1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.A8;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.E1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.E8;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.H1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.H8;

import de.stefanet.javachesskit.bitboard.Bitboard;
import de.stefanet.javachesskit.bitboard.BitboardUtils;
import de.stefanet.javachesskit.core.Color;
import de.stefanet.javachesskit.core.PieceType;
import de.stefanet.javachesskit.core.Square;
import de.stefanet.javachesskit.move.AmbiguousMoveException;
import de.stefanet.javachesskit.move.IllegalMoveException;
import de.stefanet.javachesskit.move.LegalMoveGenerator;
import de.stefanet.javachesskit.move.Move;
import de.stefanet.javachesskit.move.PseudoLegalMoveGenerator;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class representing a chess board with additional information
 * like castling rights, en-passant square, half move clock and full move number.
 *
 * <p>The class provides move generation, move validation, move parsing, game end detection
 * and the ability to make and unmake moves.
 */
public class Board extends BaseBoard {
    protected static final String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    protected Color turn;
    protected long castlingRights;

    protected Square epSquare;

    protected int fullMoveNumber;
    protected int halfMoveClock;

    protected long promoted;
    protected Deque<Move> moveStack;
    protected Deque<BoardState> stateStack;

    /**
     * Create a new board with the starting position.
     */
    public Board() {
        this(STARTING_FEN);
    }

    /**
     * Create a new board with the given FEN string.
     *
     * @param fen The FEN string.
     */
    public Board(String fen) {
        super(null);

        this.epSquare = null;
        this.moveStack = new ArrayDeque<>();
        this.stateStack = new ArrayDeque<>();

        if (fen == null) {
            clear();
        } else if (fen.equals(STARTING_FEN)) {
            reset();
        } else {
            setFen(fen);
        }
    }

    /**
     * Create an empty board.
     *
     * @return An empty board.
     */
    public static Board empty() {
        return new Board(null);
    }

    /**
     * Creates a copy of the board.
     *
     * @return A copy of the board.
     */
    public Board copy() {
        Board board = new Board();
        board.pawns = this.pawns;
        board.knights = this.knights;
        board.bishops = this.bishops;
        board.rooks = this.rooks;
        board.queens = this.queens;
        board.kings = this.kings;

        board.occupiedColor[0] = this.occupiedColor[0];
        board.occupiedColor[1] = this.occupiedColor[1];

        board.occupied = this.occupied;
        board.promoted = this.promoted;

        board.epSquare = this.epSquare;
        board.castlingRights = this.castlingRights;
        board.turn = this.turn;
        board.fullMoveNumber = this.fullMoveNumber;
        board.halfMoveClock = this.halfMoveClock;

        return board;
    }

    /**
     * Parses the FEN string and sets the board to the position described in the FEN string.
     *
     * @param fen The FEN string.
     * @throws InvalidFenException If the FEN string is invalid.
     */
    public void setFen(String fen) {
        String[] parts = fen.split(" ");

        if (parts.length != 6) {
            throw new InvalidFenException("FEN must have 6 parts, but only has " + parts.length);
        }

        if (!parts[1].equals("w") && !parts[1].equals("b")) {
            throw new InvalidFenException("Turn part of the FEN is invalid: Expected w or b, but was " + parts[1]);
        }
        if (!Pattern.matches("^(KQ?k?q?|Qk?q?|kq?|q|-)$", parts[2])) {
            throw new InvalidFenException("Castling part of the FEN is invalid");
        }
        if (!Pattern.matches("^(-|[a-h][36])$", parts[3])) {
            throw new InvalidFenException("En-passant part of the FEN is invalid");
        }
        if (!Pattern.matches("^(0|[1-9][0-9]*)$", parts[4])) {
            throw new InvalidFenException("Half move part of the FEN is invalid");
        }
        if (!Pattern.matches("^[1-9][0-9]*$", parts[5])) {
            throw new InvalidFenException("Full move part of the FEN is invalid");
        }

        setBoardFen(parts[0]);
        this.turn = Color.fromSymbol(parts[1].charAt(0));
        setCastlingFen(parts[2]);
        this.epSquare = parts[3].equals("-") ? null : Square.parseSquare(parts[3]);
        this.halfMoveClock = Integer.parseInt(parts[4]);
        this.fullMoveNumber = Integer.parseInt(parts[5]);
        clearStack();
    }

    /**
     * Sets the castling rights from the castling part in FEN string like "KQkq".
     *
     * @param castlingFen The castling part of the FEN string.
     * @throws IllegalArgumentException If the castling part is invalid.
     */
    public void setCastlingFen(String castlingFen) {
        if (castlingFen == null || castlingFen.equals("-")) {
            this.castlingRights = 0;
            return;
        }

        Pattern pattern = Pattern.compile("^(-|\\bK?Q?k?q?)$");
        if (!pattern.matcher(castlingFen).matches()) {
            throw new IllegalArgumentException("Invalid castling fen: " + castlingFen);
        }

        this.castlingRights = 0;

        for (char flag : castlingFen.toCharArray()) {
            Color color = Character.isUpperCase(flag) ? Color.WHITE : Color.BLACK;
            flag = Character.toLowerCase(flag);
            long backrank = color == Color.WHITE ? RANK_1 : RANK_8;

            if (flag == 'q') {
                this.castlingRights |= FILE_A & backrank;
            } else if (flag == 'k') {
                this.castlingRights |= FILE_H & backrank;
            }
        }

    }

    /**
     * Restores the starting position.
     */
    public void reset() {
        this.turn = Color.WHITE;
        this.castlingRights = A1 | H1 | A8 | H8;
        this.epSquare = null;
        this.halfMoveClock = 0;
        this.fullMoveNumber = 1;
        resetBoard();
    }

    @Override
    protected void resetBoard() {
        super.resetBoard();
        clearStack();
    }

    /**
     * Clears the board.
     *
     * <p>This also resets the move counters, removes the castling rights
     * and set WHITE to move.
     */
    void clear() {
        this.turn = Color.WHITE;
        this.castlingRights = 0;
        this.epSquare = null;
        this.halfMoveClock = 0;
        this.fullMoveNumber = 1;

        clearBoard();
    }


    /**
     * Clears the board, the move stack and the board state stack.
     */
    @Override
    protected void clearBoard() {
        super.clearBoard();
        clearStack();
    }

    /**
     * Clears the move stack and the board state stack.
     */
    protected void clearStack() {
        this.moveStack.clear();
        this.stateStack.clear();
    }

    /**
     * Returns the number of half-moves since start of the game.
     *
     * @return The number of half-moves since start of the game.
     */
    public int ply() {
        return 2 * (this.fullMoveNumber - 1) + this.turn.ordinal();
    }

    /**
     * Returns a LegalMoveGenerator for the current position.
     *
     * @return A LegalMoveGenerator for the current position.
     * @see LegalMoveGenerator
     */
    public LegalMoveGenerator legalMoves() {
        return new LegalMoveGenerator(this);
    }

    /**
     * Returns a PseudoLegalMoveGenerator for the current position.
     *
     * @return A PseudoLegalMoveGenerator for the current position.
     */
    public PseudoLegalMoveGenerator pseudoLegalMoves() {
        return new PseudoLegalMoveGenerator(this);
    }

    /**
     * Returns a set of legal moves with the given bitboard masks.
     *
     * <p>If the masks are set to {@link Bitboard#ALL},
     * the method will generate all legal moves.
     *
     * @param fromMask The mask of the source squares.
     * @param toMask   The mask of the target squares.
     * @return A set of legal moves.
     */
    public Set<Move> generateLegalMoves(long fromMask, long toMask) {
        Set<Move> legalMoves = new HashSet<>();

        long kingMask = this.kings & occupiedColor[this.turn.ordinal()];

        if (kingMask != 0) {
            int king = BitboardUtils.msb(kingMask);
            long blockers = sliderBlockers(king);
            long checkers = attackersMask(turn.other(), Square.fromIndex(king));

            if (checkers != 0) {
                for (Move move : generateEvasions(king, checkers, fromMask, toMask)) {
                    if (isSafe(Square.fromIndex(king), blockers, move)) {
                        legalMoves.add(move);
                    }
                }
            } else {
                for (Move move : generatePseudoLegalMoves(fromMask, toMask)) {
                    if (isSafe(Square.fromIndex(king), blockers, move)) {
                        legalMoves.add(move);
                    }
                }
            }
        } else {
            legalMoves.addAll(generatePseudoLegalMoves(fromMask, toMask));
        }

        return legalMoves;
    }

    /**
     * Returns a set of all legal moves in the current position.
     *
     * @return A set of all legal moves.
     */
    public Set<Move> generateLegalMoves() {
        return generateLegalMoves(ALL, ALL);
    }

    /**
     * Returns a set of all pseudo-legal moves in the current position.
     *
     * @return A set of all pseudo-legal moves.
     */
    public Set<Move> generatePseudoLegalMoves() {
        return generatePseudoLegalMoves(Bitboard.ALL, Bitboard.ALL);
    }

    /**
     * Returns a set of pseudo-legal moves with the given bitboard masks.
     *
     * @param sourceMask The mask of the source squares.
     * @param targetMask The mask of the target squares.
     * @return A set of pseudo-legal moves.
     */
    public Set<Move> generatePseudoLegalMoves(long sourceMask, long targetMask) {
        Set<Move> moveList = new HashSet<>();
        long ownPieces = this.occupiedColor[this.turn.ordinal()];

        // non pawn moves
        long nonPawns = ownPieces & ~this.pawns & sourceMask;
        for (int index : BitboardUtils.scanReversed(nonPawns)) {
            Square source = Square.fromIndex(index);
            long moves = attackMask(source) & ~ownPieces & targetMask;
            for (int targetIndex : BitboardUtils.scanReversed(moves)) {
                Square target = Square.fromIndex(targetIndex);
                moveList.add(new Move(source, target));
            }
        }

        // castling moves
        if ((sourceMask & this.kings) != 0) {
            moveList.addAll(generateCastlingMoves(sourceMask, targetMask));
        }

        long pawns = this.pawns & ownPieces & sourceMask;
        if (pawns == 0) {
            return moveList;
        }

        // pawn captures
        long captures = pawns;
        for (int captureIndex : BitboardUtils.scanReversed(captures)) {
            Square source = Square.fromIndex(captureIndex);

            long targets = Bitboard.PAWN_ATTACKS[turn.ordinal()][captureIndex]
                           & targetMask & this.occupiedColor[turn.other().ordinal()];

            for (int targetIndex : BitboardUtils.scanReversed(targets)) {
                Square target = Square.fromIndex(targetIndex);
                if (target.isBackrank()) {
                    // pawn capture with promotion
                    moveList.add(new Move(source, target, PieceType.QUEEN));
                    moveList.add(new Move(source, target, PieceType.ROOK));
                    moveList.add(new Move(source, target, PieceType.BISHOP));
                    moveList.add(new Move(source, target, PieceType.KNIGHT));
                } else {
                    // normal pawn capture
                    moveList.add(new Move(source, target));
                }

            }
        }

        // pawn advance
        long singlePawnMoves;
        long doublePawnMoves;

        if (turn.equals(Color.WHITE)) {
            singlePawnMoves = pawns << 8 & ~this.occupied;
            doublePawnMoves = singlePawnMoves << 8 & ~this.occupied & (Bitboard.Ranks.RANK_3 | Bitboard.Ranks.RANK_4);
        } else {
            singlePawnMoves = pawns >> 8 & ~this.occupied;
            doublePawnMoves = singlePawnMoves >> 8 & ~this.occupied & (Bitboard.Ranks.RANK_6 | Bitboard.Ranks.RANK_5);
        }

        singlePawnMoves &= targetMask;
        doublePawnMoves &= targetMask;

        // single pawn advance
        for (int index : BitboardUtils.scanReversed(singlePawnMoves)) {
            Square target = Square.fromIndex(index);
            Square source = Square.fromIndex(index - turn.forwardDirection() * 8);

            if (target.isBackrank()) {
                // pawn advance with promotion
                moveList.add(new Move(source, target, PieceType.QUEEN));
                moveList.add(new Move(source, target, PieceType.ROOK));
                moveList.add(new Move(source, target, PieceType.BISHOP));
                moveList.add(new Move(source, target, PieceType.KNIGHT));
            } else {
                // normal pawn advance
                moveList.add(new Move(source, target));
            }
        }

        // double pawn advance
        for (int index : BitboardUtils.scanReversed(doublePawnMoves)) {
            Square target = Square.fromIndex(index);
            Square source = Square.fromIndex(index - turn.forwardDirection() * 16);
            moveList.add(new Move(source, target));
        }

        if (epSquare != null) {
            moveList.addAll(generatePseudoLegalEnPassant(sourceMask, targetMask));
        }
        return moveList;
    }

    /**
     * Returns a set of pseudo-legal En passant moves in the current position.
     *
     * @return A set of pseudo-legal En passant moves.
     */
    public Set<Move> generatePseudoLegalEnPassant() {
        return generatePseudoLegalEnPassant(ALL, ALL);
    }

    /**
     * Returns a set of pseudo-legal En passant moves with the given bitboard masks.
     *
     * <p>This allows En Passant moves that put the king in check.
     * The method returns an empty set when the En Passant square is null,
     * the target mask does not contain the En Passant square
     * or the En Passant square is already occupied.
     *
     * @param sourceMask The mask of the source squares.
     * @param targetMask The mask of the target squares.
     * @return A set of pseudo-legal En passant moves.
     */
    public Set<Move> generatePseudoLegalEnPassant(long sourceMask, long targetMask) {
        Set<Move> moves = new HashSet<>();

        if (epSquare == null || (SQUARES[epSquare.ordinal()] & targetMask) == 0) {
            return moves;
        }

        // epSquare is occupied
        if ((SQUARES[epSquare.ordinal()] & this.occupied) != 0) {
            return moves;
        }

        long rankMask = this.turn.equals(Color.WHITE) ? Bitboard.RANKS[4] : Bitboard.RANKS[3];
        long attackMask = PAWN_ATTACKS[turn.other().ordinal()][epSquare.ordinal()];

        long capturers = this.pawns & this.occupiedColor[turn.ordinal()] & sourceMask & attackMask & rankMask;

        for (int index : BitboardUtils.scanReversed(capturers)) {
            Square source = Square.fromIndex(index);
            moves.add(new Move(source, epSquare));
        }

        return moves;
    }

    /**
     * Checks if there is a legal En Passant move in the current position.
     *
     * @return True if there is a legal En Passant move, false otherwise.
     */
    public boolean hasLegalEnPassant() {
        return this.epSquare != null && !generateLegalEnPassant().isEmpty();
    }

    /**
     * Checks if there is a pseudo-legal En Passant move in the current position.
     *
     * @return True if there is a pseudo-legal En Passant move, false otherwise.
     */
    public boolean hasPseudoLegalEnPassant() {
        return this.epSquare != null && !generatePseudoLegalEnPassant().isEmpty();
    }

    /**
     * Returns a set of legal En passant moves in the current position.
     *
     * @return A set of legal En passant moves.
     */
    public Set<Move> generateLegalEnPassant() {
        return generateLegalEnPassant(ALL, ALL);
    }

    /**
     * Returns a set of legal En passant moves with the given bitboard masks.
     *
     * @param sourceMask The mask of the source squares.
     * @param targetMask The mask of the target squares.
     * @return A set of legal En passant moves.
     */
    public Set<Move> generateLegalEnPassant(long sourceMask, long targetMask) {
        Set<Move> moves = new HashSet<>();

        for (Move move : generatePseudoLegalEnPassant(sourceMask, targetMask)) {
            if (!isKingInCheck(move)) {
                moves.add(move);
            }
        }

        return moves;
    }

    /**
     * Returns a set of legal castling moves.
     *
     * @return A set of legal castling moves.
     */
    public Set<Move> generateCastlingMoves() {
        return generateCastlingMoves(ALL, ALL);
    }

    /**
     * Returns a set of legal castling moves with the given bitboard masks.
     *
     * @param sourceMask The mask of the source squares.
     * @param targetMask The mask of the target squares.
     * @return A set of legal castling moves.
     */
    public Set<Move> generateCastlingMoves(long sourceMask, long targetMask) {
        Set<Move> moves = new HashSet<>();

        long backrank = this.turn.equals(Color.WHITE) ? RANK_1 : RANK_8;
        long king = this.occupiedColor[turn.ordinal()] & this.kings & ~this.promoted & backrank & sourceMask;
        king &= -king;

        if (king == 0) {
            return moves;
        }

        long c = FILE_C & backrank;
        long d = FILE_D & backrank;
        long f = FILE_F & backrank;
        long g = FILE_G & backrank;

        long castling = cleanCastlingRights() & backrank & targetMask;

        for (int index : BitboardUtils.scanReversed(castling)) {
            long rook = SQUARES[index];
            boolean queenSide = rook < king;

            long kingTarget = queenSide ? c : g;
            long rookTarget = queenSide ? d : f;

            long kingPath = BitboardUtils.between(
                    BitboardUtils.msb(king),
                    BitboardUtils.msb(kingTarget));

            long rookPath = BitboardUtils.between(
                    index,
                    BitboardUtils.msb(rookTarget));

            if (!(
                    ((this.occupied ^ king ^ rook) & (kingPath | rookPath | kingTarget | rookTarget)) != 0
                    || attackedForKing(kingPath | king, this.occupied ^ king)
                    || attackedForKing(kingTarget, this.occupied ^ king ^ rook ^ rookTarget))) {
                Square source = Square.fromIndex(BitboardUtils.msb(king));
                Square target = Square.fromIndex(index);

                if (source == Square.E1 && (this.kings & E1) != 0) {
                    if (target == Square.H1) {
                        moves.add(new Move(Square.E1, Square.G1));
                    } else if (target == Square.A1) {
                        moves.add(new Move(Square.E1, Square.C1));
                    }
                } else if (source == Square.E8 && (this.kings & E8) != 0) {
                    if (target == Square.H8) {
                        moves.add(new Move(Square.E8, Square.G8));
                    } else if (target == Square.A8) {
                        moves.add(new Move(Square.E8, Square.C8));
                    }
                }
            }
        }

        return moves;
    }

    /**
     * Returns a set of all legal captures in the current position.
     *
     * @return A set of legal captures.
     */
    public Set<Move> generateLegalCaptures() {
        return generateLegalCaptures(ALL, ALL);
    }

    /**
     * Returns a set of legal captures with the given bitboard masks.
     *
     * @param sourceMask The mask of the source squares.
     * @param targetMask The mask of the target squares.
     * @return A set of legal captures.
     */
    public Set<Move> generateLegalCaptures(long sourceMask, long targetMask) {
        Set<Move> moves = new HashSet<>();
        moves.addAll(generateLegalMoves(sourceMask, targetMask & this.occupiedColor[turn.other().ordinal()]));
        moves.addAll(generateLegalEnPassant(sourceMask, targetMask));

        return moves;

    }

    /**
     * Returns a set of all pseudo-legal captures in the current position.
     *
     * @return A set of pseudo-legal captures.
     */
    public Set<Move> generatePseudoLegalCaptures() {
        return generatePseudoLegalCaptures(ALL, ALL);
    }

    /**
     * Returns a set of pseudo-legal captures with the given bitboard masks.
     *
     * @param sourceMask The mask of the source squares.
     * @param targetMask The mask of the target squares.
     * @return A set of pseudo-legal captures.
     */
    public Set<Move> generatePseudoLegalCaptures(long sourceMask, long targetMask) {
        Set<Move> moves = new HashSet<>();
        moves.addAll(generatePseudoLegalMoves(sourceMask, targetMask & this.occupiedColor[turn.other().ordinal()]));
        moves.addAll(generatePseudoLegalEnPassant(sourceMask, targetMask));

        return moves;

    }

    /**
     * Checks if the given path of squares is attacked by the other color.
     *
     * @param path     The path of squares.
     * @param occupied The occupied squares.
     * @return True if the path is attacked, false otherwise.
     */
    private boolean attackedForKing(long path, long occupied) {
        for (int index : BitboardUtils.scanReversed(path)) {
            if (attackersMask(turn.other(), Square.fromIndex(index), occupied) != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a bitboard with the valid castling rights.
     *
     * <p>The returned bitboard can have the following bits set:
     * <ul>
     *     <li>{@link Bitboard.Squares#A1} for white queenside castling</li>
     *     <li>{@link Bitboard.Squares#H1} for white kingside castling</li>
     *     <li>{@link Bitboard.Squares#A8} for black queenside castling</li>
     *     <li>{@link Bitboard.Squares#H8} for black kingside castling</li>
     * </ul>
     *
     * @return A bitboard with the valid castling rights.
     */
    protected long cleanCastlingRights() {
        long castling = this.castlingRights & this.rooks;
        long whiteCastling = castling & RANK_1 & this.occupiedColor[Color.WHITE.ordinal()];
        long blackCastling = castling & RANK_8 & this.occupiedColor[Color.BLACK.ordinal()];

        whiteCastling &= (A1 | H1);
        blackCastling &= (A8 | H8);

        if ((this.occupiedColor[Color.WHITE.ordinal()] & this.kings & ~this.promoted & E1) == 0) {
            whiteCastling = 0;
        }
        if ((this.occupiedColor[Color.BLACK.ordinal()] & this.kings & ~this.promoted & E8) == 0) {
            blackCastling = 0;
        }
        return whiteCastling | blackCastling;
    }

    /**
     * Checks if the given color has castling rights.
     *
     * @param color The color.
     * @return True if the color has castling rights, false otherwise.
     */
    public boolean hasCastlingRights(Color color) {
        long backrank = color == Color.WHITE ? RANK_1 : RANK_8;
        return (this.castlingRights & backrank) != 0;
    }

    /**
     * Checks if the given color has kingside castling rights.
     *
     * @param color The color.
     * @return True if the color has kingside castling rights, false otherwise.
     */
    public boolean hasKingsideCastlingRights(Color color) {
        long backrank = color == Color.WHITE ? RANK_1 : RANK_8;
        long kingMask = this.kings & this.occupiedColor[color.ordinal()] & backrank;

        if (kingMask == 0) {
            return false;
        }

        long castlingRights = this.cleanCastlingRights() & backrank;

        return (castlingRights & FILE_H) != 0;
    }

    /**
     * Checks if the given color has queenside castling rights.
     *
     * @param color The color.
     * @return True if the color has queenside castling rights, false otherwise.
     */
    public boolean hasQueensideCastlingRights(Color color) {
        long backrank = color == Color.WHITE ? RANK_1 : RANK_8;
        long kingMask = this.kings & this.occupiedColor[color.ordinal()] & backrank;

        if (kingMask == 0) {
            return false;
        }

        long castlingRights = this.cleanCastlingRights() & backrank;

        return (castlingRights & FILE_A) != 0;
    }

    /**
     * Checks if the given move is legal.
     *
     * @param move The move.
     * @return True if the move is legal, false otherwise.
     */
    public boolean isLegal(Move move) {
        return isPseudoLegal(move) && !isKingInCheck(move);
    }

    /**
     * Checks if the given move puts or leaves the king in check.
     *
     * @param move The move.
     * @return True if the move puts or leaves the king in check, false otherwise.
     */
    private boolean isKingInCheck(Move move) {
        Square kingSquare = this.getKingSquare(this.turn);

        if (kingSquare == null) {
            return false;
        }

        long checkers = attackersMask(turn.other(), kingSquare);
        if (checkers != 0 && !generateEvasions(kingSquare.ordinal(), checkers,
                                               SQUARES[move.getSource().ordinal()],
                                               SQUARES[move.getTarget().ordinal()]).contains(move)) {
            return true;
        }

        return !isSafe(kingSquare, this.sliderBlockers(kingSquare.ordinal()), move);
    }

    /**
     * Checks if the given square is safe after making a move.
     *
     * @param kingSquare The given square. In most cases this will be the king square.
     * @param blockers   A bitboard containing the squares of all pieces
     *                   that block an attack from a sliding piece.
     * @param move       The move which has to be checked.
     * @return True if the given square is safe after making the move, false otherwise.
     */
    private boolean isSafe(Square kingSquare, long blockers, Move move) {
        if (move.getSource() == kingSquare) {
            if (isCastling(move)) {
                return true;
            } else {
                return !isAttackedBy(turn.other(), move.getTarget());
            }
        } else if (isEnPassant(move)) {
            return (pinMask(turn, move.getSource()) & SQUARES[move.getTarget().ordinal()]) != 0
                   && !epSkewered(kingSquare, move.getSource());
        } else {
            return (blockers & SQUARES[move.getSource().ordinal()]) == 0
                   || (
                              BitboardUtils.ray(move.getSource().ordinal(),
                                                move.getTarget().ordinal()) & SQUARES[kingSquare.ordinal()]) != 0;
        }
    }

    /**
     * Checks if the king would be in check after an En Passant move.
     *
     * @param kingSquare The square of the king.
     * @param capturer   The square of the capturer.
     * @return True if the king is in check after En Passant, false otherwise.
     */
    private boolean epSkewered(Square kingSquare, Square capturer) {
        int lastDouble = this.epSquare.ordinal() + ((this.turn == Color.WHITE) ? -8 : 8);

        long occupancy = (
                this.occupied & ~SQUARES[lastDouble]
                & ~SQUARES[capturer.ordinal()] | SQUARES[this.epSquare.ordinal()]);

        long horizontalAttackers = this.occupiedColor[turn.other().ordinal()] & (this.rooks | this.queens);
        if ((
                    RANK_ATTACKS.get(kingSquare.ordinal())
                            .get((RANK_MASKS[kingSquare.ordinal()] & occupancy)) & horizontalAttackers) != 0) {
            return true;
        }

        long diagonalAttackers = this.occupiedColor[turn.other().ordinal()] & (this.bishops | this.queens);
        if ((
                    DIAGONAL_ATTACKS.get(kingSquare.ordinal())
                            .get((DIAGONAL_MASKS[kingSquare.ordinal()] & occupancy)) & diagonalAttackers) != 0) {
            return true;
        }

        return false;

    }

    /**
     * Detects an absolute pin of the given square to the king of the given color.
     *
     * @param color  The color of the king.
     * @param square The pinned square.
     * @return The mask of the pin, either a rank, file or diagonal. Returns {@link Bitboard#ALL} if there is no pin.
     */
    public long pinMask(Color color, Square square) {
        Square king = getKingSquare(color);
        if (king == null) {
            return Bitboard.ALL;
        }

        long squareMask = Bitboard.SQUARES[square.ordinal()];

        long[][] attacks = new long[][]{
                {Bitboard.FILE_ATTACKS.get(king.ordinal()).get(0L), this.rooks | this.queens},
                {Bitboard.RANK_ATTACKS.get(king.ordinal()).get(0L), this.rooks | this.queens},
                {Bitboard.DIAGONAL_ATTACKS.get(king.ordinal()).get(0L), this.bishops | this.queens}
        };

        for (long[] attack : attacks) {
            long rays = attack[0];
            if ((rays & squareMask) != 0) {
                long snipers =
                        rays & attack[1] & this.occupied & this.occupiedColor[color.other().ordinal()];
                for (int sniper : BitboardUtils.scanReversed(snipers)) {
                    if ((BitboardUtils.between(sniper, king.ordinal()) & (this.occupied | squareMask)) == squareMask) {
                        return BitboardUtils.ray(king.ordinal(), sniper);
                    }
                }
                break;
            }
        }

        return Bitboard.ALL;
    }

    /**
     * Detects if the given square is pinned to the king of the given color.
     *
     * @param color  The color of the king.
     * @param square The possibly pinned square.
     * @return True if the square is pinned to the king of the color, false otherwise.
     */
    public boolean isPinned(Color color, Square square) {
        return this.pinMask(color, square) != Bitboard.ALL;
    }

    /**
     * Checks if the given move is a castling move.
     *
     * @param move The move.
     * @return True if the given move is a castling move, false otherwise.
     */
    public boolean isCastling(Move move) {
        if ((this.kings & SQUARES[move.getSource().ordinal()]) != 0) {
            int diff = move.getSource().getFileIndex() - move.getTarget().getFileIndex();
            return Math.abs(diff) > 1 || (
                    (this.rooks & this.occupiedColor[turn.ordinal()] & SQUARES[move.getTarget().ordinal()]) != 0);
        }
        return false;
    }

    /**
     * Checks if the given move is a kingside castling move.
     *
     * @param move The move.
     * @return True if the given move is kingside castling, false otherwise.
     */
    public boolean isKingsideCastling(Move move) {
        return isCastling(move) && move.getTarget().getFileIndex() > move.getSource().getFileIndex();
    }

    /**
     * Checks if the given move is a queenside castling move.
     *
     * @param move The move.
     * @return True if the given move is queenside castling, false otherwise.
     */
    public boolean isQueensideCastling(Move move) {
        return isCastling(move) && move.getTarget().getFileIndex() < move.getSource().getFileIndex();
    }

    /**
     * Checks if the given move is En Passant.
     *
     * @param move The move.
     * @return True if the given move is En Passant, false otherwise.
     */
    public boolean isEnPassant(Move move) {
        return this.epSquare == move.getTarget() && ((this.pawns & SQUARES[move.getSource().ordinal()]) != 0)
               && (
                       Math.abs(move.getTarget().ordinal() - move.getSource().ordinal()) == 7
                       || Math.abs(move.getTarget().ordinal() - move.getSource().ordinal()) == 9)
               && ((this.occupied & SQUARES[move.getTarget().ordinal()]) == 0);
    }

    /**
     * Detects the squares of all pieces that block an attack from a sliding piece.
     *
     * <p>It only considers exactly one piece in-between.
     *
     * @param kingSquareIndex The index of the king square.
     * @return Mask containing the squares of all pieces that block an attack from a sliding piece.
     */
    private long sliderBlockers(int kingSquareIndex) {
        long rooksAndQueens = this.rooks | this.queens;
        long bishopsAndQueens = this.bishops | this.queens;

        long snipers = (
                (RANK_ATTACKS.get(kingSquareIndex).get(0L) & rooksAndQueens)
                | (FILE_ATTACKS.get(kingSquareIndex).get(0L) & rooksAndQueens)
                | (DIAGONAL_ATTACKS.get(kingSquareIndex).get(0L) & bishopsAndQueens));

        long blockers = 0;

        for (int sniper : BitboardUtils.scanReversed(snipers & this.occupiedColor[turn.other().ordinal()])) {
            long b = BitboardUtils.between(kingSquareIndex, sniper) & this.occupied;

            if (b != 0 && SQUARES[BitboardUtils.msb(b)] == b) {
                blockers |= b;
            }
        }

        return blockers & (this.occupied & ~this.occupiedColor[turn.other().ordinal()]);
    }

    /**
     * Generates a set of moves to get out of check.
     *
     * @param kingSquareIndex The index of the king square.
     * @param checkers        Mask that contains the squares of all pieces which are giving check.
     * @param sourceMask      The mask of the source squares.
     * @param targetMask      The mask of the target squares.
     * @return Set of moves to get out of check.
     */
    private Set<Move> generateEvasions(int kingSquareIndex, long checkers, long sourceMask, long targetMask) {
        Set<Move> moves = new HashSet<>();

        long sliders = checkers & (this.bishops | this.rooks | this.queens);
        long attacked = 0;

        for (int checker : BitboardUtils.scanReversed(sliders)) {
            attacked |= BitboardUtils.ray(kingSquareIndex, checker) & ~SQUARES[checker];
        }
        if ((SQUARES[kingSquareIndex] & sourceMask) != 0) {
            long mask = KING_ATTACKS[kingSquareIndex] & ~this.occupiedColor[turn.ordinal()] & ~attacked & targetMask;
            int[] targets = BitboardUtils.scanReversed(mask);
            for (int target : targets) {
                moves.add(new Move(Square.fromIndex(kingSquareIndex), Square.fromIndex(target)));
            }
        }
        int checker = BitboardUtils.msb(checkers);
        if (SQUARES[checker] == checkers) {
            long target = BitboardUtils.between(kingSquareIndex, checker) | checkers;
            moves.addAll(generatePseudoLegalMoves(~this.kings & sourceMask, target & targetMask));

            if (this.epSquare != null && (SQUARES[epSquare.ordinal()] & target) == 0) {
                int lastDouble = this.epSquare.ordinal() - 8 * turn.forwardDirection();
                if (lastDouble == checker) {
                    moves.addAll(generatePseudoLegalEnPassant(sourceMask, targetMask));
                }
            }
        }


        return moves;
    }

    /**
     * Checks if the given move is pseudo-legal.
     *
     * @param move The move.
     * @return True if the move is pseudo-legal, false otherwise.
     */
    public boolean isPseudoLegal(Move move) {
        PieceType type = pieceTypeAt(move.getSource());
        if (type == null) {
            return false;
        }

        long sourceMask = SQUARES[move.getSource().ordinal()];

        if ((this.occupiedColor[turn.ordinal()] & sourceMask) == 0) {
            return false;
        }

        if (move.getPromotion() != null) {
            if (type != PieceType.PAWN) {
                return false;
            }

            if (turn.equals(Color.WHITE) && move.getTarget().getRank() != 8) {
                return false;
            } else if (turn.equals(Color.BLACK) && move.getTarget().getRank() != 1) {
                return false;
            }
        }

        if (type == PieceType.KING) {
            if (generateCastlingMoves().contains(move)) {
                return true;
            }
        }
        long targetMask = SQUARES[move.getTarget().ordinal()];

        if ((this.occupiedColor[turn.ordinal()] & targetMask) != 0) {
            return false;
        }

        if (type == PieceType.PAWN) {
            return generatePseudoLegalMoves(sourceMask, targetMask).contains(move);
        }

        return (attackMask(move.getSource()) & targetMask) != 0;
    }

    /**
     * Gets the FEN representation of the current position.
     *
     * <p>A FEN like 'rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1'
     * consists of a board part, a turn part, a castling part, an En Passant square,
     * half move clock and full move number.
     *
     * @return The FEN representation.
     */
    public String getFen() {
        StringBuilder fen = new StringBuilder();
        fen.append(getBoardFen()).append(" ");
        if (turn == Color.WHITE) {
            fen.append("w");
        } else {
            fen.append("b");
        }
        fen.append(" ");
        fen.append(getCastlingFen()).append(" ");
        fen.append(this.epSquare == null ? "-" : this.epSquare.getName()).append(" ");

        fen.append(this.halfMoveClock).append(" ");
        fen.append(this.fullMoveNumber);
        return fen.toString();
    }

    /**
     * Gets the castling part of the FEN.
     *
     * <p>The string represents the individual castling rights for king- and queenside castling for both sides.
     *
     * @return The castling part of the FEN. Returns "KQkq" if both sides can castle king- and queenside.
     *         If neither side can castle "-" is returned.
     */
    private String getCastlingFen() {
        StringBuilder castlingFen = new StringBuilder();

        for (Color color : Color.values()) {
            Square kingSquare = getKingSquare(color);
            if (kingSquare == null) {
                continue;
            }
            char kingFile = kingSquare.getFile();
            long backrank = color == Color.WHITE ? RANK_1 : RANK_8;

            for (int rookSquare : BitboardUtils.scanReversed(cleanCastlingRights() & backrank)) {
                char rookFile = Square.fromIndex(rookSquare).getFile();
                char c;
                if (rookFile < kingFile) {
                    c = 'q';
                } else {
                    c = 'k';
                }
                castlingFen.append(color == Color.WHITE ? Character.toUpperCase(c) : c);
            }
        }
        if (castlingFen.length() == 0) {
            castlingFen.append("-");
        }

        return castlingFen.toString();
    }

    /**
     * Updates the position with the given move and puts it onto the move stack.
     *
     * @param move The move. A null move just switches the turn.
     */
    public void push(Move move) {
        if (move == null) {
            this.turn = this.turn.other();
            return;
        }

        BoardState state = this.getBoardState();
        this.castlingRights = cleanCastlingRights();
        this.stateStack.push(state);
        this.moveStack.addLast(move);

        Square epSquare = this.epSquare;
        this.epSquare = null;

        // increment move counters
        this.halfMoveClock++;
        if (this.turn == Color.BLACK) {
            this.fullMoveNumber++;
        }

        // zero the half move clock
        if (this.isZeroingMove(move)) {
            this.halfMoveClock = 0;
        }

        long sourceMask = SQUARES[move.getSource().ordinal()];
        long targetMask = SQUARES[move.getTarget().ordinal()];

        boolean promoted = (this.promoted & sourceMask) != 0;
        PieceType type = removePieceType(move.getSource());

        if (type == null) {
            throw new IllegalMoveException("No piece at source square");
        }

        Square captureSquare = move.getTarget();
        PieceType capturedPieceType = pieceTypeAt(captureSquare);

        // update castling rights
        this.castlingRights &= ~targetMask & ~sourceMask;
        if (type == PieceType.KING && !promoted) {
            if (turn == Color.WHITE) {
                this.castlingRights &= ~RANK_1;
            } else {
                this.castlingRights &= ~RANK_8;
            }
        }

        if (type == PieceType.PAWN) {
            int diff = move.getTarget().ordinal() - move.getSource().ordinal();

            if (diff == 16 && move.getSource().getRank() == 2) {
                this.epSquare = Square.fromIndex(move.getSource().ordinal() + 8);
            } else if (diff == -16 && move.getSource().getRank() == 7) {
                this.epSquare = Square.fromIndex(move.getSource().ordinal() - 8);
            } else if (move.getTarget() == epSquare && (Math.abs(diff) == 7 || Math.abs(diff) == 9)
                       && capturedPieceType == null) {
                captureSquare = Square.fromIndex(epSquare.ordinal() - 8 * turn.forwardDirection());
                capturedPieceType = removePieceType(captureSquare);

            }
        }

        if (move.getPromotion() != null) {
            promoted = true;
            type = move.getPromotion();
        }

        boolean castling =
                type == PieceType.KING && (move.getTarget().getFile() == 'g' || move.getTarget().getFile() == 'c');
        if (castling) {
            boolean queenSide = move.getTarget().getFile() < move.getSource().getFile();
            removePieceType(move.getSource());
            if (queenSide) {
                removePieceType(turn == Color.WHITE ? Square.A1 : Square.A8);
                setPiece(turn == Color.WHITE ? Square.C1 : Square.C8, PieceType.KING, turn);
                setPiece(turn == Color.WHITE ? Square.D1 : Square.D8, PieceType.ROOK, turn);
            } else {
                removePieceType(turn == Color.WHITE ? Square.H1 : Square.H8);
                setPiece(turn == Color.WHITE ? Square.G1 : Square.G8, PieceType.KING, turn);
                setPiece(turn == Color.WHITE ? Square.F1 : Square.F8, PieceType.ROOK, turn);
            }
        } else {
            setPiece(move.getTarget(), type, this.turn, promoted);
            if (capturedPieceType != null) {
                this.halfMoveClock = 0;
            }
        }
        this.turn = turn.other();
    }

    /**
     * Restores the previous position and returns the last move from the move stack.
     *
     * @return The last move of the move stack.
     * @throws NoSuchElementException if the move stack is empty.
     */
    public Move pop() {
        Move move = this.moveStack.removeLast();
        this.stateStack.pop().restore(this);

        return move;
    }

    /**
     * Gets the last move from the move stack.
     *
     * @return The last move or <code>null</code> if the move stack is empty.
     */
    public Move peek() {
        return this.moveStack.peekLast();
    }

    /**
     * Checks if the given move is a capture or pawn move.
     *
     * <p>Such a move resets the halfmove counter to 0.
     *
     * @param move The move to be checked.
     * @return True if the given move is a capture or a pawn move, false otherwise.
     */
    private boolean isZeroingMove(Move move) {
        long moveMask = SQUARES[move.getSource().ordinal()] ^ SQUARES[move.getTarget().ordinal()];

        return (moveMask & this.pawns) != 0 || (moveMask & this.occupiedColor[turn.other().ordinal()]) != 0;
    }

    /**
     * Returns a new board state of the current position.
     *
     * @return The board state of the current position.
     * @see BoardState
     */
    private BoardState getBoardState() {
        return new BoardState(this);
    }

    /**
     * Parses the given string into a move, makes the move and puts it onto the move stack.
     *
     * @param san A string in standard algebraic notation (SAN).
     * @return The parsed move.
     */
    public Move pushSan(String san) {
        Move move = this.parseSan(san);
        this.push(move);
        return move;
    }

    /**
     * Parses the given string into a move and returns it.
     *
     * @param san A string in standard algebraic notation (SAN).
     * @return The parsed move.
     * @throws AmbiguousMoveException if the SAN is ambiguous.
     * @throws IllegalMoveException   if the SAN is illegal.
     */
    public Move parseSan(String san) {
        try {
            if (Arrays.asList("O-O", "O-O+", "O-O#", "0-0", "0-0+", "0-0#").contains(san)) {
                return generateCastlingMoves().stream()
                        .filter(this::isKingsideCastling)
                        .findFirst()
                        .orElseThrow(() -> new IllegalMoveException("Illegal san: " + san + " in " + getFen()));
            } else if (Arrays.asList("O-O-O", "O-O-O+", "O-O-O#", "0-0-0", "0-0-0+", "0-0-0#").contains(san)) {
                return generateCastlingMoves().stream()
                        .filter(this::isQueensideCastling)
                        .findFirst()
                        .orElseThrow(() -> new IllegalMoveException("Illegal san: " + san + " in " + getFen()));
            }
        } catch (Exception e) {
            throw new IllegalMoveException("Illegal san: " + san + " in " + getFen());
        }

        String regex = "^([NBKRQ])?([a-h])?([1-8])?[\\-x]?([a-h][1-8])(=?[nbrqkNBRQK])?[+#]?\\Z";
        Matcher matcher = Pattern.compile(regex).matcher(san);

        if (!matcher.matches()) {
            throw new InvalidSanException("Invalid san: " + san + " in " + getFen());
        }

        // filter promotion type
        String promotion = matcher.group(5);
        PieceType promotionType = null;

        if (promotion != null) {
            promotionType = PieceType.fromSymbol(promotion.charAt(1)); //TODO
        }

        // filter source square
        long sourceMask = ALL;

        String file = matcher.group(2);
        if (file != null) {
            sourceMask &= FILES[file.charAt(0) - 'a'];
        }

        String rank = matcher.group(3);
        if (rank != null) {
            sourceMask &= RANKS[rank.charAt(0) - '1'];
        }

        // filter target square
        Square targetSquare = Square.parseSquare(matcher.group(4));
        long targetMask = SQUARES[targetSquare.ordinal()] & ~this.occupiedColor[turn.ordinal()];

        // filter piece type
        PieceType pieceType = PieceType.PAWN;
        String piece = matcher.group(1);
        if (piece != null) {
            pieceType = PieceType.fromSymbol(piece.charAt(0));
            sourceMask &= pieceMask(pieceType, this.turn);
        } else {
            sourceMask &= this.pawns;

            // no pawn capture when file not specified
            if (file == null) {
                sourceMask &= FILES[targetSquare.getFileIndex()];
            }
        }

        // match legal moves
        Move match = null;
        for (Move move : generateLegalMoves(sourceMask, targetMask)) {
            if (move.getPromotion() != promotionType) {
                continue;
            }
            if (move.getTarget() == targetSquare) {
                if (match != null) {
                    throw new AmbiguousMoveException("Ambiguous san: " + san + " in " + getFen());
                }
                match = move;
            }
        }

        if (match == null) {
            throw new IllegalMoveException("Illegal san: " + san + " in " + getFen());
        }

        return match;
    }

    /**
     * Returns a set of possible problems in the position.
     *
     * @return A set of states indicating different problems.
     * @see Status
     */
    public EnumSet<Status> status() {
        EnumSet<Status> errors = EnumSet.noneOf(Status.class);

        if (this.occupied == 0) {
            errors.add(Status.EMPTY);
        }

        if ((this.occupiedColor[Color.WHITE.ordinal()] & this.kings) == 0) {
            errors.add(Status.NO_WHITE_KING);
        }

        if ((this.occupiedColor[Color.BLACK.ordinal()] & this.kings) == 0) {
            errors.add(Status.NO_BLACK_KING);
        }

        if (Long.bitCount(this.occupied & this.kings) > 2) {
            errors.add(Status.TOO_MANY_KINGS);
        }

        if (Long.bitCount(this.occupiedColor[Color.WHITE.ordinal()]) > 16) {
            errors.add(Status.TOO_MANY_WHITE_PIECES);
        }

        if (Long.bitCount(this.occupiedColor[Color.BLACK.ordinal()]) > 16) {
            errors.add(Status.TOO_MANY_BLACK_PIECES);
        }

        if (Long.bitCount(this.occupiedColor[Color.WHITE.ordinal()] & this.pawns) > 8) {
            errors.add(Status.TOO_MANY_WHITE_PAWNS);
        }

        if (Long.bitCount(this.occupiedColor[Color.BLACK.ordinal()] & this.pawns) > 8) {
            errors.add(Status.TOO_MANY_BLACK_PAWNS);
        }

        if ((this.pawns & BACKRANK) != 0) {
            errors.add(Status.PAWNS_ON_BACKRANK);
        }

        if (this.castlingRights != this.cleanCastlingRights()) {
            errors.add(Status.BAD_CASTLING_RIGHTS);
        }

        Square validEpSquare = this.validEpSquare();
        if (this.epSquare != validEpSquare) {
            errors.add(Status.INVALID_EP_SQUARE);
        }

        if (this.wasIntoCheck()) {
            errors.add(Status.OPPOSITE_CHECK);
        }

        long checkers = this.checkers_mask();
        long ourKings = this.kings & this.occupiedColor[turn.ordinal()];

        if (checkers != 0) {
            if (Long.bitCount(checkers) > 2) {
                errors.add(Status.TOO_MANY_CHECKERS);
            }

            if (validEpSquare != null) {
                int pushedTo = validEpSquare.ordinal() ^ 8;
                int pushedFrom = validEpSquare.ordinal() ^ 24;
                long occupiedBefore = (this.occupied & ~SQUARES[pushedTo]) | SQUARES[pushedFrom];
                if (Long.bitCount(checkers) > 1
                    || (BitboardUtils.msb(checkers) != pushedTo && attackedForKing(ourKings, occupiedBefore))) {
                    errors.add(Status.IMPOSSIBLE_CHECK);
                }
            } else {
                if (Long.bitCount(checkers) > 2
                    || (
                            Long.bitCount(checkers) == 2
                            && (BitboardUtils.ray(BitboardUtils.lsb(checkers), BitboardUtils.msb(checkers)) & ourKings)
                               != 0)) {
                    errors.add(Status.IMPOSSIBLE_CHECK);
                }
            }
        }

        if (errors.isEmpty()) {
            errors.add(Status.VALID);
        }

        return errors;
    }

    /**
     * Returns a mask indicating all pieces that are giving check.
     *
     * @return A mask of all pieces that are giving check.
     */
    private long checkers_mask() {
        Square kingSquare = getKingSquare(this.turn);
        return kingSquare == null ? 0 : attackersMask(this.turn.other(), kingSquare);
    }

    /**
     * Checks if the En Passant square is valid.
     *
     * @return The En Passant square if it is valid, otherwise null.
     */
    private Square validEpSquare() {
        if (this.epSquare == null) {
            return null;
        }

        long epRank;
        long pawnMask;
        long seventhRankMask;
        if (this.turn == Color.WHITE) {
            epRank = 6;
            pawnMask = BitboardUtils.shiftDown(SQUARES[this.epSquare.ordinal()]);
            seventhRankMask = BitboardUtils.shiftUp(SQUARES[this.epSquare.ordinal()]);
        } else {
            epRank = 3;
            pawnMask = BitboardUtils.shiftUp(SQUARES[this.epSquare.ordinal()]);
            seventhRankMask = BitboardUtils.shiftDown(SQUARES[this.epSquare.ordinal()]);
        }

        if (this.epSquare.getRank() != epRank) {
            return null;
        }

        if ((this.pawns & pawnMask & this.occupiedColor[turn.other().ordinal()]) == 0) {
            return null;
        }

        if ((this.occupied & SQUARES[this.epSquare.ordinal()]) != 0) {
            return null;
        }

        if ((this.occupied & seventhRankMask) != 0) {
            return null;
        }

        return this.epSquare;
    }

    /**
     * Checks if the current color to move is attacking the others color king.
     *
     * @return True if the current color to move is attacking the others color king, false otherwise.
     */
    private boolean wasIntoCheck() {
        Square kingSquare = this.getKingSquare(turn.other());
        return kingSquare != null && isAttackedBy(turn, kingSquare);
    }

    /**
     * Checks if the status of the position is valid.
     *
     * <p>This means there are no problems in the position.
     *
     * @return True if the position is valid, false otherwise.
     */
    public boolean isValid() {
        return status().contains(Status.VALID);
    }

    /**
     * Finds a matching legal move for the given source and target square.
     *
     * <p>Pawns are promoted to {@link PieceType#QUEEN}.
     *
     * @param sourceSquare The source square.
     * @param targetSquare The target square.
     * @return The matching legal move.
     */
    public Move findMove(Square sourceSquare, Square targetSquare) {
        return findMove(sourceSquare, targetSquare, null);
    }

    /**
     * Finds a matching legal move for the given source and target square
     * and an optional promotion piece type.
     *
     * <p>Pawns are promoted to {@link PieceType#QUEEN} by default, unless otherwise specified.
     *
     * @param sourceSquare The source square.
     * @param targetSquare The target square.
     * @param promotion    The promotion piece type. If null, a pawn is promoted to a queen.
     * @return The matching legal move.
     */
    public Move findMove(Square sourceSquare, Square targetSquare, PieceType promotion) {
        if (promotion == null && (this.pawns & SQUARES[sourceSquare.ordinal()]) != 0
            && (SQUARES[targetSquare.ordinal()] & BACKRANK) != 0) {
            promotion = PieceType.QUEEN;
        }

        Move move = new Move(sourceSquare, targetSquare, promotion);

        if (!this.isLegal(move)) {
            throw new IllegalMoveException("Illegal move: " + move + " in " + getFen());
        }

        return move;
    }


    /**
     * Checks if the given color has insufficient material to checkmate the opponent.
     *
     * @param color The color to check.
     * @return True if the given color has insufficient material to checkmate the opponent, false otherwise.
     */
    public boolean hasInsufficientMaterial(Color color) {
        long colorMask = this.occupiedColor[color.ordinal()];

        if ((colorMask & (this.rooks | this.queens | this.pawns)) != 0) {
            return false;
        }

        if ((colorMask & this.knights) != 0) {
            return (Long.bitCount(colorMask) <= 2 && (this.occupied & ~colorMask & ~this.kings & ~this.queens) == 0);
        }

        if ((colorMask & this.bishops) != 0) {
            boolean sameColor = (this.bishops & DARK_SQUARES) == 0 || (this.bishops & LIGHT_SQUARES) == 0;
            return sameColor && this.pawns == 0 && this.knights == 0;
        }

        return true;
    }

    /**
     * Checks if both colors have insufficient material to checkmate each other.
     *
     * @return True if both colors have insufficient material to checkmate each other, false otherwise.
     */
    public boolean isInsufficientMaterial() {
        return hasInsufficientMaterial(Color.WHITE) && hasInsufficientMaterial(Color.BLACK);
    }

    /**
     * Checks if the current side to move is in check.
     *
     * @return True if the current side to move is in check, false otherwise.
     */
    public boolean isCheck() {
        return checkers_mask() != 0;
    }

    /**
     * Checks if the current side to move is in checkmate.
     *
     * @return True if the current side to move is in checkmate, false otherwise.
     */
    public boolean isCheckmate() {
        return isCheck() && generateLegalMoves().isEmpty();
    }

    /**
     * Checks if the current side to move is in stalemate.
     *
     * @return True if the current side to move is in stalemate, false otherwise.
     */
    public boolean isStalemate() {
        return !isCheck() && generateLegalMoves().isEmpty();
    }

    /**
     * Detects the end of the game.
     *
     * <p>The game can be over by:
     * <ul>
     *     <li>Checkmate</li>
     *     <li>Stalemate</li>
     *     <li>Insufficient material</li>
     *     <li>Claim Fifty moves rule</li>
     *     <li>Claim Threefold repetition</li>
     *     <li>Fivefold repetition</li>
     *     <li>Seventy-five moves rule</li>
     * </ul>
     *
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return outcome() != null;
    }

    /**
     * Detects the end of the game with the option to claim draw.
     *
     * @param claimDraw If true, the game can end by claiming a draw. This includes the Fifty moves rule
     *                  and the Threefold repetition.
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver(boolean claimDraw) {
        return outcome(claimDraw) != null;
    }

    /**
     * Gets the result of the game.
     *
     * @return The result of the game, e.g. "1-0", "0-1" or "1/2-1/2". "*" if the game is not over.
     */
    public String result() {
        return result(false);
    }

    /**
     * Gets the result of the game with the option to claim draw.
     *
     * @param claimDraw If true, the game can end by claiming a draw.
     *                  This includes the Fifty moves rule and the Threefold repetition.
     * @return The result of the game, e.g. "1-0", "0-1" or "1/2-1/2". "*" if the game is not over.
     */
    public String result(boolean claimDraw) {
        Outcome outcome = outcome(claimDraw);
        return outcome != null ? outcome.result() : "*";
    }

    /**
     * Gets the outcome of the game.
     *
     * @return The outcome of the game or null if the game is not over.
     * @see Outcome
     */
    public Outcome outcome() {
        return outcome(false);
    }

    /**
     * Gets the outcome of the game with the option to claim draw.
     *
     * @param claimDraw If true, the game can end by claiming a draw.
     *                  This includes the Fifty moves rule and the Threefold repetition.
     * @return The outcome of the game or null if the game is not over.
     * @see Outcome
     */
    public Outcome outcome(boolean claimDraw) {
        if (isCheckmate()) {
            return new Outcome(Termination.CHECKMATE, turn.other());
        }

        if (isInsufficientMaterial()) {
            return new Outcome(Termination.INSUFFICIENT_MATERIAL, null);
        }

        if (generateLegalMoves().isEmpty()) {
            return new Outcome(Termination.STALEMATE, null);
        }

        if (isSeventyFiveMoves()) {
            return new Outcome(Termination.SEVENTYFIVE_MOVES, null);
        }

        if (isFivefoldRepetition()) {
            return new Outcome(Termination.FIVEFOLD_REPETITION, null);
        }

        if (claimDraw) {
            if (canClaimFiftyMoveRule()) {
                return new Outcome(Termination.FIFTY_MOVES, null);
            }
            if (canClaimThreefoldRepetition()) {
                return new Outcome(Termination.THREEFOLD_REPETITION, null);
            }
        }

        return null;
    }

    /**
     * Checks if the player to move can claim a draw by the Fifty moves rule.
     *
     * <p>This also checks if there is a legal move that achieves the Fifty moves rule.
     *
     * @return True if the player to move can claim a draw by the Fifty moves rule, false otherwise.
     */
    public boolean canClaimFiftyMoveRule() {
        if (isFiftyMoves()) {
            return true;
        }

        if (this.halfMoveClock >= 99) {
            for (Move move : generateLegalMoves()) {
                if (!isZeroingMove(move)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if the game is a draw by the Seventyfive move rule.
     *
     * @return True if the game is a draw by the Seventyfive move rule, false otherwise.
     */
    private boolean isSeventyFiveMoves() {
        return this.isHalfmoves(150);
    }

    /**
     * Checks if the halfmove clock is greater or equal to 100.
     *
     * <p>The halfmove clock counts the number of halfmoves since the last capture or pawn move.
     *
     * @return True if the halfmove clock is greater or equal to 100, false otherwise.
     */
    public boolean isFiftyMoves() {
        return this.isHalfmoves(100);
    }

    /**
     * Checks if the halfmove clock is greater or equal to the given n.
     *
     * @param n The number of halfmoves.
     * @return True if the halfmove clock is greater or equal to the given n, false otherwise.
     */
    private boolean isHalfmoves(int n) {
        return this.halfMoveClock >= n && !generateLegalMoves().isEmpty();
    }

    /**
     * Gets the standard algebraic notation (SAN) of the given move in the context of the current position.
     *
     * @param move The move.
     * @return The SAN of the move.
     */
    public String san(Move move) {
        return this.algebraicNotation(move, false);
    }

    /**
     * Gets the long algebraic notation (LAN) of the given move in the context of the current position.
     *
     * @param move The move.
     * @return The LAN of the move.
     */
    public String lan(Move move) {
        return this.algebraicNotation(move, true);
    }

    /**
     * Gets the short or long algebraic notation of the given move in the context of the current position.
     *
     * @param move         The move.
     * @param longNotation If true, the long algebraic notation is used, otherwise the short algebraic notation.
     * @return The algebraic notation of the move.
     */
    private String algebraicNotation(Move move, boolean longNotation) {
        String san = algebraicNotationPush(move, longNotation);
        this.pop();
        return san;
    }

    /**
     * Make the given move and return the algebraic notation of the move.
     *
     * @param move         The move.
     * @param longNotation If true, the long algebraic notation is used, otherwise the short algebraic notation.
     * @return The algebraic notation of the move.
     */
    private String algebraicNotationPush(Move move, boolean longNotation) {
        String san = this.algebraicNotationWithoutSuffix(move, longNotation);
        this.push(move);
        boolean isCheck = this.isCheck();
        boolean isCheckmate = isCheck && this.isCheckmate();

        if (isCheckmate) {
            return san + "#";
        } else if (isCheck) {
            return san + "+";
        }
        return san;
    }

    /**
     * Make the given move and return the standard algebraic notation (SAN) of the move.
     *
     * @param move The move.
     * @return The SAN of the move.
     */
    public String sanAndPush(Move move) {
        return algebraicNotationPush(move, false);
    }

    /**
     * Gets the algebraic notation of the given move without suffix in the context of the current position.
     *
     * <p>Suffixes like '+' for check and '#' for checkmate are not considered.
     *
     * @param move         The move.
     * @param longNotation If true, the long algebraic notation is used, otherwise the short algebraic notation.
     * @return The algebraic notation of the move without suffix.
     */
    private String algebraicNotationWithoutSuffix(Move move, boolean longNotation) {
        if (this.isCastling(move)) {
            if (move.getTarget().getFileIndex() < move.getSource().getFileIndex()) {
                return "O-O-O";
            } else {
                return "O-O";
            }
        }

        PieceType pieceType = this.pieceTypeAt(move.getSource());
        if (pieceType == null) {
            throw new IllegalMoveException("No piece at source square");
        }

        boolean isCapture = isCapture(move);

        StringBuilder san = new StringBuilder();
        if (pieceType != PieceType.PAWN) {
            san.append(Character.toUpperCase(pieceType.getSymbol()));
        }

        if (longNotation) {
            san.append(move.getSource().getName());
        } else if (pieceType != PieceType.PAWN) {
            long others = 0;
            long sourceMask = this.pieceMask(pieceType, this.turn);
            sourceMask &= ~SQUARES[move.getSource().ordinal()];
            long targetMask = SQUARES[move.getTarget().ordinal()];

            for (Move candidate : generateLegalMoves(sourceMask, targetMask)) {
                others |= SQUARES[candidate.getSource().ordinal()];
            }

            if (others != 0) {
                boolean row = false;
                boolean column = false;

                if ((others & RANKS[move.getSource().getRankIndex()]) != 0) {
                    column = true;
                }

                if ((others & FILES[move.getSource().getFileIndex()]) != 0) {
                    row = true;
                } else {
                    column = true;
                }
                if (column) {
                    san.append(move.getSource().getFile());
                }
                if (row) {
                    san.append(move.getSource().getRank());
                }
            }
        } else if (isCapture) {
            san.append(move.getSource().getFile());
        }

        if (isCapture) {
            san.append("x");
        } else if (longNotation) {
            san.append("-");
        }

        san.append(move.getTarget().getName());

        if (move.getPromotion() != null) {
            san.append("=").append(Character.toUpperCase(move.getPromotion().getSymbol()));
        }

        return san.toString();
    }

    /**
     * Checks if the given move is a capture.
     *
     * @param move The move.
     * @return True if the given move is a capture, false otherwise.
     */
    public boolean isCapture(Move move) {
        long targetMask = SQUARES[move.getTarget().ordinal()];
        long otherColorMask = this.occupiedColor[turn.other().ordinal()];

        return (targetMask & otherColorMask) != 0 || this.isEnPassant(move);
    }

    /**
     * Gets a string representing the given sequence of moves in standard algebraic notation (SAN).
     *
     * <p>The string also contains the full move numbers, e.g. "1. e4 e5 2. Nf3 Nc6".
     * This operation doesn't modify the board.
     *
     * @param variation The sequence of moves as an Iterable.
     * @return The SAN of the given sequence of moves.
     */
    public String variationSan(Iterable<Move> variation) {
        StringBuilder san = new StringBuilder();
        Board board = this.copy();

        for (Move move : variation) {
            if (!board.isLegal(move)) {
                throw new IllegalMoveException("Illegal move: " + move + " in " + getFen());
            }

            if (board.turn == Color.WHITE) {
                san.append(board.fullMoveNumber)
                        .append(". ")
                        .append(board.sanAndPush(move))
                        .append(" ");
            } else if (san.length() == 0) {
                san.append(board.fullMoveNumber)
                        .append("...")
                        .append(board.sanAndPush(move))
                        .append(" ");
            } else {
                san.append(board.sanAndPush(move))
                        .append(" ");
            }
        }

        if (san.length() > 0) {
            san.deleteCharAt(san.length() - 1);
        }

        return san.toString();
    }

    /**
     * Parses the given string in UCI notation into a move.
     *
     * @param uci The string in UCI notation.
     * @return The parsed move.
     * @throws IllegalMoveException if the move is illegal.
     */
    public Move parseUci(String uci) {
        Move move = Move.fromUci(uci);

        if (!this.isLegal(move)) {
            throw new IllegalMoveException("Illegal move: " + move + " in " + getFen());
        }
        return move;
    }

    /**
     * Parses the given string in UCI notation into a move, makes the move and puts it onto the move stack.
     *
     * @param uci The string in UCI notation.
     * @return The parsed move.
     * @throws IllegalMoveException if the move is illegal.
     */
    public Move pushUci(String uci) {
        Move move = parseUci(uci);
        this.push(move);
        return move;
    }

    /**
     * Gets the current color to move.
     *
     * @return The current color to move.
     */
    public Color getTurn() {
        return turn;
    }

    /**
     * Gets the En Passant square.
     *
     * @return The En Passant square or null if there is no En Passant square.
     */
    public Square getEpSquare() {
        return epSquare;
    }

    /**
     * Checks if the player to move can claim a draw by Fifty moves rule or by treefold repetition.
     *
     * @return True if the player to move can claim a draw by Fifty moves rule or by treefold repetition,
     *         false otherwise.
     */
    public boolean canClaimDraw() {
        return canClaimFiftyMoveRule() || canClaimThreefoldRepetition();
    }

    /**
     * Checks if the player to move can claim a draw by treefold repetition.
     *
     * <p>If the position occurred the third time or if there is a legal move to reach such a repetition
     * a draw can be claimed.
     *
     * @return True if the player to move can claim a draw by treefold repetition, false otherwise.
     */
    public boolean canClaimThreefoldRepetition() {
        int transpositionKey = this.hashCode();
        Map<Integer, Integer> transpositions = new HashMap<>();
        transpositions.put(transpositionKey, 1);

        Deque<Move> switchyard = new ArrayDeque<>();
        while (!this.moveStack.isEmpty()) {
            Move move = this.pop();
            switchyard.push(move);

            if (isIrreversible(move)) {
                break;
            }

            int nextTranspositionKey = this.hashCode();
            transpositions.put(nextTranspositionKey, transpositions.getOrDefault(nextTranspositionKey, 0) + 1);
        }

        while (!switchyard.isEmpty()) {
            this.push(switchyard.pop());
        }

        if (transpositions.get(transpositionKey) >= 3) {
            return true;
        }

        for (Move move : generateLegalMoves()) {
            push(move);
            try {
                int nextTranspositionKey = hashCode();
                if (transpositions.getOrDefault(nextTranspositionKey, 0) >= 2) {
                    return true;
                }
            } finally {
                pop();
            }
        }

        return false;
    }

    /**
     * Checks if the given move is irreversible.
     *
     * <p>A move is irreversible if it is a capture, pawn move, reduces the castling rights or rejects En Passant.
     *
     * @param move The move to be checked.
     * @return True if the given move is irreversible, false otherwise.
     */
    public boolean isIrreversible(Move move) {
        return isZeroingMove(move) || hasLegalEnPassant() || reducesCastlingRights(move);
    }

    /**
     * Checks if the given move reduces the castling rights.
     *
     * <p>King and rook moves from their initial square are reducing the castling rights.
     *
     * @param move The move to be checked.
     * @return True if the given move reduces the castling rights, false otherwise.
     */
    private boolean reducesCastlingRights(Move move) {
        long castlingRights = this.cleanCastlingRights();
        long touched = SQUARES[move.getSource().ordinal()] ^ SQUARES[move.getTarget().ordinal()];

        return (touched & castlingRights) != 0
               || (
                       (castlingRights & RANK_1) != 0
                       && (touched & this.kings & this.occupiedColor[Color.WHITE.ordinal()] & ~this.promoted) != 0)
               || (
                       (castlingRights & RANK_8) != 0
                       && (touched & this.kings & this.occupiedColor[Color.BLACK.ordinal()] & ~this.promoted) != 0);
    }

    /**
     * Checks if the current position has repeated 5 times.
     *
     * @return True if the current position has repeated 5 times, false otherwise.
     */
    public boolean isFivefoldRepetition() {
        return isRepetition(5);
    }

    /**
     * Checks if the current position has repeated 3 times.
     *
     * @return True if the current position has repeated 3 times, false otherwise.
     */
    public boolean isRepetition() {
        return isRepetition(3);
    }

    /**
     * Checks if the current position has repeated the given count of times.
     *
     * @param count The count of repetitions.
     * @return True if the current position has repeated the given count of times, false otherwise.
     */
    public boolean isRepetition(int count) {
        int maybeRepetitions = 1;

        for (BoardState state : this.stateStack) {
            if (state.occupied == this.occupied) {
                maybeRepetitions++;

                if (maybeRepetitions >= count) {
                    break;
                }
            }
        }

        if (maybeRepetitions < count) {
            return false;
        }

        int transpositionKey = this.hashCode();
        Deque<Move> switchyard = new ArrayDeque<>();

        try {
            while (true) {
                if (count <= 1) {
                    return true;
                }

                if (this.moveStack.size() < count - 1) {
                    break;
                }

                Move move = this.pop();
                switchyard.push(move);

                if (isIrreversible(move)) {
                    break;
                }

                if (hashCode() == transpositionKey) {
                    count--;
                }
            }
        } finally {
            while (!switchyard.isEmpty()) {
                this.push(switchyard.pop());
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Board board = (Board) o;
        return fullMoveNumber == board.fullMoveNumber
               && halfMoveClock == board.halfMoveClock
               && hashCode() == board.hashCode();
    }

    @Override
    public int hashCode() {
        if (hasLegalEnPassant()) {
            return Objects.hash(super.hashCode(), turn, cleanCastlingRights(), epSquare);
        }
        return Objects.hash(super.hashCode(), turn, cleanCastlingRights());
    }

    /**
     * Returns a mirrored copy of the board.
     *
     * <p>The board is mirrored vertically. This also swaps the En Passant square, the castling rights and turn.
     *
     * @return The mirrored copy of the board.
     */
    public Board mirror() {
        Board board = this.copy();
        board.applyMirror();
        return board;
    }

    /**
     * Mirrors the board and swaps the turn.
     *
     * <p>The board is mirrored vertically. This operation swaps the white and black pieces.
     */
    @Override
    public void applyMirror() {
        super.applyMirror();
        this.turn = this.turn.other();
    }

    /**
     * Applies a transform function to the board.
     *
     * @param transform The transform function.
     */
    @Override
    public void applyTransform(Function<Long, Long> transform) {
        super.applyTransform(transform);
        this.clearStack();
        if (this.epSquare != null) {
            int index = BitboardUtils.msb(transform.apply(SQUARES[epSquare.ordinal()]));
            this.epSquare = Square.fromIndex(index);
        }
        this.castlingRights = transform.apply(this.castlingRights);
    }
}
