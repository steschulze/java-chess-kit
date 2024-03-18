package de.stefanet.javachesskit;

import de.stefanet.javachesskit.board0x88.Color;
import de.stefanet.javachesskit.board0x88.Piece;
import de.stefanet.javachesskit.board0x88.PieceType;
import de.stefanet.javachesskit.board0x88.Square;

import java.util.Map;

public interface Board {
	Board copy();

	Piece get(Square square);

	void set(Square square, Piece piece);

	void clear();

	void reset();

	Color getTurn();

	void setTurn(Color turn);

	void toggleTurn();

	boolean getCastlingRight(char type);

	boolean getTheoreticalCastlingRight(char type);

	void setCastlingRight(char type, boolean status);

	Character getEpFile();

	void setEpFile(Character file);

	int getHalfMoves();

	void setHalfMoves(int halfMoves);

	int getMoveNumber();

	void setMoveNumber(int moveNumber);

	Map<PieceType, Integer> getPieceCounts(String color);

	Square getKingSquare(Color color);

	String getFen();

	void setFen(String fen);
}
