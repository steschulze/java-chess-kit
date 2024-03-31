package de.stefanet.javachesskit.polyglot;

import de.stefanet.javachesskit.bitboard.Board;

import java.util.function.Function;

public final class Polyglot {
	private Polyglot() {
	}

	public static long zobristHash(Board board) {
		return zobristHash(board, new ZobristHasher()::hash);
	}

	public static long zobristHash(Board board, Function<Board, Long> hashFunction) {
		return hashFunction.apply(board);
	}
}
