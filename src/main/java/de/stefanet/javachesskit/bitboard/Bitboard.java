package de.stefanet.javachesskit.bitboard;

import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_A;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_C;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_E;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_F;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_G;
import static de.stefanet.javachesskit.bitboard.Bitboard.Files.FILE_H;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_1;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_2;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_3;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_4;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_5;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_6;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_7;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.RANK_8;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.*;

import java.util.List;
import java.util.Map;

/**
 * Class containing bitboard constants for squares, rank and files.
 */
public final class Bitboard {

    private Bitboard() {
    }

    public static final long[] FILES = {FILE_A, Files.FILE_B, FILE_C, Files.FILE_D, FILE_E, FILE_F, FILE_G, FILE_H};
    public static final long[] RANKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};

    public static final long[] SQUARES = {
            A1, B1, C1, D1, E1, F1, G1, H1,
            A2, B2, C2, D2, E2, F2, G2, H2,
            A3, B3, C3, D3, E3, F3, G3, H3,
            A4, B4, C4, D4, E4, F4, G4, H4,
            A5, B5, C5, D5, E5, F5, G5, H5,
            A6, B6, C6, D6, E6, F6, G6, H6,
            A7, B7, C7, D7, E7, F7, G7, H7,
            A8, B8, C8, D8, E8, F8, G8, H8
    };

    public static final long EMPTY = 0;
    public static final long ALL = 0xFFFFFFFFFFFFFFFFL;
    public static final long CORNERS = A1 | H1 | A8 | H8;
    public static final long CENTER = D4 | E4 | D5 | E5;

    public static final long LIGHT_SQUARES = 0x55AA55AA55AA55AAL;
    public static final long DARK_SQUARES = 0xAA55AA55AA55AA55L;

    public static final long BACKRANK = RANK_1 | RANK_8;

    public static final long[] KNIGHT_ATTACKS = Attacks.generateKnightAttacks();
    public static final long[] KING_ATTACKS = Attacks.generateKingAttacks();
    public static final long[][] PAWN_ATTACKS = Attacks.generatePawnAttacks();

    public static final long[] DIAGONAL_MASKS = Attacks.attackTable(-9, -7, 7, 9).getMaskTable();
    public static final List<Map<Long, Long>> DIAGONAL_ATTACKS = Attacks.attackTable(-9, -7, 7, 9).getAttackTable();
    public static final long[] FILE_MASKS = Attacks.attackTable(-8, 8).getMaskTable();
    public static final List<Map<Long, Long>> FILE_ATTACKS = Attacks.attackTable(-8, 8).getAttackTable();

    public static final long[] RANK_MASKS = Attacks.attackTable(-1, 1).getMaskTable();
    public static final List<Map<Long, Long>> RANK_ATTACKS = Attacks.attackTable(-1, 1).getAttackTable();
    public static final long[][] RAYS = Attacks.rays();

    /**
     * Class containing bitboard constants for files.
     */
    public static final class Files {
        private Files() {
        }

        public static final long FILE_A = 0x0101010101010101L;
        public static final long FILE_B = FILE_A << 1;
        public static final long FILE_C = FILE_B << 1;
        public static final long FILE_D = FILE_C << 1;
        public static final long FILE_E = FILE_D << 1;
        public static final long FILE_F = FILE_E << 1;
        public static final long FILE_G = FILE_F << 1;
        public static final long FILE_H = FILE_G << 1;
    }

    /**
     * Class containing bitboard constants for ranks.
     */
    public static final class Ranks {
        private Ranks() {
        }

        public static final long RANK_1 = 0xFFL;
        public static final long RANK_2 = RANK_1 << 8;
        public static final long RANK_3 = RANK_2 << 8;
        public static final long RANK_4 = RANK_3 << 8;
        public static final long RANK_5 = RANK_4 << 8;
        public static final long RANK_6 = RANK_5 << 8;
        public static final long RANK_7 = RANK_6 << 8;
        public static final long RANK_8 = RANK_7 << 8;
    }

    /**
     * Class containing bitboard constants for squares.
     */
    public static final class Squares {
        private Squares() {
        }

        public static final long A1 = 0x1L;
        public static final long B1 = 0x2L;
        public static final long C1 = 0x4L;
        public static final long D1 = 0x8L;
        public static final long E1 = 0x10L;
        public static final long F1 = 0x20L;
        public static final long G1 = 0x40L;
        public static final long H1 = 0x80L;

        public static final long A2 = 0x100L;
        public static final long B2 = 0x200L;
        public static final long C2 = 0x400L;
        public static final long D2 = 0x800L;
        public static final long E2 = 0x1000L;
        public static final long F2 = 0x2000L;
        public static final long G2 = 0x4000L;
        public static final long H2 = 0x8000L;

        public static final long A3 = 0x10000L;
        public static final long B3 = 0x20000L;
        public static final long C3 = 0x40000L;
        public static final long D3 = 0x80000L;
        public static final long E3 = 0x100000L;
        public static final long F3 = 0x200000L;
        public static final long G3 = 0x400000L;
        public static final long H3 = 0x800000L;

        public static final long A4 = 0x1000000L;
        public static final long B4 = 0x2000000L;
        public static final long C4 = 0x4000000L;
        public static final long D4 = 0x8000000L;
        public static final long E4 = 0x10000000L;
        public static final long F4 = 0x20000000L;
        public static final long G4 = 0x40000000L;
        public static final long H4 = 0x80000000L;

        public static final long A5 = 0x100000000L;
        public static final long B5 = 0x200000000L;
        public static final long C5 = 0x400000000L;
        public static final long D5 = 0x800000000L;
        public static final long E5 = 0x1000000000L;
        public static final long F5 = 0x2000000000L;
        public static final long G5 = 0x4000000000L;
        public static final long H5 = 0x8000000000L;

        public static final long A6 = 0x10000000000L;
        public static final long B6 = 0x20000000000L;
        public static final long C6 = 0x40000000000L;
        public static final long D6 = 0x80000000000L;
        public static final long E6 = 0x100000000000L;
        public static final long F6 = 0x200000000000L;
        public static final long G6 = 0x400000000000L;
        public static final long H6 = 0x800000000000L;

        public static final long A7 = 0x1000000000000L;
        public static final long B7 = 0x2000000000000L;
        public static final long C7 = 0x4000000000000L;
        public static final long D7 = 0x8000000000000L;
        public static final long E7 = 0x10000000000000L;
        public static final long F7 = 0x20000000000000L;
        public static final long G7 = 0x40000000000000L;
        public static final long H7 = 0x80000000000000L;

        public static final long A8 = 0x100000000000000L;
        public static final long B8 = 0x200000000000000L;
        public static final long C8 = 0x400000000000000L;
        public static final long D8 = 0x800000000000000L;
        public static final long E8 = 0x1000000000000000L;
        public static final long F8 = 0x2000000000000000L;
        public static final long G8 = 0x4000000000000000L;
        public static final long H8 = 0x8000000000000000L;
    }
}
