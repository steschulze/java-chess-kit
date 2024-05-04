package de.stefanet.javachesskit.bitboard;

import static de.stefanet.javachesskit.bitboard.Bitboard.ALL;
import static de.stefanet.javachesskit.bitboard.Bitboard.RAYS;

/**
 * Utility class for bitboards.
 */
public final class BitboardUtils {

    private BitboardUtils() {
    }

    /**
     * Returns the index of the least significant one-bit of the given bitboard.
     *
     * @param bb The bitboard.
     * @return The index of the least significant one-bit.
     */
    public static int lsb(long bb) {
        return Long.numberOfTrailingZeros(bb);
    }

    /**
     * Returns the index of the most significant one-bit of the given bitboard.
     *
     * @param bb The bitboard.
     * @return The index of the most significant one-bit.
     */
    public static int msb(long bb) {
        return 63 - Long.numberOfLeadingZeros(bb);
    }

    /**
     * Returns an array of indices of the one-bits in the given bitboard.
     *
     * <p>The indices are ordered from least significant to most significant.
     *
     * @param bb The bitboard.
     * @return An array of indices of the one-bits.
     */
    public static int[] scanForward(long bb) {
        int count = Long.bitCount(bb);
        int[] indices = new int[count];

        int index = 0;
        for (int i = 0; i <= 63 && index < count; i++) {
            if (((bb >> i) & 1) == 1) {
                indices[index++] = i;
            }
        }

        return indices;
    }

    /**
     * Returns an array of indices of the one-bits in the given bitboard.
     *
     * <p>The indices are ordered from most significant to least significant.
     *
     * @param bb The bitboard.
     * @return An array of indices of the one-bits.
     */
    public static int[] scanReversed(long bb) {
        int count = Long.bitCount(bb);
        int[] indices = new int[count];

        int index = 0;
        for (int i = 63; i >= 0 && index < count; i--) {
            if (((bb >> i) & 1) == 1) {
                indices[index++] = i;
            }
        }

        return indices;
    }

    /**
     * Flips the given bitboard vertically.
     *
     * @param bb The bitboard.
     * @return The bitboard flipped vertically.
     */
    public static long flipVertical(long bb) {
        bb = ((bb >>> 8) & 0x00ff_00ff_00ff_00ffL) | ((bb & 0x00ff_00ff_00ff_00ffL) << 8);
        bb = ((bb >>> 16) & 0x0000_ffff_0000_ffffL) | ((bb & 0x0000_ffff_0000_ffffL) << 16);
        bb = (bb >>> 32) | ((bb & 0x0000_0000_ffff_ffffL) << 32);
        return bb;
    }

    /**
     * Flips the given bitboard horizontally.
     *
     * @param bb The bitboard.
     * @return The bitboard flipped horizontally.
     */
    public static long flipHorizontal(long bb) {
        bb = ((bb >>> 1) & 0x5555_5555_5555_5555L) | ((bb & 0x5555_5555_5555_5555L) << 1);
        bb = ((bb >>> 2) & 0x3333_3333_3333_3333L) | ((bb & 0x3333_3333_3333_3333L) << 2);
        bb = ((bb >>> 4) & 0x0f0f_0f0f_0f0f_0f0fL) | ((bb & 0x0f0f_0f0f_0f0f_0f0fL) << 4);
        return bb;
    }

    /**
     * Flips the given bitboard along the main diagonal (A1-H8).
     *
     * @param bb The bitboard.
     * @return The bitboard flipped along the main diagonal.
     */
    public static long flipDiagonal(long bb) {
        long t = (bb ^ (bb << 28)) & 0x0f0f_0f0f_0000_0000L;
        bb = bb ^ t ^ (t >>> 28);
        t = (bb ^ (bb << 14)) & 0x3333_0000_3333_0000L;
        bb = bb ^ t ^ (t >>> 14);
        t = (bb ^ (bb << 7)) & 0x5500_5500_5500_5500L;
        bb = bb ^ t ^ (t >>> 7);
        return bb;
    }

