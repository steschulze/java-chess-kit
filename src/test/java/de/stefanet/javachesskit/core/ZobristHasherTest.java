package de.stefanet.javachesskit.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZobristHasherTest {

	@Test
	void testHashing() {
		ZobristHasher hasher = new ZobristHasher();

		Position position = new Position();
		assertEquals(0x5C9C0485E4F84772L, hasher.hashPosition(position));

		position = new Position("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");

		assertEquals(0x23292DB2AB441B42L, hasher.hashPosition(position));

		position = new Position("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2");

		assertEquals(0x942A949F0FBEDDD1L, hasher.hashPosition(position));
	}

}