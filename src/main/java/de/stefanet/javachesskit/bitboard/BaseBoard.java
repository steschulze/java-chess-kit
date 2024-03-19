package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.Color;
import de.stefanet.javachesskit.InvalidMoveException;
import de.stefanet.javachesskit.Piece;
import de.stefanet.javachesskit.PieceType;

import java.util.HashMap;
import java.util.Map;

import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.SQUARES;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.*;

public class BaseBoard {
	private static final String STARTING_BOARD_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
	protected long pawns;
	protected long knights;
	protected long bishops;
	protected long rooks;
	protected long queens;
	protected long kings;

	protected long promoted;
	protected long whitePieces;
	protected long blackPieces;
	protected long occupied;

	public BaseBoard() {
		this(STARTING_BOARD_FEN);
	}

	public BaseBoard(String fen) {
		if (fen == null) {
			this.clearBitboards();
		} else if (fen.equals(STARTING_BOARD_FEN)) {
			resetBoard();
		} else {
			setBoardFen(fen);
		}
	}

	protected void clearBoard() {
		clearBitboards();
	}

	protected void clearBitboards() {
		this.pawns = 0;
		this.knights = 0;
		this.bishops = 0;
		this.rooks = 0;
		this.queens = 0;
		this.kings = 0;

		this.promoted = 0;

		this.whitePieces = 0;
		this.blackPieces = 0;
		this.occupied = 0;
	}

	protected void resetBoard() {
		this.pawns = RANK_2 | RANK_7;
		this.knights = B1 | G1 | B8 | G8;
		this.bishops = C1 | F1 | C8 | F8;
		this.rooks = A1 | H1 | A8 | H8;
		this.queens = D1 | D8;
		this.kings = E1 | E8;

		this.promoted = 0;

		this.whitePieces = RANK_1 | RANK_2;
		this.blackPieces = RANK_7 | RANK_8;
		this.occupied = RANK_1 | RANK_2 | RANK_7 | RANK_8;
	}

	public long pieceMask(PieceType type, Color color) {
		long pieceMask = 0L;

		switch (type) {
			case PAWN:
				pieceMask = color == Color.WHITE ? this.pawns & this.whitePieces : this.pawns & this.blackPieces;
				break;
			case KNIGHT:
				pieceMask = color == Color.WHITE ? this.knights & this.whitePieces : this.knights & this.blackPieces;
				break;
			case BISHOP:
				pieceMask = color == Color.WHITE ? this.bishops & this.whitePieces : this.bishops & this.blackPieces;
				break;
			case ROOK:
				pieceMask = color == Color.WHITE ? this.rooks & this.whitePieces : this.rooks & this.blackPieces;
				break;
			case QUEEN:
				pieceMask = color == Color.WHITE ? this.queens & this.whitePieces : this.queens & this.blackPieces;
				break;
			case KING:
				pieceMask = color == Color.WHITE ? this.kings & this.whitePieces : this.kings & this.blackPieces;
				break;
		}

		return pieceMask;
	}

	public BaseBoard copy() {
		BaseBoard board = new BaseBoard();
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

		return board;
	}


	public Piece get(long square) {
		PieceType type = pieceTypeAt(square);

		if (type != null) {
			Color color = (this.whitePieces & square) == 0 ? Color.BLACK : Color.WHITE;
			return Piece.fromTypeAndColor(type, color);
		}
		return null;
	}

	public Piece pieceAt(long square) {
		PieceType type = pieceTypeAt(square);
		if (type != null) {
			Color color = Color.fromBoolean((this.whitePieces & square) != 0);
			return Piece.fromTypeAndColor(type, color);
		}
		return null;
	}

	public PieceType pieceTypeAt(long square) {
		if ((this.occupied & square) == 0) {
			return null;
		} else if ((this.pawns & square) != 0) {
			return PieceType.PAWN;
		} else if ((this.knights & square) != 0) {
			return PieceType.KNIGHT;
		} else if ((this.bishops & square) != 0) {
			return PieceType.BISHOP;
		} else if ((this.rooks & square) != 0) {
			return PieceType.ROOK;
		} else if ((this.queens & square) != 0) {
			return PieceType.QUEEN;
		} else {
			return PieceType.KING;
		}
	}

	public void set(long square, Piece piece) {
		if (piece == null) {
			removePiece(square);
		} else {
			setPiece(square, piece.getType(), piece.getColor());
		}
	}

