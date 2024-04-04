package de.stefanet.javachesskit;

import java.util.*;

public class SquareSet implements Set<Square> {

	private long mask;

	public SquareSet() {
		this(0);
	}

	public SquareSet(long squares) {
		this.mask = squares;
	}

	@Override
	public int size() {
		return Long.bitCount(mask);
	}

	@Override
	public boolean isEmpty() {
		return mask == 0;
	}

	@Override
	public boolean contains(Object o) {
		if (o == null || o.getClass() != Square.class) return false;

		Square square = (Square) o;
		return (Bitboard.SQUARES[square.ordinal()] & mask) != 0;
	}

	@Override
	public Iterator<Square> iterator() {
		return Arrays.stream(BitboardUtils.scanForward(mask))
				.mapToObj(Square::fromIndex)
				.iterator();
	}

	@Override
	public Object[] toArray() {
		Square[] squares = new Square[Long.bitCount(mask)];
		int index = 0;

		for (int i = 0; i < 64; i++) {
			if ((mask & (1L << i)) != 0) {
				squares[index++] = Square.fromIndex(i);
			}
		}

		return squares;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size()) {
			return (T[]) Arrays.copyOf(toArray(), size(), a.getClass());
		}
		System.arraycopy(toArray(), 0, a, 0, size());
		if (a.length > size()) {
			a[size()] = null;
		}
		return a;
	}

	@Override
	public boolean add(Square square) {
		int index = square.ordinal();
		if ((mask & Bitboard.SQUARES[index]) == 0) {
			this.mask |= Bitboard.SQUARES[index];
			return true;
		}

		return false;
	}

	@Override
	public boolean remove(Object o) {
		if (o.getClass() != Square.class) return false;

		Square square = (Square) o;
		int index = square.ordinal();

		if ((mask & Bitboard.SQUARES[index]) != 0) {
			this.mask &= ~Bitboard.SQUARES[index];
			return true;
		}

		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object obj : c) {
			if (!contains(obj)) return false;
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Square> c) {
		boolean modified = false;
		for (Square square : c) {
			if (add(square)) modified = true;
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		for (Iterator<Square> iterator = iterator(); iterator.hasNext(); ) {
			Square square = iterator.next();
			if (!c.contains(square)) {
				remove(square);
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		for (Object obj : c) {
			if (remove(obj)) modified = true;
		}
		return modified;
	}

	@Override
	public void clear() {
		this.mask = 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SquareSet other = (SquareSet) o;
		return this.mask == other.mask;
	}

	@Override
	public int hashCode() {
		return Objects.hash(mask);
	}
}
