package de.stefanet.javachesskit.bitboard;

import de.stefanet.javachesskit.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static de.stefanet.javachesskit.bitboard.Bitboard.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.*;
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


	public Piece get(Square square) {
		long mask = SQUARES[square.ordinal()];
		PieceType type = pieceTypeAt(square);

		if (type != null) {
			Color color = (this.whitePieces & mask) == 0 ? Color.BLACK : Color.WHITE;
			return Piece.fromTypeAndColor(type, color);
		}
		return null;
	}

	public Piece pieceAt(Square square) {
		long mask = SQUARES[square.ordinal()];
		PieceType type = pieceTypeAt(square);
		if (type != null) {
			Color color = Color.fromBoolean((this.whitePieces & mask) != 0);
			return Piece.fromTypeAndColor(type, color);
		}
		return null;
	}

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

	public void set(Square square, Piece piece) {
		if (piece == null) {
			removePiece(square);
		} else {
			setPiece(square, piece.getType(), piece.getColor());
		}
	}

	private void setPiece(Square square, PieceType type, Color color) {
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
		if (color == Color.WHITE) {
			this.whitePieces ^= mask;
		} else {
			this.blackPieces ^= mask;
		}
	}

	public Piece removePiece(Square square) {
		Color color = Color.fromBoolean((this.whitePieces & SQUARES[square.ordinal()]) != 0);
		PieceType pieceType = removePieceType(square);

		if (pieceType != null) {
			return Piece.fromTypeAndColor(pieceType, color);
		}

		return null;
	}

	private PieceType removePieceType(Square square) {
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
		this.whitePieces &= ~mask;
		this.blackPieces &= ~mask;
		this.promoted &= ~mask;

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

	public Color colorAt(Square square) {
		long mask = SQUARES[square.ordinal()];
		if ((this.whitePieces & mask) != 0) {
			return Color.WHITE;
		} else if ((this.blackPieces & mask) != 0) {
			return Color.BLACK;
		} else {
			return null;
		}
	}

	public Square getKingSquare(Color color) {
		long colorMask = (color == Color.WHITE) ? this.whitePieces : this.blackPieces;
		long kingMask = this.kings & colorMask & ~this.promoted;
		if (kingMask != 0) {
			return Square.fromIndex(BitboardUtils.msb(kingMask));
		}


		return null;
	}

	public SquareSet pieces(PieceType type, Color color) {
		return new SquareSet(pieceMask(type, color));
	}

	public long attackMask(Square square) {
		long mask = SQUARES[square.ordinal()];

		if ((mask & this.pawns) != 0) {
			int color = (mask & this.whitePieces) != 0 ? 0 : 1;
			return PAWN_ATTACKS[color][square.ordinal()];
		} else if ((mask & this.knights) != 0) {
			return KNIGHT_ATTACKS[square.ordinal()];
		} else if ((mask & this.knights) != 0) {
			return KING_ATTACKS[square.ordinal()];
		} else {
			long attacks = 0;
			if ((mask & this.bishops) != 0 || (mask & this.queens) != 0) {
				Map<Long, Long> attackMap = DIAGONAL_ATTACKS.get(square.ordinal());
				attacks = attackMap.get(DIAGONAL_MASKS[square.ordinal()] & this.occupied);
			}
			if ((mask & this.rooks) != 0 || (mask & this.queens) != 0) {
				Map<Long, Long> rankAttackMap = RANK_ATTACKS.get(square.ordinal());
				Map<Long, Long> fileAttackMap = FILE_ATTACKS.get(square.ordinal());

				attacks |= rankAttackMap.get(RANK_MASKS[square.ordinal()] & this.occupied)
						| fileAttackMap.get(FILE_MASKS[square.ordinal()] & this.occupied);
			}
			return attacks;
		}
	}

	public SquareSet attacks(Square square) {
		return new SquareSet(attackMask(square));
	}

	protected long attackersMask(Color color, Square square, long occupied) {
		long rankPieces = RANK_MASKS[square.ordinal()] & occupied;
		long filePieces = FILE_MASKS[square.ordinal()] & occupied;
		long diagPieces = DIAGONAL_MASKS[square.ordinal()] & occupied;

		long queensAndRooks = this.queens | this.rooks;
		long queensAndBishops = this.queens | this.bishops;

		long attackers = (KING_ATTACKS[square.ordinal()] & this.kings) |
				KNIGHT_ATTACKS[square.ordinal()] & this.knights |
				RANK_ATTACKS.get(square.ordinal()).get(rankPieces) & queensAndRooks |
				FILE_ATTACKS.get(square.ordinal()).get(rankPieces) & queensAndRooks |
				DIAGONAL_ATTACKS.get(square.ordinal()).get(diagPieces) & queensAndBishops |
				PAWN_ATTACKS[color.other().ordinal()][square.ordinal()] & this.pawns;
		return attackers & (color == Color.WHITE ? this.whitePieces : this.blackPieces);
	}

	public long attackersMask(Color color, Square square) {
		return attackersMask(color, square, this.occupied);
	}

	public boolean isAttackedBy(Color color, Square square) {
		return attackersMask(color, square) != 0;
	}

	public SquareSet attackers(Color color, Square square) {
		return new SquareSet(attackersMask(color, square));
	}

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

	public Map<Square, Piece> getPieceMap(long mask) {
		Map<Square, Piece> result = new HashMap<>();

		for (int index : BitboardUtils.scanReversed(mask)) {
			Square square = Square.fromIndex(index);
			result.put(square, pieceAt(square));
		}

		return result;
	}

	public Map<Square, Piece> getPieceMap() {
		return getPieceMap(Bitboard.ALL);
	}

	public void setPieceMap(Map<Square, Piece> pieceMap) {
		clearBitboards();
		for (Map.Entry<Square, Piece> entry : pieceMap.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BaseBoard board = (BaseBoard) o;
		return pawns == board.pawns &&
				knights == board.knights &&
				bishops == board.bishops &&
				rooks == board.rooks &&
				queens == board.queens &&
				kings == board.kings &&
				promoted == board.promoted &&
				whitePieces == board.whitePieces &&
				blackPieces == board.blackPieces &&
				occupied == board.occupied;
	}

	@Override
	public int hashCode() {
		return Objects.hash(pawns, knights, bishops, rooks, queens, kings, promoted, whitePieces, blackPieces, occupied);
	}
}
