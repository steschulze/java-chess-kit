package de.stefanet.javachesskit;

import static de.stefanet.javachesskit.Bitboard.DIAGONAL_ATTACKS;
import static de.stefanet.javachesskit.Bitboard.EMPTY;
import static de.stefanet.javachesskit.Bitboard.FILE_ATTACKS;
import static de.stefanet.javachesskit.Bitboard.RANK_ATTACKS;
import static de.stefanet.javachesskit.Bitboard.SQUARES;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for generating piece attacks.
 */
public final class Attacks {
    private Attacks() {
    }

    /**
     * Generates a bitboard representing attacks for a sliding piece on the given square.
     *
     * <p>Sliding pieces are pieces that can move any number of squares along a rank, file, or diagonal.
     * Examples of sliding pieces are the rook, bishop, and queen.
     *
     * @param square   the square to generate the attacks for
     * @param occupied the bitboard representing the occupied squares.
     *                 If <code>occupied</code> is set to {@link Bitboard#ALL}, only step attacks are considered.
     * @param deltas   the deltas to use for generating the attacks.
     *                 For example, the deltas for a rook are {8, -8, 1, -1}.
     * @return the generated bitboard representing the attacks
     */
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

    /**
     * Generates a bitboard representing attacks for a non-sliding piece on the given square.
     *
     * <p>Non-sliding pieces are pieces that can move only one square at a time.
     * Examples of non-sliding pieces are the knight, king and pawn.
     *
     * @param square the square to generate the attacks for
     * @param deltas the deltas to use for generating the attacks.
     *               For example, the deltas for a knight are {17, 15, 10, 6, -17, -15, -10, -6}.
     * @return the generated bitboard representing the attacks
     */
    private static long stepAttacks(Square square, int[] deltas) {
        return slidingAttacks(square, Bitboard.ALL, deltas);
    }

    /**
     * Generates a bitboard containing the edges of the board for a given square.
     *
     * <p>The edges of the board are the squares on the first and last ranks and files.
     * For example, the edges for the e4 square are squares on the a and h files and the 1 and 8 ranks.
     *
     * @param square the square to generate the edges for
     * @return the generated bitboard containing the edges
     */
    private static long edges(Square square) {
        int rank = square.getRankIndex();
        int file = square.getFileIndex();
        return (
                ((Bitboard.Ranks.RANK_1 | Bitboard.Ranks.RANK_8) & ~Bitboard.RANKS[rank])
                | ((Bitboard.Files.FILE_A | Bitboard.Files.FILE_H) & ~Bitboard.FILES[file]));
    }

    /**
     * Generates all subsets of a given mask using the carry-rippler method.
     *
     * @param mask the mask to generate the subsets for
     * @return an array containing all subsets of the mask
     */
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

    /**
     * Generates an attack table for the given deltas.
     *
     * @param deltas the deltas to use for generating the attack table
     * @return the generated attack table
     */

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

    /**
     * Generates a rays attack table.
     *
     * <p>The rays attack table is a 64x64 array of bitboards where each row represents a square
     * and each column represents a square. The value at a given row and column is a bitboard
     * representing the squares that are attacked by a rook or bishop on the square in the row
     * attacking the square in the column.
     *
     * @return the generated rays attack table
     */
    public static long[][] rays() {
        long[][] rays = new long[64][64];

        for (int a = 0; a < SQUARES.length; a++) {
            long[] raysRow = new long[64];

            for (int b = 0; b < SQUARES.length; b++) {
                if ((DIAGONAL_ATTACKS.get(a).get(0L) & SQUARES[b]) != EMPTY) {
                    raysRow[b] = (DIAGONAL_ATTACKS.get(a).get(0L) & DIAGONAL_ATTACKS.get(b).get(0L))
                                 | SQUARES[a] | SQUARES[b];
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

    /**
     * Generates an attack table representing the attacks of a knight for each square.
     *
     * @return the generated knight attacks table
     */
    public static long[] generateKnightAttacks() {
        long[] knightAttacks = new long[64];

        for (Square square : Square.values()) {
            knightAttacks[square.ordinal()] = stepAttacks(square, new int[]{17, 15, 10, 6, -17, -15, -10, -6});
        }

        return knightAttacks;
    }

    /**
     * Generates an attack table representing the attacks of a king for each square.
     *
     * @return the generated king attacks table
     */
    public static long[] generateKingAttacks() {
        long[] kingAttacks = new long[64];

        for (Square square : Square.values()) {
            kingAttacks[square.ordinal()] = stepAttacks(square, new int[]{9, 8, 7, 1, -9, -8, -7, -1});
        }

        return kingAttacks;
    }

    /**
     * Generates an attack table representing the attacks of a pawn for each square.
     *
     * @return the generated pawn attacks table
     */
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
