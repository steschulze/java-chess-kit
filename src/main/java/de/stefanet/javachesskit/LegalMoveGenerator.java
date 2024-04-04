package de.stefanet.javachesskit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class LegalMoveGenerator implements Iterable<Move> {
	private final Board board;

	public LegalMoveGenerator(Board board) {
		this.board = board;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		LegalMoveGenerator other = (LegalMoveGenerator) obj;
		return Objects.equals(board, other.board);
	}

	@Override
	public int hashCode() {
		return Objects.hash(board);
	}

	@Override
	public Iterator<Move> iterator() {
		return board.generateLegalMoves().iterator();
	}

	public boolean contains(Object obj) {
		if (!(obj instanceof Move)) return false;
		Move move = (Move) obj;
		return board.isLegal(move);
	}

	@Override
	public String toString() {
		List<String> uciMoveList = new ArrayList<>();
		for (Move move : this) {
			uciMoveList.add(move.getUciMove());
		}
		return String.format("<LegalMoveGenerator (%s)>", String.join(", ", uciMoveList));
	}

	public boolean any() {
		return iterator().hasNext();
	}

	public int count() {
		int count = 0;
		for (Move move : this) {
			count++;
		}
		return count;
	}
}
