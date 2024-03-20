package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.Color;
import de.stefanet.javachesskit.InvalidMoveException;
import de.stefanet.javachesskit.board0x88.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_A;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_H;
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
}
