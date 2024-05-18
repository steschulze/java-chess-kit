package de.stefanet.javachesskit.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import de.stefanet.javachesskit.bitboard.Bitboard;
import org.junit.jupiter.api.Test;
import java.util.Iterator;

class SquareSetTest {

    @Test
    void testEqual() {
        SquareSet set = new SquareSet(Bitboard.CENTER);
        SquareSet other = new SquareSet(Bitboard.CENTER);

        assertEquals(set, other);
    }

    @Test
    void testNotEqual() {
        SquareSet set = new SquareSet(Bitboard.Ranks.RANK_2);
        SquareSet other = new SquareSet(Bitboard.Ranks.RANK_6);

        assertNotEquals(set, other);
    }

    @Test
    void testHashCode_withSameMask() {
        SquareSet set1 = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.C3);
        SquareSet set2 = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.C3);

        assertEquals(set1.hashCode(), set2.hashCode());
    }

    @Test
    void hashCode_withDifferentMask() {
        SquareSet set1 = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.C3);
        SquareSet set2 = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.H8);

        assertNotEquals(set1.hashCode(), set2.hashCode());
    }

    @Test
    void testSize() {
        SquareSet set1 = new SquareSet(0b10000000_01000000_00100000_00010000_00001000_00000100_00000010_00000001L);
        SquareSet set2 = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.E1
                | Bitboard.Squares.H1);

        assertEquals(8, set1.size());
        assertEquals(3, set2.size());
    }

    @Test
    void testEmpty() {
        SquareSet set1 = new SquareSet();
        SquareSet set2 = new SquareSet(0xFF);

        assertTrue(set1.isEmpty());
        assertFalse(set2.isEmpty());
    }

    @Test
    void testContains() {
        SquareSet set = new SquareSet(0xFF);

        assertTrue(set.contains(Square.A1));
        assertTrue(set.contains(Square.H1));

        assertFalse(set.contains(Square.A2));
        assertFalse(set.contains(null));
    }

    @Test
    void testIterator() {
        SquareSet set = new SquareSet(
                Bitboard.Squares.A3
                | Bitboard.Squares.C6);

        assertEquals(2, set.size());
        Iterator<Square> iterator = set.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(Square.A3, iterator.next());

        assertTrue(iterator.hasNext());
        assertEquals(Square.C6, iterator.next());
    }

    @Test
    void testToArray() {
        Square[] expected = new Square[]{Square.A1, Square.C3, Square.H8};

        SquareSet set = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.C3
                | Bitboard.Squares.H8);
        assertArrayEquals(expected, set.toArray());
    }

    @Test
    void testToArray_withEmptyArray() {
        SquareSet set = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.C3
                | Bitboard.Squares.H8);
        Square[] input = new Square[0];
        Square[] expected = new Square[]{Square.A1, Square.C3, Square.H8};

        assertArrayEquals(expected, set.toArray(input));
    }

    @Test
    void testToArray_withLargerArray() {
        SquareSet set = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.C3);
        Square[] input = new Square[5];
        Square[] expected = new Square[]{Square.A1, Square.C3, null, null, null};

        assertArrayEquals(expected, set.toArray(input));
    }

    @Test
    void testToArray_withExactSizeArray() {
        SquareSet set = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.C3
                | Bitboard.Squares.H8);
        Square[] input = new Square[3];
        Square[] expected = new Square[]{Square.A1, Square.C3, Square.H8};

        assertArrayEquals(expected, set.toArray(input));
        assertSame(input, set.toArray(input));
    }

    @Test
    void testToArray_withNullArray() {
        SquareSet set = new SquareSet(
                Bitboard.Squares.A1
                | Bitboard.Squares.C3
                | Bitboard.Squares.H8);

        assertThrows(NullPointerException.class, () -> set.toArray(null));
    }

    @Test
    void testAdd() {
        SquareSet set = new SquareSet();
        assertEquals(0, set.size());

        set.add(Square.E4);
        set.add(Square.D5);

        assertEquals(2, set.size());
        assertTrue(set.contains(Square.E4));
        assertTrue(set.contains(Square.D5));
    }

    @Test
    void testAdd_withDuplicateSquare() {
        SquareSet set = new SquareSet(Bitboard.CORNERS);
        assertEquals(4, set.size());

        boolean changed = set.add(Square.H1);

        assertFalse(changed);
        assertEquals(4, set.size());
    }

    @Test
    void testRemove() {
        SquareSet set = new SquareSet(
                Bitboard.Squares.E4
                | Bitboard.Squares.D5);
        assertEquals(2, set.size());

        assertTrue(set.remove(Square.E4));
        assertEquals(1, set.size());

        assertFalse(set.remove(Square.A1));
        assertTrue(set.remove(Square.D5));

        assertTrue(set.isEmpty());
    }

    @Test
    void testContainsAll() {
        SquareSet set = new SquareSet(0xFF_00_FF_00_FF_00_FF_00L);
        SquareSet subset = new SquareSet(
                Bitboard.Ranks.RANK_2
                | Bitboard.Ranks.RANK_4);

        assertTrue(set.containsAll(subset));

        subset.add(Square.D5);

        assertFalse(set.containsAll(subset));
    }

    @Test
    void testAddAll() {
        SquareSet set = new SquareSet();
        SquareSet set1 = new SquareSet(
                Bitboard.Files.FILE_A
                | Bitboard.Ranks.RANK_4);

        assertTrue(set.addAll(set1));
        assertEquals(15, set.size());
    }

    @Test
    void testRetainAll() {
        SquareSet set1 = new SquareSet(
                Bitboard.Files.FILE_A
                | Bitboard.Ranks.RANK_4);
        SquareSet set2 = new SquareSet(Bitboard.Files.FILE_D);

        assertTrue(set1.retainAll(set2));
        assertEquals(1, set1.size());
        assertTrue(set1.contains(Square.D4));
    }

    @Test
    void testRemoveAll() {
        SquareSet set1 = new SquareSet(
                Bitboard.Files.FILE_A
                | Bitboard.Files.FILE_D);
        SquareSet set2 = new SquareSet(Bitboard.Files.FILE_A);

        assertTrue(set1.removeAll(set2));
        assertEquals(8, set1.size());
    }

    @Test
    void testClear() {
        SquareSet set = new SquareSet(
                Bitboard.Files.FILE_A
                | Bitboard.Files.FILE_D);
        assertFalse(set.isEmpty());

        set.clear();
        assertTrue(set.isEmpty());
    }


}