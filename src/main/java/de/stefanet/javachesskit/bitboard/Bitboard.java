package de.stefanet.javachesskit.bitboard;

import static de.stefanet.javachesskit.bitboard.Bitboard.Files.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.Ranks.*;
import static de.stefanet.javachesskit.bitboard.Bitboard.Squares.*;

public class Bitboard {

	static long[] FILES = {FILE_A, Files.FILE_B, FILE_C, Files.FILE_D, FILE_E, FILE_F, FILE_G, FILE_H};
	static long[] RANKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};

	static long[] SQUARES = {A1, B1, C1, D1, E1, F1, G1, H1,
			A2, B2, C2, D2, E2, F2, G2, H2,
			A3, B3, C3, D3, E3, F3, G3, H3,
			A4, B4, C4, D4, E4, F4, G4, H4,
			A5, B5, C5, D5, E5, F5, G5, H5,
			A6, B6, C6, D6, E6, F6, G6, H6,
			A7, B7, C7, D7, E7, F7, G7, H7,
			A8, B8, C8, D8, E8, F8, G8, H8};

	static long EMPTY = 0;
	static long ALL = 0xFFFFFFFFFFFFFFFFL;
	static long CORNERS = A1 | H1 | A8 | H8;
	static long CENTER = D4 | E4 | D5 | E5;

	static long LIGHT_SQUARES = 0x55AA55AA55AA55AAL;
	static long DARK_SQUARES = 0xAA55AA55AA55AA55L;

	static long BACKRANK = RANK_1 | RANK_7;

	public static class Files {
		static long FILE_A = 0x0101010101010101L;
		static long FILE_B = FILE_A << 1;
		static long FILE_C = FILE_B << 1;
		static long FILE_D = FILE_C << 1;
		static long FILE_E = FILE_D << 1;
		static long FILE_F = FILE_E << 1;
		static long FILE_G = FILE_F << 1;
		static long FILE_H = FILE_G << 1;
	}

	static class Ranks {
		static long RANK_1 = 0xFFL;
		static long RANK_2 = RANK_1 << 8;
		static long RANK_3 = RANK_2 << 8;
		static long RANK_4 = RANK_3 << 8;
		static long RANK_5 = RANK_4 << 8;
		static long RANK_6 = RANK_5 << 8;
		static long RANK_7 = RANK_6 << 8;
		static long RANK_8 = RANK_7 << 8;
	}

	static class Squares {
		static long A1 = 0x1L;
		static long B1 = 0x2L;
		static long C1 = 0x4L;
		static long D1 = 0x8L;
		static long E1 = 0x10L;
		static long F1 = 0x20L;
		static long G1 = 0x40L;
		static long H1 = 0x80L;

		static long A2 = 0x100L;
		static long B2 = 0x200L;
		static long C2 = 0x400L;
		static long D2 = 0x800L;
		static long E2 = 0x1000L;
		static long F2 = 0x2000L;
		static long G2 = 0x4000L;
		static long H2 = 0x8000L;

		static long A3 = 0x10000L;
		static long B3 = 0x20000L;
		static long C3 = 0x40000L;
		static long D3 = 0x80000L;
		static long E3 = 0x100000L;
		static long F3 = 0x200000L;
		static long G3 = 0x400000L;
		static long H3 = 0x800000L;

		static long A4 = 0x1000000L;
		static long B4 = 0x2000000L;
		static long C4 = 0x4000000L;
		static long D4 = 0x8000000L;
		static long E4 = 0x10000000L;
		static long F4 = 0x20000000L;
		static long G4 = 0x40000000L;
		static long H4 = 0x80000000L;

		static long A5 = 0x100000000L;
		static long B5 = 0x200000000L;
		static long C5 = 0x400000000L;
		static long D5 = 0x800000000L;
		static long E5 = 0x1000000000L;
		static long F5 = 0x2000000000L;
		static long G5 = 0x4000000000L;
		static long H5 = 0x8000000000L;

		static long A6 = 0x10000000000L;
		static long B6 = 0x20000000000L;
		static long C6 = 0x40000000000L;
		static long D6 = 0x80000000000L;
		static long E6 = 0x100000000000L;
		static long F6 = 0x200000000000L;
		static long G6 = 0x400000000000L;
		static long H6 = 0x800000000000L;

		static long A7 = 0x1000000000000L;
		static long B7 = 0x2000000000000L;
		static long C7 = 0x4000000000000L;
		static long D7 = 0x8000000000000L;
		static long E7 = 0x10000000000000L;
		static long F7 = 0x20000000000000L;
		static long G7 = 0x40000000000000L;
		static long H7 = 0x80000000000000L;

		static long A8 = 0x100000000000000L;
		static long B8 = 0x200000000000000L;
		static long C8 = 0x400000000000000L;
		static long D8 = 0x800000000000000L;
		static long E8 = 0x1000000000000000L;
		static long F8 = 0x2000000000000000L;
		static long G8 = 0x4000000000000000L;
		static long H8 = 0x8000000000000000L;
	}
}
