package de.stefanet.javachesskit.polyglot;

/**
 * Pseudo-random number generator (PseudoRandomNumberGenerator) based on xorshift128* algorithm.
 */
public class PseudoRandomNumberGenerator {
    private long state;

    /**
     * Generates a 64-bit random number.
     *
     * @return A 64-bit random number.
     */
    public long rand64() {
        state ^= state >>> 12;
        state ^= state << 25;
        state ^= state >>> 27;
        return state * 2685821657736338717L;
    }

    /**
     * Constructs a PseudoRandomNumberGenerator with the specified seed.
     *
     * @param seed The seed value for initializing the PseudoRandomNumberGenerator.
     * @throws IllegalArgumentException If the seed is zero.
     */
    public PseudoRandomNumberGenerator(long seed) {
        if (seed == 0) {
            throw new IllegalArgumentException("Seed must not be zero.");
        }
        this.state = seed;
    }
}
