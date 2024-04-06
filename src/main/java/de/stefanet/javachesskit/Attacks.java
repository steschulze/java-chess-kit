package de.stefanet.javachesskit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.stefanet.javachesskit.Bitboard.*;

public final class Attacks {
	private Attacks() {
	}
	
	private static long slidingAttacks(Square square, long occupied, int[] deltas) {
		long attacks = EMPTY;

		for (int delta : deltas) {
			int squareIndex = square.ordinal();

			while (true) {
				squareIndex += delta;
				if (squareIndex < 0 || squareIndex >= 64
						|| Square.distance(Square.fromIndex(squareIndex), Square.fromIndex(squareIndex - delta)) > 2) {
					break;
				}

				attacks |= SQUARES[squareIndex];

				if ((occupied & SQUARES[squareIndex]) != 0) {
					break;
				}
			}
		}

		return attacks;
	}

	private static long stepAttacks(Square square, int[] deltas) {
		return slidingAttacks(square, Bitboard.ALL, deltas);
	}

	private static long edges(Square square) {
		int rank = square.getRankIndex();
		int file = square.getFileIndex();
		return (((Bitboard.Ranks.RANK_1 | Bitboard.Ranks.RANK_8) & ~Bitboard.RANKS[rank]) |
				((Bitboard.Files.FILE_A | Bitboard.Files.FILE_H) & ~Bitboard.FILES[file]));
	}

	private static long[] carryRippler(long mask) {
		int size = (int) Math.pow(2, Long.bitCount(mask));

		long[] result = new long[size];
		int index = 0;

		long subset = 0;
		do {
			result[index++] = subset;
			subset = (subset - mask) & mask;
		} while (subset != 0 && index < size);

		return result;
	}

	public static AttackTable attackTable(int... deltas) {
		long[] maskTable = new long[64];
		List<Map<Long, Long>> attackTable = new ArrayList<>();

		for (Square square : Square.values()) {
			Map<Long, Long> squareAttacks = new HashMap<>();
			long mask = Attacks.slidingAttacks(square, 0, deltas) & ~edges(square);

			for (long subset : carryRippler(mask)) {
				squareAttacks.put(subset, Attacks.slidingAttacks(square, subset, deltas));
			}

			attackTable.add(squareAttacks);
			maskTable[square.ordinal()] = mask;
		}

		return new AttackTable(maskTable, attackTable);
	}

	public static long[][] rays() {
		long[][] rays = new long[64][64];

		for (int a = 0; a < SQUARES.length; a++) {
			long[] raysRow = new long[64];

			for (int b = 0; b < SQUARES.length; b++) {
				if ((DIAGONAL_ATTACKS.get(a).get(0L) & SQUARES[b]) != EMPTY) {
					raysRow[b] = (DIAGONAL_ATTACKS.get(a).get(0L) & DIAGONAL_ATTACKS.get(b).get(0L)) | SQUARES[a] | SQUARES[b];
				} else if ((RANK_ATTACKS.get(a).get(0L) & SQUARES[b]) != EMPTY) {
					raysRow[b] = RANK_ATTACKS.get(a).get(0L) | SQUARES[a];
				} else if ((FILE_ATTACKS.get(a).get(0L) & SQUARES[b]) != EMPTY) {
					raysRow[b] = FILE_ATTACKS.get(a).get(0L) | SQUARES[a];
				} else {
					raysRow[b] = EMPTY;
				}
			}

			rays[a] = raysRow;
		}

		return rays;
	}

	public static long[] generateKnightAttacks() {
		long[] knightAttacks = new long[64];

		for (Square square : Square.values()) {
			knightAttacks[square.ordinal()] = stepAttacks(square, new int[]{17, 15, 10, 6, -17, -15, -10, -6});
		}

		return knightAttacks;
	}

	public static long[] generateKingAttacks() {
		long[] kingAttacks = new long[64];

		for (Square square : Square.values()) {
			kingAttacks[square.ordinal()] = stepAttacks(square, new int[]{9, 8, 7, 1, -9, -8, -7, -1});
		}

		return kingAttacks;
	}

	public static long[][] generatePawnAttacks() {
		long[][] pawnAttacks = new long[2][64];

		int[][] deltas = {{7, 9}, {-7, -9}};

		for (int i = 0; i < 2; i++) {
			long[] attacks = new long[64];
			for (Square square : Square.values()) {
				attacks[square.ordinal()] = stepAttacks(square, deltas[i]);
			}
			pawnAttacks[i] = attacks;
		}

		return pawnAttacks;
	}
}
