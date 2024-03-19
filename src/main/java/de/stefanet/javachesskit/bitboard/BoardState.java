package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.Color;

public class BoardState {
	private long pawns;
	private long knights;
	private long bishops;
	private long rooks;
	private long queens;
	private long kings;

	private long promoted;
	private long whitePieces;
	private long blackPieces;
	private long occupied;

	private Color turn;
	private long castlingRights;

	private Long epSquare;

	private int fullMoveNumber;
	private int halfMoveClock;

	public BoardState(Board board) {
		this.pawns = board.pawns;
		this.knights = board.knights;
		this.bishops = board.bishops;
		this.rooks = board.rooks;
		this.queens = board.queens;
		this.kings = board.kings;


		this.occupied = board.occupied;
		this.whitePieces = board.whitePieces;
		this.blackPieces = board.blackPieces;

		this.promoted = board.promoted;
		this.turn = board.turn;
		this.castlingRights = board.castlingRights;
		this.epSquare = board.epSquare;
		this.halfMoveClock = board.halfMoveClock;
		this.fullMoveNumber = board.fullMoveNumber;
	}

	public void restore(Board board) {
		board.pawns = this.pawns;
		board.knights = this.knights;
		board.bishops = this.bishops;
		board.rooks = this.rooks;
		board.queens = this.queens;
		board.kings = this.kings;

		board.occupied = this.occupied;
		board.whitePieces = this.whitePieces;
		board.blackPieces = this.blackPieces;

		board.promoted = this.promoted;
		board.turn = this.turn;
		board.castlingRights = this.castlingRights;
		board.epSquare = this.epSquare;
		board.halfMoveClock = this.halfMoveClock;
		board.fullMoveNumber = this.fullMoveNumber;
	}
}
