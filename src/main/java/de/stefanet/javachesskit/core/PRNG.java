package de.stefanet.javachesskit.core;

public class PRNG {
	private long s;

	public long rand64() {
		s ^= s >>> 12;
		s ^= s << 25;
		s ^= s >>> 27;
		return s * 2685821657736338717L;
	}

	public PRNG(long seed) {
		if (seed == 0) {
			throw new IllegalArgumentException("Seed must not be zero.");
		}
		this.s = seed;
	}
}