	private void setPiece(long square, PieceType type, Color color) {
		removePiece(square);

		if (type == PieceType.PAWN) {
			this.pawns |= square;
		} else if (type == PieceType.KNIGHT) {
			this.knights |= square;
		} else if (type == PieceType.BISHOP) {
			this.bishops |= square;
		} else if (type == PieceType.ROOK) {
			this.rooks |= square;
		} else if (type == PieceType.QUEEN) {
			this.queens |= square;
		} else if (type == PieceType.KING) {
			this.kings |= square;
		} else {
			return;
		}

		this.occupied ^= square;
		if (color == Color.WHITE) {
			this.whitePieces ^= square;
		} else {
			this.blackPieces ^= square;
		}
	}

	private PieceType removePiece(long square) {
		PieceType type = pieceTypeAt(square);

		if (type == PieceType.PAWN) {
			this.pawns ^= square;
		} else if (type == PieceType.KNIGHT) {
			this.knights ^= square;
		} else if (type == PieceType.BISHOP) {
			this.bishops ^= square;
		} else if (type == PieceType.ROOK) {
			this.rooks ^= square;
		} else if (type == PieceType.QUEEN) {
			this.queens ^= square;
		} else if (type == PieceType.KING) {
			this.kings ^= square;
		} else {
			return null;
		}

		this.occupied ^= square;
		this.whitePieces &= ~square;
		this.blackPieces &= ~square;
		this.promoted &= ~square;

		return type;
	}

	public Map<PieceType, Integer> getPieceCounts(String color) {
		if (!color.equals("w") && !color.equals("b") && !color.equals("wb") && !color.equals("bw"))
			throw new IllegalArgumentException("Invalid color string: " + color);

		long colorMask;
		if (color.equals("w")) {
			colorMask = this.whitePieces;
		} else if (color.equals("b")) {
			colorMask = this.blackPieces;
		} else {
			colorMask = this.whitePieces | blackPieces;
		}

		Map<PieceType, Integer> pieceCounts = new HashMap<>();
		pieceCounts.put(PieceType.PAWN, Long.bitCount(this.pawns & colorMask));
		pieceCounts.put(PieceType.KNIGHT, Long.bitCount(this.knights & colorMask));
		pieceCounts.put(PieceType.BISHOP, Long.bitCount(this.bishops & colorMask));
		pieceCounts.put(PieceType.ROOK, Long.bitCount(this.rooks & colorMask));
		pieceCounts.put(PieceType.KING, Long.bitCount(this.kings & colorMask));
		pieceCounts.put(PieceType.QUEEN, Long.bitCount(this.queens & colorMask));

		return pieceCounts;
	}

	public long getKingSquare(Color color) {
		long colorMask = (color == Color.WHITE) ? this.whitePieces : this.blackPieces;
		long kingMask = this.kings & colorMask;

		for (int i = 0; i < 64; i++) {
			if (((kingMask >> i) & 1) == 1) {
				return SQUARES[i];
			}
		}

		return 0;
	}


	public String getBoardFen() {
		StringBuilder sb = new StringBuilder();
		int empty = 0;

		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
				int index = x + 8 * y;
				Piece piece = pieceAt(SQUARES[index]);

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

	protected void setBoardFen(String fen) {
		fen = fen.trim();
		if (fen.contains(" ")) {
			throw new IllegalArgumentException("Invalid board fen: contains space");
		}

		String[] rows = fen.split("/");
		if (rows.length != 8) {
			throw new IllegalArgumentException("Expected 8 rows in board fen");
		}

		for (String row : rows) {
			int rowSum = 0;
			boolean previousWasNumber = false;

			for (char c : row.toCharArray()) {
				if (c >= '1' && c <= '8') {
					if (previousWasNumber) {
						throw new InvalidMoveException("Position part of the FEN is invalid Several numbers in a row");
					}

					rowSum += Character.getNumericValue(c);
					previousWasNumber = true;
				} else if ("pnbrkqPNBRKQ".indexOf(c) != -1) {
					rowSum++;
					previousWasNumber = false;
				} else {
					throw new InvalidMoveException("Position part of the FEN is invalid: Invalid character " + c);
				}
			}

			if (rowSum != 8) {
				throw new InvalidMoveException("Position part of the FEN is invalid: Invalid row length");
			}
		}

		clearBoard();
		int square_index = 56;
		for (char c : fen.toCharArray()) {
			if (c >= '1' && c <= '8') {
				square_index += Character.getNumericValue(c);
			} else if ("pnbrkqPNBRKQ".indexOf(c) != -1) {
				Piece piece = new Piece(c);
				setPiece(SQUARES[square_index], piece.getType(), piece.getColor());
				square_index++;
			} else {
				square_index -= 16;
			}
		}

	}
}
