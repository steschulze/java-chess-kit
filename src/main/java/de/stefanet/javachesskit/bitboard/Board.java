package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.*;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.stefanet.javachesskit.bitboard.Bitboard.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_8;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.*;

public class Board extends BaseBoard {
	protected final static String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	protected Color turn;
	protected long castlingRights;

	protected Square epSquare;

	protected int fullMoveNumber;
	protected int halfMoveClock;

	protected long promoted;
	protected Deque<Move> moveStack;
	protected Deque<BoardState> stateStack;

	public Board() {
		this(STARTING_FEN);
	}

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

	public static Board empty() {
		return new Board(null);
	}

	public Board copy() {
		Board board = new Board();
		board.pawns = this.pawns;
		board.knights = this.knights;
		board.bishops = this.bishops;
		board.rooks = this.rooks;
		board.queens = this.queens;
		board.kings = this.kings;

		board.whitePieces = this.whitePieces;
		board.blackPieces = this.blackPieces;
		board.occupied = this.occupied;
		board.promoted = this.promoted;

		board.epSquare = this.epSquare;
		board.castlingRights = this.castlingRights;
		board.turn = this.turn;
		board.fullMoveNumber = this.fullMoveNumber;
		board.halfMoveClock = this.halfMoveClock;

		return board;
	}

	public void setFen(String fen) {
		String[] parts = fen.split(" ");

		if (parts.length != 6) {
			throw new InvalidMoveException("FEN must have 6 parts, but only has " + parts.length);
		}

		if (!parts[1].equals("w") && !parts[1].equals("b")) {
			throw new InvalidMoveException("Turn part of the FEN is invalid: Expected w or b, but was " + parts[1]);
		}
		if (!Pattern.matches("^(KQ?k?q?|Qk?q?|kq?|q|-)$", parts[2])) {
			throw new InvalidMoveException("Castling part of the FEN is invalid");
		}
		if (!Pattern.matches("^(-|[a-h][36])$", parts[3])) {
			throw new InvalidMoveException("En-passant part of the FEN is invalid");
		}
		if (!Pattern.matches("^(0|[1-9][0-9]*)$", parts[4])) {
			throw new InvalidMoveException("Half move part of the FEN is invalid");
		}
		if (!Pattern.matches("^[1-9][0-9]*$", parts[5])) {
			throw new InvalidMoveException("Full move part of the FEN is invalid");
		}

		setBoardFen(parts[0]);
		this.turn = Color.fromSymbol(parts[1].charAt(0));
		setCastlingFen(parts[2]);
		this.epSquare = parts[3].equals("-") ? null : Square.parseSquare(parts[3]);
		this.halfMoveClock = Integer.parseInt(parts[4]);
		this.fullMoveNumber = Integer.parseInt(parts[5]);
		clearStack();
	}

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

	private void reset() {
		this.turn = Color.WHITE;
		this.castlingRights = A1 | H1 | A8 | H8;
		this.epSquare = null;
		this.halfMoveClock = 0;
		this.fullMoveNumber = 1;
		resetBoard();
	}

	/**
	 *
	 */
	@Override
	protected void resetBoard() {
		super.resetBoard();
		clearStack();
	}

	void clear() {
		this.turn = Color.WHITE;
		this.castlingRights = 0;
		this.epSquare = null;
		this.halfMoveClock = 0;
		this.fullMoveNumber = 1;

		clearBoard();
	}


	@Override
	protected void clearBoard() {
		super.clearBoard();
		clearStack();
	}

	protected void clearStack() {
		this.moveStack.clear();
		this.stateStack.clear();
	}

	public int ply() {
		return 2 * (this.fullMoveNumber - 1) + this.turn.ordinal();
	}

	public LegalMoveGenerator legalMoves() {
		return new LegalMoveGenerator(this);
	}

	public PseudoLegalMoveGenerator pseudoLegalMoves() {
		return new PseudoLegalMoveGenerator(this);
	}

