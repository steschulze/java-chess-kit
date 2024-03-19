package de.stefanet.javachesskit.bitboard;

import java.util.Iterator;

import static de.stefanet.javachesskit.bitboard.Bitboard.ALL;
import static de.stefanet.javachesskit.bitboard.Bitboard.RAYS;

public class BitboardUtils {
	public static int lsb(long bb) {
		return Long.numberOfTrailingZeros(bb);
	}

	public static int msb(long bb) {
		return 63 - Long.numberOfLeadingZeros(bb);
	}

	public static Iterator<Square> scanForward(long bb) {
		return new Iterator<Square>() {
			long currentBB = bb;

			@Override
			public boolean hasNext() {
				return currentBB != 0;
			}

			@Override
			public Square next() {
				int square = Long.numberOfTrailingZeros(currentBB);
				currentBB ^= 1L << square;
				return Square.fromIndex(square);
			}
		};
	}

	public static Iterator<Square> scanReversed(long bb) {
		return new Iterator<Square>() {
			long currentBB = bb;

			@Override
			public boolean hasNext() {
				return currentBB != 0;
			}

			@Override
			public Square next() {
				int square = msb(currentBB);
				currentBB ^= 1L << square;
				return Square.fromIndex(square);
			}
		};
	}

	public static long flipVertical(long bb) {
		bb = ((bb >>> 8) & 0x00ff_00ff_00ff_00ffL) | ((bb & 0x00ff_00ff_00ff_00ffL) << 8);
		bb = ((bb >>> 16) & 0x0000_ffff_0000_ffffL) | ((bb & 0x0000_ffff_0000_ffffL) << 16);
		bb = (bb >>> 32) | ((bb & 0x0000_0000_ffff_ffffL) << 32);
		return bb;
	}

	public static long flipHorizontal(long bb) {
		bb = ((bb >>> 1) & 0x5555_5555_5555_5555L) | ((bb & 0x5555_5555_5555_5555L) << 1);
		bb = ((bb >>> 2) & 0x3333_3333_3333_3333L) | ((bb & 0x3333_3333_3333_3333L) << 2);
		bb = ((bb >>> 4) & 0x0f0f_0f0f_0f0f_0f0fL) | ((bb & 0x0f0f_0f0f_0f0f_0f0fL) << 4);
		return bb;
	}

	public static long flipDiagonal(long bb) {
		long t = (bb ^ (bb << 28)) & 0x0f0f_0f0f_0000_0000L;
		bb = bb ^ t ^ (t >>> 28);
		t = (bb ^ (bb << 14)) & 0x3333_0000_3333_0000L;
		bb = bb ^ t ^ (t >>> 14);
		t = (bb ^ (bb << 7)) & 0x5500_5500_5500_5500L;
		bb = bb ^ t ^ (t >>> 7);
		return bb;
	}

	public static long flipAntiDiagonal(long bb) {
		long t = bb ^ (bb << 36);
		bb = bb ^ ((t ^ (bb >>> 36)) & 0xf0f0_f0f0_0f0f_0f0fL);
		t = (bb ^ (bb << 18)) & 0xcccc_0000_cccc_0000L;
		bb = bb ^ t ^ (t >>> 18);
		t = (bb ^ (bb << 9)) & 0xaa00_aa00_aa00_aa00L;
		bb = bb ^ t ^ (t >>> 9);
		return bb;
	}

	public static long shiftDown(long b) {
		return b >>> 8;
	}

	public static long shift2Down(long b) {
		return b >>> 16;
	}

	public static long shiftUp(long b) {
		return (b << 8) & Bitboard.ALL;
	}

	public static long shift2Up(long b) {
		return (b << 16) & Bitboard.ALL;
	}

	public static long shiftRight(long b) {
		return (b << 1) & ~Bitboard.Files.FILE_A & Bitboard.ALL;
	}

	public static long shift2Right(long b) {
		return (b << 2) & ~Bitboard.Files.FILE_A & ~Bitboard.Files.FILE_B & Bitboard.ALL;
	}

	public static long shiftLeft(long b) {
		return (b >>> 1) & ~Bitboard.Files.FILE_H;
	}

	public static long shift2Left(long b) {
		return (b >>> 2) & ~Bitboard.Files.FILE_G & ~Bitboard.Files.FILE_H;
	}

	public static long shiftUpLeft(long b) {
		return (b << 7) & ~Bitboard.Files.FILE_H & Bitboard.ALL;
	}

	public static long shiftUpRight(long b) {
		return (b << 9) & ~Bitboard.Files.FILE_A & Bitboard.ALL;
	}

	public static long shiftDownLeft(long b) {
		return (b >>> 9) & ~Bitboard.Files.FILE_H;
	}

	public static long shiftDownRight(long b) {
		return (b >>> 7) & ~Bitboard.Files.FILE_A;
	}

	public static long ray(Square a, Square b) {
		return RAYS[a.ordinal()][b.ordinal()];
	}

	public static long between(Square a, Square b) {
		long bb = ray(a, b) & ((ALL << a.ordinal()) ^ (ALL << b.ordinal()));
		return bb & (bb - 1);
	}
}
