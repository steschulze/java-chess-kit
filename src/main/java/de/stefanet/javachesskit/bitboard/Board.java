package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static de.stefanet.javachesskit.bitboard.Bitboard.Files.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.PAWN_ATTACKS;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_8;
import static de.stefanet.javachesskit.bitboard.Bitboard.SQUARES;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.*;

public class Board extends BaseBoard {
	protected final static String STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
	protected Color turn;
	protected long castlingRights;

	protected Square epSquare;

	protected int fullMoveNumber;
	protected int halfMoveClock;

	protected long promoted;
	private List<Move> moveList;
	private List<BoardState> stateList;

	public Board() {
		this(STARTING_FEN);
	}

	public Board(String fen) {
		super(null);

		this.epSquare = null;
		this.moveList = new ArrayList<>();
		this.stateList = new ArrayList<>();

		if (fen == null) {
			clear();
		} else if (fen.equals(STARTING_FEN)) {
			reset();
		} else {
			setFen(fen);
		}
	}

	private void setFen(String fen) {
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

	private void setCastlingFen(String castlingFen) {
		if (castlingFen == null || castlingFen.equals("-")) {
			this.castlingRights = 0;
			return;
		}
		//TODO check castlingFen with regex
		this.castlingRights = 0;

		for (char flag : castlingFen.toCharArray()) {
			Color color = Character.isUpperCase(flag) ? Color.WHITE : Color.BLACK;
			flag = Character.toLowerCase(flag);
			long backrank = color == Color.WHITE ? RANK_1 : RANK_8;
			long colorMask = color == Color.WHITE ? this.whitePieces : this.blackPieces;
			long rooks = colorMask & this.rooks & backrank;

			Square kingSquare = getKingSquare(color);

			if (flag == 'q') {
				if (kingSquare != null) { //TODO
					this.castlingRights |= rooks & -rooks;
				} else {
					this.castlingRights |= FILE_A & backrank;
				}
			} else if (flag == 'k') {
				int rook = 0; //TODO
				if (kingSquare != null) { //TODO
					this.castlingRights |= SQUARES[rook];
				} else {
					this.castlingRights |= FILE_H & backrank;
				}
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

	private void clear() {
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

	private void clearStack() {
		this.moveList.clear();
		this.stateList.clear();
	}

	public LegalMoveGenerator legalMoves() {
		return new LegalMoveGenerator(this);
	}

	public PseudoLegalMoveGenerator pseudoLegalMoves() {
		return new PseudoLegalMoveGenerator(this);
	}

	public List<Move> generateLegalMoves() {

		return Collections.emptyList();
	}

	public List<Move> generatePseudoLegalMoves() {
		return generatePseudoLegalMoves(Bitboard.ALL, Bitboard.ALL);
	}

	public List<Move> generatePseudoLegalMoves(long sourceMask, long targetMask) {
		List<Move> moveList = new ArrayList<>();
		long ownPieces = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;

		// non pawn moves
		long nonPawns = ownPieces & ~this.pawns & sourceMask;
		for (Iterator<Square> it = BitboardUtils.scanReversed(nonPawns); it.hasNext(); ) {
			Square source = it.next();
			long moves = attackMask(source) & ~ownPieces & targetMask;
			for (Iterator<Square> iter = BitboardUtils.scanReversed(moves); iter.hasNext(); ) {
				Square target = iter.next();
				moveList.add(new Move(source, target));
			}
		}

		// castling moves
		if ((targetMask & this.kings) != 0) {
			moveList.addAll(generateCastlingMoves(sourceMask, targetMask));
		}

		long pawns = this.pawns & ownPieces & sourceMask;
		if (pawns == 0) return moveList;

		// pawn captures
		long captures = pawns;
		for (Iterator<Square> it = BitboardUtils.scanReversed(captures); it.hasNext(); ) {
			Square source = it.next();

			long targets = Bitboard.PAWN_ATTACKS[turn.ordinal()][source.ordinal()]
					& targetMask & (turn.equals(Color.WHITE) ? this.blackPieces : this.whitePieces);

			for (Iterator<Square> iter = BitboardUtils.scanReversed(targets); iter.hasNext(); ) {
				Square target = iter.next();
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
		for (Iterator<Square> it = BitboardUtils.scanReversed(singlePawnMoves); it.hasNext(); ) {
			Square target = it.next();
			Square source = Square.fromIndex(target.ordinal() + turn.forwardDirection() * 8);

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
		for (Iterator<Square> it = BitboardUtils.scanReversed(doublePawnMoves); it.hasNext(); ) {
			Square target = it.next();
			Square source = Square.fromIndex(target.ordinal() + turn.forwardDirection() * 16);
			moveList.add(new Move(source, target));
		}

		if (epSquare != null) {
			moveList.addAll(generatePseudoLegalEnPassant(sourceMask, targetMask));
		}
		return moveList;
	}

	private List<Move> generatePseudoLegalEnPassant(long sourceMask, long targetMask) {
		List<Move> moves = new ArrayList<>();

		if (epSquare == null || (SQUARES[epSquare.ordinal()] & targetMask) == 0) return moves;

		// epSquare is occupied
		if ((SQUARES[epSquare.ordinal()] & this.occupied) != 0) return moves;

		long colorMask = this.turn.equals(Color.WHITE) ? this.whitePieces : this.blackPieces;
		long rankMask = this.turn.equals(Color.WHITE) ? Bitboard.RANKS[4] : Bitboard.RANKS[3];
		long attackMask = PAWN_ATTACKS[turn.other().ordinal()][epSquare.ordinal()];

		long capturers = this.pawns & colorMask & sourceMask & attackMask & rankMask;

		for (Iterator<Square> it = BitboardUtils.scanReversed(capturers); it.hasNext(); ) {
			Square source = it.next();
			moves.add(new Move(source, epSquare));
		}

		return moves;
	}

	private List<Move> generateCastlingMoves(long sourceMask, long targetMask) {
		List<Move> moves = new ArrayList<>();

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

		for (Iterator<Square> it = BitboardUtils.scanReversed(castling); it.hasNext(); ) {
			Square candidate = it.next();

			long rook = SQUARES[candidate.ordinal()];
			boolean queenSide = rook < king;

			long kingTarget = queenSide ? c : g;
			long rookTarget = queenSide ? d : f;

			long kingPath = BitboardUtils.between(
					Square.fromIndex(BitboardUtils.msb(king)),
					Square.fromIndex(BitboardUtils.msb(kingTarget)));

			long rookPath = BitboardUtils.between(
					candidate,
					Square.fromIndex(BitboardUtils.msb(rookTarget)));

			if (!(((this.occupied ^ king ^ rook) &
					(kingPath | rookPath | kingTarget | rookTarget)) != 0 ||
					attackedForKing(kingPath | king, this.occupied ^ king) ||
					attackedForKing(kingTarget, this.occupied ^ king ^ rook ^ rookTarget)
			)) {
				//TODO moves.add(...)
			}
		}

		return moves;
	}

	private boolean attackedForKing(long l, long l1) {
		//TODO
		return false;
	}

	private long cleanCastlingRights() {
		//TODO
		return 0;
	}

	public boolean isLegal(Move move) {

		return false;
	}

	public boolean isPseudoLegal(Move move) {

		return false;
	}

}