	public Set<Move> generateLegalMoves(long fromMask, long toMask) {
		Set<Move> legalMoves = new HashSet<>();

		long colorMask = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;
		long kingMask = this.kings & colorMask;

		if (kingMask != 0) {
			int king = BitboardUtils.msb(kingMask);
			long blockers = sliderBlockers(king);
			long checkers = attackersMask(turn.other(), Square.fromIndex(king));

			if (checkers != 0) {
				for (Move move : generateEvations(king, checkers, fromMask, toMask)) {
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

	public Set<Move> generateLegalMoves() {
		return generateLegalMoves(ALL, ALL);
	}

	public Set<Move> generatePseudoLegalMoves() {
		return generatePseudoLegalMoves(Bitboard.ALL, Bitboard.ALL);
	}

	public Set<Move> generatePseudoLegalMoves(long sourceMask, long targetMask) {
		Set<Move> moveList = new HashSet<>();
		long ownPieces = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;

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
		if (pawns == 0) return moveList;

		// pawn captures
		long captures = pawns;
		for (int captureIndex : BitboardUtils.scanReversed(captures)) {
			Square source = Square.fromIndex(captureIndex);

			long targets = Bitboard.PAWN_ATTACKS[turn.ordinal()][captureIndex]
					& targetMask & (turn.equals(Color.WHITE) ? this.blackPieces : this.whitePieces);

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

	public Set<Move> generatePseudoLegalEnPassant() {
		return generatePseudoLegalEnPassant(ALL, ALL);
	}

	public Set<Move> generatePseudoLegalEnPassant(long sourceMask, long targetMask) {
		Set<Move> moves = new HashSet<>();

		if (epSquare == null || (SQUARES[epSquare.ordinal()] & targetMask) == 0) return moves;

		// epSquare is occupied
		if ((SQUARES[epSquare.ordinal()] & this.occupied) != 0) return moves;

		long colorMask = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;
		long rankMask = this.turn.equals(Color.WHITE) ? Bitboard.RANKS[4] : Bitboard.RANKS[3];
		long attackMask = PAWN_ATTACKS[turn.other().ordinal()][epSquare.ordinal()];

		long capturers = this.pawns & colorMask & sourceMask & attackMask & rankMask;

		for (int index : BitboardUtils.scanReversed(capturers)) {
			Square source = Square.fromIndex(index);
			moves.add(new Move(source, epSquare));
		}

		return moves;
	}

	public boolean hasLegalEnPassant() {
		return this.epSquare != null && !generateLegalEnPassant().isEmpty();
	}


	public Set<Move> generateLegalEnPassant() {
		return generateLegalEnPassant(ALL, ALL);
	}

	public Set<Move> generateLegalEnPassant(long sourceMask, long targetMask) {
		Set<Move> moves = new HashSet<>();

		for (Move move : generatePseudoLegalEnPassant(sourceMask, targetMask)) {
			if (!isKingInCheck(move)) {
				moves.add(move);
			}
		}

		return moves;
	}

	public Set<Move> generateCastlingMoves() {
		return generateCastlingMoves(ALL, ALL);
	}

	public Set<Move> generateCastlingMoves(long sourceMask, long targetMask) {
		Set<Move> moves = new HashSet<>();

		long backrank = this.turn.equals(Color.WHITE) ? RANK_1 : RANK_8;
		long colorMask = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;

		long king = colorMask & this.kings & ~this.promoted & backrank & sourceMask;
		king &= -king;

		if (king == 0) return moves;

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

			if (!(((this.occupied ^ king ^ rook) &
					(kingPath | rookPath | kingTarget | rookTarget)) != 0 ||
					attackedForKing(kingPath | king, this.occupied ^ king) ||
					attackedForKing(kingTarget, this.occupied ^ king ^ rook ^ rookTarget)
			)) {
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

	public Set<Move> generateLegalCaptures() {
		return generateLegalCaptures(ALL, ALL);
	}

	public Set<Move> generateLegalCaptures(long sourceMask, long targetMask) {
		long otherColorMask = this.turn == Color.WHITE ? this.blackPieces : this.whitePieces;
		Set<Move> moves = new HashSet<>();
		moves.addAll(generateLegalMoves(sourceMask, targetMask & otherColorMask));
		moves.addAll(generateLegalEnPassant(sourceMask, targetMask));

		return moves;

	}

	public Set<Move> generatePseudoLegalCaptures() {
		return generatePseudoLegalCaptures(ALL, ALL);
	}

	public Set<Move> generatePseudoLegalCaptures(long sourceMask, long targetMask) {
		long otherColorMask = this.turn == Color.WHITE ? this.blackPieces : this.whitePieces;
		Set<Move> moves = new HashSet<>();
		moves.addAll(generatePseudoLegalMoves(sourceMask, targetMask & otherColorMask));
		moves.addAll(generatePseudoLegalEnPassant(sourceMask, targetMask));

		return moves;

	}

	private boolean attackedForKing(long path, long occupied) {
		for (int index : BitboardUtils.scanReversed(path)) {
			if (attackersMask(turn.other(), Square.fromIndex(index), occupied) != 0) return true;
		}
		return false;
	}

	protected long cleanCastlingRights() {
		long castling = this.castlingRights & this.rooks;
		long whiteCastling = castling & RANK_1 & this.whitePieces;
		long blackCastling = castling & RANK_8 & this.blackPieces;

		whiteCastling &= (A1 | H1);
		blackCastling &= (A8 | H8);

		if ((this.whitePieces & this.kings & ~this.promoted & E1) == 0) {
			whiteCastling = 0;
		}
		if ((this.blackPieces & this.kings & ~this.promoted & E8) == 0) {
			blackCastling = 0;
		}
		return whiteCastling | blackCastling;
	}

	public boolean hasCastlingRights(Color color) {
		long backrank = color == Color.WHITE ? RANK_1 : RANK_8;
		return (this.castlingRights & backrank) != 0;
	}

	public boolean hasKingsideCastlingRights(Color color) {
		long backrank = color == Color.WHITE ? RANK_1 : RANK_8;
		long colorMask = color == Color.WHITE ? this.whitePieces : this.blackPieces;
		long kingMask = this.kings & colorMask & backrank;

		if (kingMask == 0) {
			return false;
		}

		long castlingRights = this.cleanCastlingRights() & backrank;

		return (castlingRights & FILE_H) != 0;
	}

	public boolean hasQueensideCastlingRights(Color color) {
		long backrank = color == Color.WHITE ? RANK_1 : RANK_8;
		long colorMask = color == Color.WHITE ? this.whitePieces : this.blackPieces;
		long kingMask = this.kings & colorMask & backrank;

		if (kingMask == 0) {
			return false;
		}

		long castlingRights = this.cleanCastlingRights() & backrank;

		return (castlingRights & FILE_A) != 0;
	}

	public boolean isLegal(Move move) {
		return isPseudoLegal(move) && !isKingInCheck(move);
	}

	private boolean isKingInCheck(Move move) {
		Square kingSquare = this.getKingSquare(this.turn);

		if (kingSquare == null) {
			return false;
		}

		long checkers = attackersMask(turn.other(), kingSquare);
		if (checkers != 0 &&
				!generateEvations(
						kingSquare.ordinal(),
						checkers,
						SQUARES[move.getSource().ordinal()],
						SQUARES[move.getTarget().ordinal()]
				).contains(move)) {
			return true;
		}

		return !isSafe(kingSquare, this.sliderBlockers(kingSquare.ordinal()), move);
	}

	private boolean isSafe(Square kingSquare, long blockers, Move move) {
		if (move.getSource() == kingSquare) {
			if (isCastling(move)) {
				return true;
			} else {
				return !isAttackedBy(turn.other(), move.getTarget());
			}
		} else if (isEnPassant(move)) {
			return (pinMask(turn, move.getSource()) & SQUARES[move.getTarget().ordinal()]) != 0 &&
					!epSkewered(kingSquare, move.getSource());
		} else {
			return (blockers & SQUARES[move.getSource().ordinal()]) == 0 ||
					(BitboardUtils.ray(move.getSource().ordinal(), move.getTarget().ordinal())
							& SQUARES[kingSquare.ordinal()]) != 0;
		}
	}

	private boolean epSkewered(Square kingSquare, Square capturer) {
		if (this.epSquare == null) {
			throw new IllegalStateException("No ep square");
		}

		int lastDouble = this.epSquare.ordinal() + ((this.turn == Color.WHITE) ? -8 : 8);

		long occupancy = (this.occupied & ~SQUARES[lastDouble] &
				~SQUARES[capturer.ordinal()] | SQUARES[this.epSquare.ordinal()]);

		long colorMask = this.turn.equals(Color.WHITE) ? this.blackPieces : this.whitePieces;
		long horizontalAttackers = colorMask & (this.rooks | this.queens);
		if ((RANK_ATTACKS.get(kingSquare.ordinal())
				.get((RANK_MASKS[kingSquare.ordinal()] & occupancy)) & horizontalAttackers) != 0) {
			return true;
		}

		long diagonalAttackers = colorMask & (this.bishops | this.queens);
		if ((DIAGONAL_ATTACKS.get(kingSquare.ordinal())
				.get((DIAGONAL_MASKS[kingSquare.ordinal()] & occupancy)) & diagonalAttackers) != 0) {
			return true;
		}

		return false;

	}

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
				long snipers = rays & attack[1] & this.occupied & (color == Color.WHITE ? this.blackPieces : this.whitePieces);
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

	public boolean isPinned(Color color, Square square) {
		return this.pinMask(color, square) != Bitboard.ALL;
	}

	public boolean isCastling(Move move) {
		if ((this.kings & SQUARES[move.getSource().ordinal()]) != 0) {
			int diff = move.getSource().getFileIndex() - move.getTarget().getFileIndex();
			long colorMask = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;
			return Math.abs(diff) > 1 || ((this.rooks & colorMask & SQUARES[move.getTarget().ordinal()]) != 0);
		}
		return false;
	}

	public boolean isKingsideCastling(Move move) {
		return isCastling(move) &&
				move.getTarget().getFileIndex() > move.getSource().getFileIndex();
	}

	public boolean isQueensideCastling(Move move) {
		return isCastling(move) &&
				move.getTarget().getFileIndex() < move.getSource().getFileIndex();
	}

	public boolean isEnPassant(Move move) {
		return (this.epSquare == move.getTarget() &&
				((this.pawns & SQUARES[move.getSource().ordinal()]) != 0) &&
				(Math.abs(move.getTarget().ordinal() - move.getSource().ordinal()) == 7 ||
						Math.abs(move.getTarget().ordinal() - move.getSource().ordinal()) == 9) &&
				((this.occupied & SQUARES[move.getTarget().ordinal()]) == 0));
	}

	private long sliderBlockers(int kingSquareIndex) {
		long rooksAndQueens = this.rooks | this.queens;
		long bishopsAndQueens = this.bishops | this.queens;

		long snipers = ((RANK_ATTACKS.get(kingSquareIndex).get(0L) & rooksAndQueens) |
				(FILE_ATTACKS.get(kingSquareIndex).get(0L) & rooksAndQueens) |
				(DIAGONAL_ATTACKS.get(kingSquareIndex).get(0L) & bishopsAndQueens));

		long blockers = 0;
		long colorMask = this.turn == Color.WHITE ? this.blackPieces : this.whitePieces;
		for (int sniper : BitboardUtils.scanReversed(snipers & colorMask)) {
			long b = BitboardUtils.between(kingSquareIndex, sniper) & this.occupied;

			if (b != 0 && SQUARES[BitboardUtils.msb(b)] == b) {
				blockers |= b;
			}
		}

		return blockers & (this.occupied & ~colorMask);
	}

	private Set<Move> generateEvations(int kingSquareIndex, long checkers, long sourceMask, long targetMask) {
		Set<Move> moves = new HashSet<>();

		long sliders = checkers & (this.bishops | this.rooks | this.queens);
		long attacked = 0;

		for (int checker : BitboardUtils.scanReversed(sliders)) {
			attacked |= BitboardUtils.ray(kingSquareIndex, checker) & ~SQUARES[checker];
		}
		if ((SQUARES[kingSquareIndex] & sourceMask) != 0) {
			long colorMask = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;
			long mask = KING_ATTACKS[kingSquareIndex] & ~colorMask & ~attacked & targetMask;
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

	public boolean isPseudoLegal(Move move) {
		PieceType type = pieceTypeAt(move.getSource());
		if (type == null) return false;

		long sourceMask = SQUARES[move.getSource().ordinal()];
		long targetMask = SQUARES[move.getTarget().ordinal()];

		long colorMask = turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;

		if ((colorMask & sourceMask) == 0)
			return false;

		if (move.getPromotion() != null) {
			if (type != PieceType.PAWN)
				return false;

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

		if ((colorMask & targetMask) != 0) {
			return false;
		}

		if (type == PieceType.PAWN) {
			return generatePseudoLegalMoves(sourceMask, targetMask).contains(move);
		}

		return (attackMask(move.getSource()) & targetMask) != 0;
	}

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

	public void push(Move move) {
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

		if (move == null) {
			this.turn = this.turn.other();
			return;
		}

		// zero the half move clock
		if (this.isZeroingMove(move)) {
			this.halfMoveClock = 0;
		}

		long sourceMask = SQUARES[move.getSource().ordinal()];
		long targetMask = SQUARES[move.getTarget().ordinal()];
		long colorMask = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;

		boolean promoted = (this.promoted & sourceMask) != 0;
		PieceType type = removePieceType(move.getSource());

		if (type == null) {
			throw new InvalidMoveException("No piece at source square");
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
			} else if (move.getTarget() == epSquare &&
					(Math.abs(diff) == 7 || Math.abs(diff) == 9) &&
					capturedPieceType == null) {
				captureSquare = Square.fromIndex(epSquare.ordinal() - 8 * turn.forwardDirection());
				capturedPieceType = removePieceType(captureSquare);

			}
		}

		if (move.getPromotion() != null) {
			promoted = true;
			type = move.getPromotion();
		}

		boolean castling = type == PieceType.KING && (move.getTarget().getFile() == 'g' || move.getTarget().getFile() == 'c');
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

	public Move pop() {
		Move move = this.moveStack.removeLast();
		this.stateStack.pop().restore(this);

		return move;
	}

	public Move peek() {
		return this.moveStack.peekLast();
	}

	private boolean isZeroingMove(Move move) {
		long moveMask = SQUARES[move.getSource().ordinal()] ^ SQUARES[move.getTarget().ordinal()];
		long otherColorMask = this.turn.equals(Color.WHITE) ? this.blackPieces : this.whitePieces;

		return (moveMask & this.pawns) != 0 || (moveMask & otherColorMask) != 0;
	}

	private BoardState getBoardState() {
		return new BoardState(this);
	}

	public Move pushSan(String san) {
		Move move = this.parseSan(san);
		this.push(move);
		return move;
	}

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
			throw new InvalidMoveException("Invalid san: " + san + " in " + getFen());
		}

		// filter target square
		Square targetSquare = Square.parseSquare(matcher.group(4));
		long colorMask = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;
		long targetMask = SQUARES[targetSquare.ordinal()] & ~colorMask;

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
			sourceMask &= FILE_MASKS[file.charAt(0) - 'a'];
		}

		String rank = matcher.group(3);
		if (rank != null) {
			sourceMask &= RANK_MASKS[rank.charAt(0) - '1'];
		}

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

	public EnumSet<Status> status() {
		EnumSet<Status> errors = EnumSet.noneOf(Status.class);

		if (this.occupied == 0) {
			errors.add(Status.EMPTY);
		}

		if ((this.whitePieces & this.kings) == 0) {
			errors.add(Status.NO_WHITE_KING);
		}

		if ((this.blackPieces & this.kings) == 0) {
			errors.add(Status.NO_BLACK_KING);
		}

		if (Long.bitCount(this.occupied & this.kings) > 2) {
			errors.add(Status.TOO_MANY_KINGS);
		}

		if (Long.bitCount(this.whitePieces) > 16) {
			errors.add(Status.TOO_MANY_WHITE_PIECES);
		}

		if (Long.bitCount(this.blackPieces) > 16) {
			errors.add(Status.TOO_MANY_BLACK_PIECES);
		}

		if (Long.bitCount(this.whitePieces & this.pawns) > 8) {
			errors.add(Status.TOO_MANY_WHITE_PAWNS);
		}

		if (Long.bitCount(this.blackPieces & this.pawns) > 8) {
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
		long ourKings = this.kings & (this.turn == Color.WHITE ? this.whitePieces : this.blackPieces);

		if (checkers != 0) {
			if (Long.bitCount(checkers) > 2) {
				errors.add(Status.TOO_MANY_CHECKERS);
			}

			if (validEpSquare != null) {
				int pushedTo = validEpSquare.ordinal() ^ 8;
				int pushedFrom = validEpSquare.ordinal() ^ 24;
				long occupiedBefore = (this.occupied & ~SQUARES[pushedTo]) | SQUARES[pushedFrom];
				if (Long.bitCount(checkers) > 1 ||
						(BitboardUtils.msb(checkers) != pushedTo && attackedForKing(ourKings, occupiedBefore))) {
					errors.add(Status.IMPOSSIBLE_CHECK);
				}
			} else {
				if (Long.bitCount(checkers) > 2 || (Long.bitCount(checkers) == 2 &&
						(BitboardUtils.ray(BitboardUtils.lsb(checkers), BitboardUtils.msb(checkers)) & ourKings) != 0)) {
					errors.add(Status.IMPOSSIBLE_CHECK);
				}
			}
		}

		if (errors.isEmpty()) {
			errors.add(Status.VALID);
		}

		return errors;
	}

	private long checkers_mask() {
		Square kingSquare = getKingSquare(this.turn);
		return kingSquare == null ? 0 : attackersMask(this.turn.other(), kingSquare);
	}

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

		long otherColorMask = this.turn == Color.WHITE ? this.blackPieces : this.whitePieces;
		if ((this.pawns & pawnMask & otherColorMask) == 0) {
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

	private boolean wasIntoCheck() {
		Square kingSquare = this.getKingSquare(turn.other());
		return kingSquare != null && isAttackedBy(turn, kingSquare);
	}

	public boolean isValid() {
		return status().contains(Status.VALID);
	}

	public Move findMove(Square sourceSquare, Square targetSquare) {
		return findMove(sourceSquare, targetSquare, null);
	}

	public Move findMove(Square sourceSquare, Square targetSquare, PieceType promotion) {
		if (promotion == null &&
				(this.pawns & SQUARES[sourceSquare.ordinal()]) != 0 &&
				(SQUARES[targetSquare.ordinal()] & BACKRANK) != 0) {
			promotion = PieceType.QUEEN;
		}

		Move move = new Move(sourceSquare, targetSquare, promotion);

		if (!this.isLegal(move)) {
			throw new IllegalMoveException("Illegal move: " + move + " in " + getFen());
		}

		return move;
	}


	public boolean hasInsufficientMaterial(Color color) {
		long colorMask = color == Color.WHITE ? this.whitePieces : this.blackPieces;

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

	public boolean isInsufficientMaterial() {
		return hasInsufficientMaterial(Color.WHITE) && hasInsufficientMaterial(Color.BLACK);
	}

	public boolean isCheck() {
		return checkers_mask() != 0;
	}

	public boolean isCheckmate() {
		return isCheck() && generateLegalMoves().isEmpty();
	}

	public boolean isStalemate() {
		return !isCheck() && generateLegalMoves().isEmpty();
	}

	public boolean isGameOver() {
		return isGameOver(false);
	}

	public boolean isGameOver(boolean claimDraw) {
		return outcome(claimDraw) != null;
	}

	public String result() {
		return result(false);
	}

	public String result(boolean claimDraw) {
		Outcome outcome = outcome(claimDraw);
		return outcome != null ? outcome.result() : "*";
	}

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


	private boolean isSeventyFiveMoves() {
		return this.isHalfmoves(150);
	}

	public boolean isFiftyMoves() {
		return this.isHalfmoves(100);
	}

	private boolean isHalfmoves(int n) {
		return this.halfMoveClock >= n && !generateLegalMoves().isEmpty();
	}

	public String san(Move move) {
		return this.algebraicNotation(move, false);
	}

	public String lan(Move move) {
		return this.algebraicNotation(move, true);
	}

	private String algebraicNotation(Move move, boolean longNotation) {
		String san = algebraicNotationPush(move, longNotation);
		this.pop();
		return san;
	}

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

	public String sanAndPush(Move move) {
		return algebraicNotationPush(move, false);
	}

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
				boolean row = false, column = false;

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

	public boolean isCapture(Move move) {
		long targetMask = SQUARES[move.getTarget().ordinal()];
		long otherColorMask = this.turn == Color.WHITE ? this.blackPieces : this.whitePieces;

		return (targetMask & otherColorMask) != 0 || this.isEnPassant(move);
	}

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

	public Move parseUCI(String uci) {
		Move move = Move.fromUCI(uci);

		if (!this.isLegal(move)) {
			throw new IllegalMoveException("Illegal move: " + move + " in " + getFen());
		}
		return move;
	}

	public Move pushUCI(String uci) {
		Move move = parseUCI(uci);
		this.push(move);
		return move;
	}

	public Color getTurn() {
		return turn;
	}

	public Square getEpSquare() {
		return epSquare;
	}

	public boolean canClaimDraw() {
		return canClaimFiftyMoveRule() || canClaimThreefoldRepetition();
	}

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

	public boolean isIrreversible(Move move) {
		return isZeroingMove(move) || hasLegalEnPassant() || reducesCastlingRights(move);
	}

	private boolean reducesCastlingRights(Move move) {
		long castlingRights = this.cleanCastlingRights();
		long touched = SQUARES[move.getSource().ordinal()] ^ SQUARES[move.getTarget().ordinal()];

		return (touched & castlingRights) != 0 ||
				((castlingRights & RANK_1) != 0 && (touched & this.kings & this.whitePieces & ~this.promoted) != 0) ||
				((castlingRights & RANK_8) != 0 && (touched & this.kings & this.blackPieces & ~this.promoted) != 0);

	}

	public boolean isFivefoldRepetition() {
		return isRepetition(5);
	}

	public boolean isRepetition() {
		return isRepetition(3);
	}

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
		return fullMoveNumber == board.fullMoveNumber &&
				halfMoveClock == board.halfMoveClock &&
				hashCode() == board.hashCode();
	}

	@Override
	public int hashCode() {
		if (hasLegalEnPassant()) {
			return Objects.hash(super.hashCode(), turn, cleanCastlingRights(), epSquare);
		}
		return Objects.hash(super.hashCode(), turn, cleanCastlingRights());
	}

	public Board mirror() {
		Board board = this.copy();
		board.applyMirror();
		return board;
	}

	@Override
	public void applyMirror() {
		super.applyMirror();
		this.turn = this.turn.other();
	}

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
