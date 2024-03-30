package de.stefanet.javachesskit.polyglot;

/**
 * Pseudo-random number generator (PRNG) based on xorshift128* algorithm.
 */
public class PRNG {
	private long s;

	/**
	 * Generates a 64-bit random number.
	 *
	 * @return A 64-bit random number.
	 */
	public long rand64() {
		s ^= s >>> 12;
		s ^= s << 25;
		s ^= s >>> 27;
		return s * 2685821657736338717L;
	}

	/**
	 * Constructs a PRNG with the specified seed.
	 *
	 * @param seed The seed value for initializing the PRNG.
	 * @throws IllegalArgumentException If the seed is zero.
	 */
	public PRNG(long seed) {
		if (seed == 0) {
			throw new IllegalArgumentException("Seed must not be zero.");
		}
		this.s = seed;
	}
}
