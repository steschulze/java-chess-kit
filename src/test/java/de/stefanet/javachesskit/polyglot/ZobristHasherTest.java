package de.stefanet.javachesskit.polyglot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.stefanet.javachesskit.Board;
import org.junit.jupiter.api.Test;

class ZobristHasherTest {

    @Test
    void hasherShouldInitializeWithSeed() {
        ZobristHasher hasher = new ZobristHasher(1234);
        assertNotNull(hasher);
    }

    @Test
    void hasherShouldGenerateDifferentValuesForDifferentSeeds() {
        ZobristHasher hasher1 = new ZobristHasher(1234);
        ZobristHasher hasher2 = new ZobristHasher(5678);

        Board board = new Board();
        assertNotEquals(hasher1.hash(board), hasher2.hash(board));
    }

    @Test
    void hasherShouldGenerateSameValueForSameSeed() {
        ZobristHasher hasher1 = new ZobristHasher(1234);
        ZobristHasher hasher2 = new ZobristHasher(1234);

        Board board = new Board();
        assertEquals(hasher1.hash(board), hasher2.hash(board));
    }

    @Test
    void hasherShouldHandleNegativeSeed() {
        ZobristHasher hasher = new ZobristHasher(-1234);
        assertNotNull(hasher);
    }

    @Test
    void hasherShouldHandleZeroSeed() {
        assertThrows(IllegalArgumentException.class, () -> new ZobristHasher(0));
    }

}