package de.stefanet.javachesskit;

import java.util.Iterator;

public class PseudoLegalMoveGenerator implements Iterable<Move> {
	private final Board board;

	public PseudoLegalMoveGenerator(Board board) {
		this.board = board;
	}

	@Override
	public Iterator<Move> iterator() {
		return board.generatePseudoLegalMoves().iterator();
	}

	public boolean contains(Object obj) {
		if (!(obj instanceof Move)) return false;
		Move move = (Move) obj;
		return board.isPseudoLegal(move);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Move move : this) {
			if (board.isLegal(move)) {
				sb.append(board.san(move));
			} else {
				sb.append(move.getUciMove());
			}
			sb.append(", ");
		}
		return String.format("<PseudoLegalMoveGenerator (%s)>", sb);
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
