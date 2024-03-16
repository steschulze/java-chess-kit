package de.stefanet.javachesskit.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZobristHasherTest {

	@Test
	void testHashing_startingPosition() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position();
		assertEquals(0x463b96181691fc9cL, hasher.hashPosition(position));
	}

	@Test
	void testHashing_after_e4() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
		assertEquals(0x823c9b50fd114196L, hasher.hashPosition(position));
	}

	@Test
	void testHashing_after_e4_d5() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2");
		assertEquals(0x0756b94461c50fb0L, hasher.hashPosition(position));
	}

	@Test
	void testHashing_after_e4_d5_e5() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position("rnbqkbnr/ppp1pppp/8/3pP3/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2");
		assertEquals(0x0662fafb965db29d4L, hasher.hashPosition(position));
	}

	@Test
	void testHashing_after_e4_d5_e5_f5() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position("rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPP1PPP/RNBQKBNR w KQkq f6 0 3");
		assertEquals(0x22a48b5a8e47ff78L, hasher.hashPosition(position));
	}

	@Test
	void testHashing_after_e4_d5_e5_f5_Ke2() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position("rnbqkbnr/ppp1p1pp/8/3pPp2/8/8/PPPPKPPP/RNBQ1BNR b kq - 0 3");
		assertEquals(0x652a607ca3f242c1L, hasher.hashPosition(position));
	}

	@Test
	void testHashing_after_e4_d5_e5_f5_Ke2_Kf7() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position("rnbq1bnr/ppp1pkpp/8/3pPp2/8/8/PPPPKPPP/RNBQ1BNR w - - 0 4");
		assertEquals(0x00fdd303c946bdd9L, hasher.hashPosition(position));
	}

	@Test
	void testHashing_after_a4_b5_h4_b4_c4() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position("rnbqkbnr/p1pppppp/8/8/PpP4P/8/1P1PPPP1/RNBQKBNR b KQkq c3 0 3");
		assertEquals(0x3c8123ea7b067637L, hasher.hashPosition(position));
	}

	@Test
	void testHashing_after_a4_b5_h4_b4_c4_bxc3_Ta3() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position("rnbqkbnr/p1pppppp/8/8/P6P/R1p5/1P1PPPP1/1NBQKBNR b Kkq - 0 4");
		assertEquals(0x5c3f9b829b279560L, hasher.hashPosition(position));
	}




}