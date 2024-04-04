package de.stefanet.javachesskit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BitboardUtilsTest {

	@Test
	void testLSB() {
		assertEquals(0, BitboardUtils.lsb(Long.MAX_VALUE));
		assertEquals(63, BitboardUtils.lsb(Long.MIN_VALUE));
		assertEquals(2, BitboardUtils.lsb(0b100));
	}

	@Test
	void testMSB() {
		assertEquals(62, BitboardUtils.msb(Long.MAX_VALUE));
		assertEquals(63, BitboardUtils.msb(Long.MIN_VALUE));
		assertEquals(5, BitboardUtils.msb(0b101001));
	}

	@Test
	void testForwardScan() {
		long value = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;
		int[] expected = new int[]{7, 14, 21, 28, 35, 42, 49, 56};

		assertArrayEquals(expected, BitboardUtils.scanForward(value));
	}

	@Test
	void testReversedScan() {
		long value = 0b10000000_01000000_00100000_00010000_00001000_00000100_00000010_00000001L;
		int[] expected = new int[]{63, 54, 45, 36, 27, 18, 9, 0};

		assertArrayEquals(expected, BitboardUtils.scanReversed(value));
	}

	@Test
	void testFlipVertical() {
		long value = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;
		long expected = 0b10000000_01000000_00100000_00010000_00001000_00000100_00000010_00000001L;

		assertEquals(expected, BitboardUtils.flipVertical(value));
	}

	@Test
	void testFlipHorizontal() {
		long value = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;
		long expected = 0b10000000_01000000_00100000_00010000_00001000_00000100_00000010_00000001L;

		assertEquals(expected, BitboardUtils.flipHorizontal(value));
	}

	@Test
	void testFlipDiagonal1() {
		long value = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;

		assertEquals(value, BitboardUtils.flipDiagonal(value));
	}

	@Test
	void testFlipDiagonal2() {
		long value = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
		long expected = 0b00001000_00001000_00001000_00001000_00001000_00001000_00001000_00001000L;

		assertEquals(expected, BitboardUtils.flipDiagonal(value));
	}

	@Test
	void testFlipAntiDiagonal1() {
		long value = 0b00000001_00000010_00000100_00001000_00010000_00100000_01000000_10000000L;

		assertEquals(value, BitboardUtils.flipAntiDiagonal(value));
	}

	@Test
	void testFlipAntiDiagonal2() {
		long value = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
		long expected = 0b00010000_00010000_00010000_00010000_00010000_00010000_00010000_00010000L;

		assertEquals(expected, BitboardUtils.flipAntiDiagonal(value));
	}

	@Test
	void testShiftDown() {
		long value = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
		long expected = 0b00000000_00000000_00000000_00000000_00000000_11111111_00000000_00000000L;

		assertEquals(expected, BitboardUtils.shiftDown(value));
	}

	@Test
	void testShiftDown2() {
		long value = 0b00000000_00000000_00000000_00000000_11111111_00000000_00000000_00000000L;
		long expected = 0b00000000_00000000_00000000_00000000_00000000_00000000_11111111_00000000L;

		assertEquals(expected, BitboardUtils.shift2Down(value));
	}

	@Test
	void testShiftUp() {
		long input1 = 1L << 10;
		long expected1 = 1L << 18;
		long result1 = BitboardUtils.shiftUp(input1);
		assertEquals(expected1, result1);

		long input2 = 1L;
		long expected2 = 1L << 8;
		long result2 = BitboardUtils.shiftUp(input2);
		assertEquals(expected2, result2);

		long input3 = 1L << 63;
		long expected3 = 0L;
		long result3 = BitboardUtils.shiftUp(input3);
		assertEquals(expected3, result3);

		long input4 = Bitboard.ALL;
		long expected4 = (Bitboard.ALL << 8) & Bitboard.ALL;
		long result4 = BitboardUtils.shiftUp(input4);
		assertEquals(expected4, result4);
	}

	@Test
	void testShift2Up() {
		long input = 0x2L;
		long result = BitboardUtils.shift2Up(input);

		assertEquals(0x20000L, result);
	}

	@Test
	void testShiftRightA() {
		long input = 0x01_01_01_01_01_01_01_01L;
		long result = BitboardUtils.shiftRight(input);

		assertEquals(0x02_02_02_02_02_02_02_02L, result);
	}

	@Test
	void testShiftRightH() {
		long input = 0x80_80_80_80_80_80_80_80L;
		long result = BitboardUtils.shiftRight(input);

		assertEquals(0, result);
	}

	@Test
	void testShift2RightA() {
		long input = 0x01_01_01_01_01_01_01_01L;
		long result = BitboardUtils.shift2Right(input);

		assertEquals(0x04_04_04_04_04_04_04_04L, result);
	}

	@Test
	void testShift2RightG() {
		long input = 0x40_40_40_40_40_40_40_40L;
		long result = BitboardUtils.shift2Right(input);

		assertEquals(0, result);
	}

	@Test
	void testShift2RightH() {
		long input = 0x80_80_80_80_80_80_80_80L;
		long result = BitboardUtils.shiftRight(input);

		assertEquals(0, result);
	}

	@Test
	void testShiftLefttA() {
		long input = 0x01_01_01_01_01_01_01_01L;
		long result = BitboardUtils.shiftLeft(input);

		assertEquals(0, result);
	}

	@Test
	void testShiftLeftH() {
		long input = 0x80_80_80_80_80_80_80_80L;
		long result = BitboardUtils.shiftLeft(input);

		assertEquals(0x40_40_40_40_40_40_40_40L, result);
	}

	@Test
	void testShift2LeftA() {
		long input = 0x01_01_01_01_01_01_01_01L;
		long result = BitboardUtils.shift2Left(input);

		assertEquals(0, result);
	}

	@Test
	void testShift2LeftB() {
		long input = 0x02_02_02_02_02_02_02_02L;
		long result = BitboardUtils.shift2Left(input);

		assertEquals(0, result);
	}

	@Test
	void testShiftUpLeft1() {
		long input = 0x00_00_00_00_00_00_00_FFL;
		long result = BitboardUtils.shiftUpLeft(input);

		assertEquals(0x00_00_00_00_00_00_7F_00L, result);
	}

	@Test
	void testShiftUpRight1() {
		long input = 0x00_00_00_00_00_00_00_FFL;
		long result = BitboardUtils.shiftUpRight(input);

		assertEquals(0x00_00_00_00_00_00_FE_00L, result);
	}

	@Test
	void testShiftDownLeft8() {
		long input = 0xFF_00_00_00_00_00_00_00L;
		long result = BitboardUtils.shiftDownLeft(input);

		assertEquals(0x00_7F_00_00_00_00_00_00L, result);
	}

	@Test
	void testShiftDownRight8() {
		long input = 0xFF_00_00_00_00_00_00_00L;
		long result = BitboardUtils.shiftDownRight(input);

		assertEquals(0x00_FE_00_00_00_00_00_00L, result);
	}

	@Test
	void testRayA1H8() {
		long result = BitboardUtils.ray(Square.B2.ordinal(), Square.E5.ordinal());

		assertEquals(0x80_40_20_10_08_04_02_01L, result);
	}

	@Test
	void testRayF1H3() {
		long result = BitboardUtils.ray(Square.F1.ordinal(), Square.G2.ordinal());

		assertEquals(0x80_40_20L, result);
	}

	@Test
	void testRayA1H1() {
		long result = BitboardUtils.ray(Square.F1.ordinal(), Square.H1.ordinal());

		assertEquals(0xFFL, result);
	}

	@Test
	void testRayA1C2() {
		long result = BitboardUtils.ray(Square.A1.ordinal(), Square.C2.ordinal());

		assertEquals(0, result);
	}

	@Test
	void testBetweenB1G1() {
		long result = BitboardUtils.between(Square.B1.ordinal(), Square.G1.ordinal());

		assertEquals(0x3C, result);
	}

	@Test
	void testBetweenA1H8() {
		long result = BitboardUtils.between(Square.A1.ordinal(), Square.H8.ordinal());

		assertEquals(0x00_40_20_10_08_04_02_00L, result);
	}


}