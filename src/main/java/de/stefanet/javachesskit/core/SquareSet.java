package de.stefanet.javachesskit.core;

import de.stefanet.javachesskit.bitboard.Bitboard;
import de.stefanet.javachesskit.bitboard.BitboardUtils;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * A class representing a set of squares on a chessboard.
 */
public class SquareSet implements Set<Square> {

    private long mask;

    /**
     * Constructs an empty SquareSet.
     */
    public SquareSet() {
        this(0);
    }

    /**
     * Constructs a SquareSet with the specified squares.
     *
     * @param squares A bitboard representing the squares in this set.
     */
    public SquareSet(long squares) {
        this.mask = squares;
    }

    /**
     * Returns the number of squares in this set.
     *
     * @return The number of squares in this set.
     */
    @Override
    public int size() {
        return Long.bitCount(mask);
    }

    /**
     * Returns true if this set contains no squares.
     *
     * @return True if this set contains no squares, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return mask == 0;
    }

    /**
     * Returns true if this set contains the specified square.
     *
     * @param o The object to be checked for containment in this set.
     * @return True if this set contains the specified square, false otherwise.
     */
    @Override
    public boolean contains(Object o) {
        if (o == null || o.getClass() != Square.class) {
            return false;
        }

        Square square = (Square) o;
        return (Bitboard.SQUARES[square.ordinal()] & mask) != 0;
    }

    /**
     * Returns an iterator over the squares in this set.
     *
     * @return An iterator over the squares in this set.
     */
    @Override
    public Iterator<Square> iterator() {
        return Arrays.stream(BitboardUtils.scanForward(mask))
                .mapToObj(Square::fromIndex)
                .iterator();
    }

    /**
     * Returns an array containing all the squares in this set.
     *
     * @return An array containing all the squares in this set.
     */
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

    /**
     * Returns an array containing all the squares in this set;
     * the runtime type of the returned array is that of the specified array.
     *
     * @param a   The array into which the elements of this set are to be stored,
     *            if it is big enough; otherwise, a new array of the same runtime type is allocated for this purpose.
     * @param <T> The runtime type of the array to contain the collection
     * @return An array containing all the squares in this set.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
        } else if (a.length > size) {
            a[size] = null;
        }
        System.arraycopy(toArray(), 0, a, 0, size);
        return a;
    }

    /**
     * Adds the specified square to this set if it is not already present.
     *
     * @param square The square to be added to this set.
     * @return True if this set did not already contain the specified square, false otherwise.
     */
    @Override
    public boolean add(Square square) {
        int index = square.ordinal();
        if ((mask & Bitboard.SQUARES[index]) == 0) {
            this.mask |= Bitboard.SQUARES[index];
            return true;
        }

        return false;
    }

    /**
     * Removes the specified square from this set if it is present.
     *
     * @param o The object to be removed from this set, if present.
     * @return True if this set contained the specified square, false otherwise.
     */
    @Override
    public boolean remove(Object o) {
        if (o.getClass() != Square.class) {
            return false;
        }

        Square square = (Square) o;
        int index = square.ordinal();

        if ((mask & Bitboard.SQUARES[index]) != 0) {
            this.mask &= ~Bitboard.SQUARES[index];
            return true;
        }

        return false;
    }

    /**
     * Returns true if this set contains all the squares in the specified collection.
     *
     * @param c Collection to be checked for containment in this set.
     * @return True if this set contains all the squares in the specified collection, false otherwise.
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object obj : c) {
            if (!contains(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds all the squares in the specified collection to this set if they're not already present.
     *
     * @param c Collection containing squares to be added to this set.
     * @return True if this set changed as a result of the call, false otherwise.
     */
    @Override
    public boolean addAll(Collection<? extends Square> c) {
        boolean modified = false;
        for (Square square : c) {
            if (add(square)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Retains only the squares in this set that are contained in the specified collection.
     *
     * @param c Collection containing squares to be retained in this set.
     * @return True if this set changed as a result of the call, false otherwise.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        for (Square square : this) {
            if (!c.contains(square)) {
                remove(square);
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes from this set all of its squares that are contained in the specified collection.
     *
     * @param c Collection containing squares to be removed from this set.
     * @return True if this set changed as a result of the call, false otherwise.
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object obj : c) {
            if (remove(obj)) {
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Removes all the squares from this set.
     */
    @Override
    public void clear() {
        this.mask = 0;
    }

    /**
     * Compares the specified object with this set for equality.
     *
     * @param o Object to be compared for equality with this set.
     * @return True if the specified object is equal to this set, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SquareSet other = (SquareSet) o;
        return this.mask == other.mask;
    }

    /**
     * Returns the hash value for this set.
     *
     * @return The hash value for this set.
     */
    @Override
    public int hashCode() {
        return Objects.hash(mask);
    }
}