    /**
     * Flips the given bitboard along the anti-diagonal (H1-A8).
     *
     * @param bb The bitboard.
     * @return The bitboard flipped along the anti-diagonal.
     */
    public static long flipAntiDiagonal(long bb) {
        long t = bb ^ (bb << 36);
        bb = bb ^ ((t ^ (bb >>> 36)) & 0xf0f0_f0f0_0f0f_0f0fL);
        t = (bb ^ (bb << 18)) & 0xcccc_0000_cccc_0000L;
        bb = bb ^ t ^ (t >>> 18);
        t = (bb ^ (bb << 9)) & 0xaa00_aa00_aa00_aa00L;
        bb = bb ^ t ^ (t >>> 9);
        return bb;
    }

    /**
     * Shifts the given bitboard one rank down.
     *
     * @param b The bitboard.
     * @return The bitboard shifted one rank down.
     */
    public static long shiftDown(long b) {
        return b >>> 8;
    }

    /**
     * Shifts the given bitboard two ranks down.
     *
     * @param b The bitboard.
     * @return The bitboard shifted two ranks down.
     */
    public static long shift2Down(long b) {
        return b >>> 16;
    }

    /**
     * Shifts the given bitboard one rank up.
     *
     * @param b The bitboard.
     * @return The bitboard shifted one rank up.
     */
    public static long shiftUp(long b) {
        return (b << 8) & Bitboard.ALL;
    }

    /**
     * Shifts the given bitboard two ranks up.
     *
     * @param b The bitboard.
     * @return The bitboard shifted two ranks up.
     */
    public static long shift2Up(long b) {
        return (b << 16) & Bitboard.ALL;
    }

    /**
     * Shifts the given bitboard one file to the right.
     *
     * @param b The bitboard.
     * @return The bitboard shifted one file to the right.
     */
    public static long shiftRight(long b) {
        return (b << 1) & ~Bitboard.Files.FILE_A & Bitboard.ALL;
    }

    /**
     * Shifts the given bitboard two files to the right.
     *
     * @param b The bitboard.
     * @return The bitboard shifted two files to the right.
     */
    public static long shift2Right(long b) {
        return (b << 2) & ~Bitboard.Files.FILE_A & ~Bitboard.Files.FILE_B & Bitboard.ALL;
    }

    /**
     * Shifts the given bitboard one file to the left.
     *
     * @param b The bitboard.
     * @return The bitboard shifted one file to the left.
     */
    public static long shiftLeft(long b) {
        return (b >>> 1) & ~Bitboard.Files.FILE_H;
    }

    /**
     * Shifts the given bitboard two files to the left.
     *
     * @param b The bitboard.
     * @return The bitboard shifted two files to the left.
     */
    public static long shift2Left(long b) {
        return (b >>> 2) & ~Bitboard.Files.FILE_G & ~Bitboard.Files.FILE_H;
    }

    /**
     * Shifts the given bitboard one rank up and one file to the left.
     *
     * @param b The bitboard.
     * @return The bitboard shifted one rank up and one file to the left.
     */
    public static long shiftUpLeft(long b) {
        return (b << 7) & ~Bitboard.Files.FILE_H & Bitboard.ALL;
    }

    /**
     * Shifts the given bitboard one rank up and one file to the right.
     *
     * @param b The bitboard.
     * @return The bitboard shifted one rank up and one file to the right.
     */
    public static long shiftUpRight(long b) {
        return (b << 9) & ~Bitboard.Files.FILE_A & Bitboard.ALL;
    }

    /**
     * Shifts the given bitboard one rank down and one file to the left.
     *
     * @param b The bitboard.
     * @return The bitboard shifted one rank down and one file to the left.
     */
    public static long shiftDownLeft(long b) {
        return (b >>> 9) & ~Bitboard.Files.FILE_H;
    }

    /**
     * Shifts the given bitboard one rank down and one file to the right.
     *
     * @param b The bitboard.
     * @return The bitboard shifted one rank down and one file to the right.
     */
    public static long shiftDownRight(long b) {
        return (b >>> 7) & ~Bitboard.Files.FILE_A;
    }

    /**
     * Returns a bitboard with the ray from square a to square b.
     *
     * @param a The first square.
     * @param b The second square.
     * @return The bitboard with the ray from square a to square b.
     */
    public static long ray(int a, int b) {
        return RAYS[a][b];
    }

    /**
     * Returns a bitboard with the squares between square a and square b.
     *
     * @param a The first square.
     * @param b The second square.
     * @return The bitboard with the squares between square a and square b.
     */
    public static long between(int a, int b) {
        long bb = ray(a, b) & ((ALL << a) ^ (ALL << b));
        return bb & (bb - 1);
    }
}
