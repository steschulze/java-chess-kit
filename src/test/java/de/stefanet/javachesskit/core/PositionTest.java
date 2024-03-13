package de.stefanet.javachesskit.core;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PositionTest {

    @Test
    void testGetFEN_defaultPosition() {
        Position position = Position.getDefault();
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", position.getFen());
    }

    @Test
    void testDefaultPosition() {
        Position position = Position.getDefault();
        Piece piece1 = position.get(Square.fromName("a1"));
        Piece piece2 = position.get(Square.fromName("b8"));
        Piece piece3 = position.get(Square.fromName("f1"));
        Piece piece4 = position.get(Square.fromName("d8"));
        Piece piece5 = position.get(Square.fromName("e1"));

        assertEquals(PieceType.ROOK, piece1.getType());
        assertEquals(Color.WHITE, piece1.getColor());

        assertEquals(PieceType.KNIGHT, piece2.getType());
        assertEquals(Color.BLACK, piece2.getColor());

        assertEquals(PieceType.BISHOP, piece3.getType());
        assertEquals(Color.WHITE, piece3.getColor());

        assertEquals(PieceType.QUEEN, piece4.getType());
        assertEquals(Color.BLACK, piece4.getColor());

        assertEquals(PieceType.KING, piece5.getType());
        assertEquals(Color.WHITE, piece5.getColor());
    }

    @Test
    void testSetPiece() {
        Position position = new Position();
        position.clear();

        position.set(Square.fromName("e4"), Piece.fromSymbol('P'));
        position.set(Square.fromName("d5"), Piece.fromSymbol('n'));

        String expectedFEN = "8/8/8/3n4/4P3/8/8/8 w - - 0 1";

        assertEquals(expectedFEN, position.getFen());
    }

    @Test
    void testGetTurn_whiteTurn() {
        Position position = Position.getDefault();

        assertEquals(position.getTurn(), Color.WHITE);
    }

    @Test
    void testGetTurn_blackTurn() {
        Position position = Position.fromFen("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq - 0 1");

        assertEquals(position.getTurn(), Color.BLACK);
    }

    @Test
    void testToggleTurn() {
        Position position = Position.getDefault();

        assertEquals(position.getTurn(), Color.WHITE);
        position.toggleTurn();
        assertEquals(position.getTurn(), Color.BLACK);
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1", position.getFen());
        position.setTurn(Color.WHITE);
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", position.getFen());
    }

    @Test
    void testCastlingRight_defaultPosition() {
        Position position = Position.getDefault();
        assertTrue(position.getCastlingRight('K'));
        assertTrue(position.getCastlingRight('Q'));
        assertTrue(position.getCastlingRight('k'));
        assertTrue(position.getCastlingRight('q'));
    }

    @Test
    void testCastlingRight_wrongPieceType() {
        Position position = Position.getDefault();
        assertThrows(AssertionError.class, () -> position.getCastlingRight('r'));
        assertThrows(AssertionError.class, () -> position.setCastlingRight('r', false));
    }

    @Test
    void testCastlingRight_emptyPosition() {
        Position position = Position.getDefault();
        position.clear();
        assertThrows(AssertionError.class, () -> position.setCastlingRight('K', true));
        assertThrows(AssertionError.class, () -> position.setCastlingRight('Q', true));
        assertThrows(AssertionError.class, () -> position.setCastlingRight('k', true));
        assertThrows(AssertionError.class, () -> position.setCastlingRight('q', true));
    }

    @Test
    void testTheoreticalCastlingRight_fromFen() {
        Position position = Position.fromFen("n3k2b/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        assertTrue(position.getTheoreticalCastlingRight('K'));
        assertTrue(position.getTheoreticalCastlingRight('Q'));
        assertFalse(position.getTheoreticalCastlingRight('k'));
        assertFalse(position.getTheoreticalCastlingRight('q'));
    }

    @Test
    void testTheoreticalCastlingRight_setupPosition() {
        Position position = new Position();
        position.clear();
        position.set(Square.fromName("e1"), Piece.fromSymbol('K'));
        position.set(Square.fromName("h1"), Piece.fromSymbol('R'));
        position.set(Square.fromName("a1"), Piece.fromSymbol('B'));
        position.set(Square.fromName("e8"), Piece.fromSymbol('k'));
        position.set(Square.fromName("a8"), Piece.fromSymbol('r'));

        assertTrue(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertFalse(position.getTheoreticalCastlingRight('k'));
        assertTrue(position.getTheoreticalCastlingRight('q'));
    }

    @Test
    void testTheoreticalCastlingRight_wrongPieceType() {
        Position position = Position.getDefault();
        assertThrows(AssertionError.class, () -> position.getTheoreticalCastlingRight('b'));
    }

    @Test
    void testTheoreticalCastlingRight_emptyKingSquare() {
        Position position = Position.getDefault();
        position.set(Square.fromName("e1"), null);

        assertFalse(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertTrue(position.getTheoreticalCastlingRight('k'));
        assertTrue(position.getTheoreticalCastlingRight('q'));

        position.set(Square.fromName("e8"), null);
        assertFalse(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertFalse(position.getTheoreticalCastlingRight('k'));
        assertFalse(position.getTheoreticalCastlingRight('q'));
    }

    @Test
    void testTheoreticalCastlingRight_otherPieceOnKingSquare() {
        Position position = Position.getDefault();
        position.set(Square.fromName("e1"), Piece.fromSymbol('Q'));

        assertFalse(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertTrue(position.getTheoreticalCastlingRight('k'));
        assertTrue(position.getTheoreticalCastlingRight('q'));

        position.set(Square.fromName("e8"), Piece.fromSymbol('r'));

        assertFalse(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertFalse(position.getTheoreticalCastlingRight('k'));
        assertFalse(position.getTheoreticalCastlingRight('q'));
    }

    @Test
    void testTheoreticalCastlingRight_emptyRookSquare() {
        Position position = Position.getDefault();
        position.set(Square.fromName("a1"), null);

        assertTrue(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertTrue(position.getTheoreticalCastlingRight('k'));
        assertTrue(position.getTheoreticalCastlingRight('q'));

        position.set(Square.fromName("h1"), null);

        assertFalse(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertTrue(position.getTheoreticalCastlingRight('k'));
        assertTrue(position.getTheoreticalCastlingRight('q'));

        position.set(Square.fromName("a8"), null);

        assertFalse(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertTrue(position.getTheoreticalCastlingRight('k'));
        assertFalse(position.getTheoreticalCastlingRight('q'));

        position.set(Square.fromName("h8"), null);

        assertFalse(position.getTheoreticalCastlingRight('K'));
        assertFalse(position.getTheoreticalCastlingRight('Q'));
        assertFalse(position.getTheoreticalCastlingRight('k'));
        assertFalse(position.getTheoreticalCastlingRight('q'));
    }

    @Test
    void testSimpleProperties() {
        Position position = Position.getDefault();

        assertNull(position.getEpFile());
        assertEquals(0, position.getHalfMoves());
        assertEquals(1, position.getMoveNumber());

        assertThrows(AssertionError.class, () -> position.setEpFile(null));
        assertThrows(AssertionError.class, () -> position.setEpFile('k'));
        assertThrows(AssertionError.class, () -> position.setHalfMoves(-1));

        position.setEpFile('f');
        position.setHalfMoves(3);
        position.setMoveNumber(5);

        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq f6 3 5", position.getFen());
    }

    @Test
    void testGetKingSquare_emptyPosition() {
        Position position = Position.getDefault();
        position.clear();

        assertNull(position.getKingSquare(Color.WHITE));
        assertNull(position.getKingSquare(Color.BLACK));
    }

    @Test
    void testPieceCounts_defaultPosition() {
        Position position = Position.getDefault();
        assertThrows(AssertionError.class, () -> position.getPieceCounts("WB"));
        Map<PieceType, Integer> allPieces = position.getPieceCounts("wb");

        assertEquals(16, allPieces.get(PieceType.PAWN));
        assertEquals(4, allPieces.get(PieceType.ROOK));
        assertEquals(4, allPieces.get(PieceType.KNIGHT));
        assertEquals(4, allPieces.get(PieceType.BISHOP));
        assertEquals(2, allPieces.get(PieceType.QUEEN));
        assertEquals(2, allPieces.get(PieceType.KING));
    }

    @Test
    void testPieceCounts_onlyBlackPieces() {
        Position position = Position.fromFen("rnbqkbnr/pppppppp/8/8/8/8/8/8 w KQkq - 0 1");

        Map<PieceType, Integer> whitePieces = position.getPieceCounts("w");
        int whitePieceCount = whitePieces.values().stream().mapToInt(Integer::intValue).sum();

        assertEquals(0, whitePieceCount);
    }

    @Test
    void testEquals() {
        Position position1 = Position.getDefault();
        position1.set(Square.fromName("a1"), null);

        Position position2 = Position.fromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/1NBQKBNR w KQkq - 0 1");

        assertEquals(position1, position1);
        assertNotEquals(null, position1);
        assertEquals(position1, position2);
        assertEquals(position1.hashCode(), position2.hashCode());

        position2.makeMove(Move.fromUCI("e2e4"));
        assertNotEquals(position1, position2);
        assertNotEquals(position1.hashCode(), position2.hashCode());
    }

    @Test
    void testToString() {
        Position position = Position.fromFen("8/8/8/4k3/8/3K4/8/8 w - - 0 60");
        assertEquals("Position.fromFen(8/8/8/4k3/8/3K4/8/8 w - - 0 60)", position.toString());
    }

    @Test
    void testInsufficientMaterial_onlyKings() {
        Position position = Position.getDefault();
        position.clear();

        position.set(Square.fromName("c3"), Piece.fromSymbol('K'));
        position.set(Square.fromName("e6"), Piece.fromSymbol('k'));

        assertTrue(position.isInsufficientMaterial());
    }

    @Test
    void testInsufficientMaterial_kingAndKnightVersusKing() {
        Position position = Position.getDefault();
        position.clear();

        position.set(Square.fromName("c3"), Piece.fromSymbol('K'));
        position.set(Square.fromName("d3"), Piece.fromSymbol('N'));
        position.set(Square.fromName("e6"), Piece.fromSymbol('k'));

        assertTrue(position.isInsufficientMaterial());
    }

    @Test
    void testInsufficientMaterial_kingAndBishopVersusKingAndBishop() {
        Position position = Position.getDefault();
        position.clear();

        position.set(Square.fromName("e4"), Piece.fromSymbol('K'));
        position.set(Square.fromName("c1"), Piece.fromSymbol('B'));
        position.set(Square.fromName("e6"), Piece.fromSymbol('k'));
        position.set(Square.fromName("d8"), Piece.fromSymbol('b'));

        assertTrue(position.isInsufficientMaterial());
    }

    @Test
    void testLegalMoves_emptyPosition() {
        Position position = Position.getDefault();
        position.clear();

        assertTrue(position.getLegalMoves().isEmpty());
    }

    @Test
    void testLegalMoves_whiteKingAndPawnEndgame_blacksTurn() {
        Position position = Position.fromFen("8/8/8/3k4/8/3PK3/8/8 b - - 0 1");

        List<Move> legalMoves = position.getLegalMoves();
        assertEquals(5, legalMoves.size());
        assertTrue(legalMoves.contains(Move.fromUCI("d5c5")));
        assertTrue(legalMoves.contains(Move.fromUCI("d5e5")));
        assertTrue(legalMoves.contains(Move.fromUCI("d5c6")));
        assertTrue(legalMoves.contains(Move.fromUCI("d5d6")));
        assertTrue(legalMoves.contains(Move.fromUCI("d5e6")));
    }

    @Test
    void testLegalMoves_whiteKingAndPawnEndgame_blackInCheck() {
        Position position = Position.fromFen("8/8/8/8/2k5/3PK3/8/8 b - - 0 1");

        List<Move> legalMoves = position.getLegalMoves();
        assertEquals(6, legalMoves.size());
        assertTrue(legalMoves.contains(Move.fromUCI("c4c3")));
        assertTrue(legalMoves.contains(Move.fromUCI("c4b3")));
        assertTrue(legalMoves.contains(Move.fromUCI("c4b4")));
        assertTrue(legalMoves.contains(Move.fromUCI("c4b5")));
        assertTrue(legalMoves.contains(Move.fromUCI("c4c5")));
        assertTrue(legalMoves.contains(Move.fromUCI("c4d5")));
    }

    @Test
    void testLegalMoves_blackKingAndPawnEndgame() {
        Position position = Position.fromFen("8/8/8/2kp4/8/3K4/8/8 w - - 0 1");
        List<Move> legalMoves = position.getLegalMoves();

        assertEquals(5, legalMoves.size());

        assertTrue(legalMoves.contains(Move.fromUCI("d3c3")));
        assertTrue(legalMoves.contains(Move.fromUCI("d3e3")));
        assertTrue(legalMoves.contains(Move.fromUCI("d3c2")));
        assertTrue(legalMoves.contains(Move.fromUCI("d3d2")));
        assertTrue(legalMoves.contains(Move.fromUCI("d3e2")));
    }

    @Test
    void testLegalMoves_whitePawnPromotion() {
        Position position = Position.fromFen("7K/4Pk1P/8/8/8/8/8/8 w - - 0 1");
        List<Move> legalMoves = position.getLegalMoves();

        assertEquals(4, legalMoves.size());

        assertTrue(legalMoves.contains(Move.fromUCI("e7e8r")));
        assertTrue(legalMoves.contains(Move.fromUCI("e7e8n")));
        assertTrue(legalMoves.contains(Move.fromUCI("e7e8b")));
        assertTrue(legalMoves.contains(Move.fromUCI("e7e8q")));
    }

    @Test
    void testLegalMoves_whitePawnCapturePromotion() {
        Position position = Position.fromFen("4bb1K/4Pk1P/8/8/8/8/8/8 w - - 0 1");
        List<Move> legalMoves = position.getLegalMoves();

        assertEquals(4, legalMoves.size());

        assertTrue(legalMoves.contains(Move.fromUCI("e7f8r")));
        assertTrue(legalMoves.contains(Move.fromUCI("e7f8n")));
        assertTrue(legalMoves.contains(Move.fromUCI("e7f8b")));
        assertTrue(legalMoves.contains(Move.fromUCI("e7f8q")));
    }

    @Test
    void testLegalMoves_whitePawnCapture() {
        Position position = Position.fromFen("5k1K/7P/2n1n3/3P4/8/8/8/8 w - - 0 1");
        List<Move> legalMoves = position.getLegalMoves();

        assertEquals(3, legalMoves.size());

        assertTrue(legalMoves.contains(Move.fromUCI("d5d6")));
        assertTrue(legalMoves.contains(Move.fromUCI("d5c6")));
        assertTrue(legalMoves.contains(Move.fromUCI("d5e6")));
    }

    @Test
    void testLegalMoves_whiteCastling() {
        Position position = Position.getDefault();
        position.clear();

        position.set(Square.fromName("e1"), Piece.fromSymbol('K'));
        position.set(Square.fromName("e8"), Piece.fromSymbol('k'));

        List<Move> legalMoves = position.getLegalMoves();

        assertFalse(legalMoves.contains(Move.fromUCI("e1g1")));
        assertFalse(legalMoves.contains(Move.fromUCI("e1c1")));

        position.set(Square.fromName("h1"), Piece.fromSymbol('R'));
        position.setCastlingRight('K', true);

        legalMoves = position.getLegalMoves();

        assertTrue(legalMoves.contains(Move.fromUCI("e1g1")));
        assertFalse(legalMoves.contains(Move.fromUCI("e1c1")));

        position.set(Square.fromName("a1"), Piece.fromSymbol('R'));
        position.setCastlingRight('Q', true);

        legalMoves = position.getLegalMoves();

        assertTrue(legalMoves.contains(Move.fromUCI("e1g1")));
        assertTrue(legalMoves.contains(Move.fromUCI("e1c1")));
    }

    @Test
    void testScholarsMate() {
        Position position = Position.getDefault();

        Move e4 = Move.fromUCI("e2e4");
        assertTrue(position.getLegalMoves().contains(e4));
        position.makeMove(e4);

        Move e5 = Move.fromUCI("e7e5");
        assertTrue(position.getLegalMoves().contains(e5));
        assertFalse(position.getLegalMoves().contains(e4));
        position.makeMove(e5);

        Move qf3 = Move.fromUCI("d1f3");
        assertTrue(position.getLegalMoves().contains(qf3));
        position.makeMove(qf3);

        Move nc6 = Move.fromUCI("b8c6");
        assertTrue(position.getLegalMoves().contains(nc6));
        position.makeMove(nc6);

        Move lc4 = Move.fromUCI("f1c4");
        assertTrue(position.getLegalMoves().contains(lc4));
        position.makeMove(lc4);

        Move rb8 = Move.fromUCI("a8b8");
        assertTrue(position.getLegalMoves().contains(rb8));
        position.makeMove(rb8);

        assertFalse(position.isCheck());
        assertFalse(position.isCheckmate());
        assertFalse(position.isGameOver());
        assertFalse(position.isStalemate());

        Move qf7 = Move.fromUCI("f3f7");
        assertTrue(position.getLegalMoves().contains(qf7));
        position.makeMove(qf7);

        assertTrue(position.isCheck());
        assertTrue(position.isCheckmate());
        assertTrue(position.isGameOver());
        assertFalse(position.isStalemate());

        assertEquals("1rbqkbnr/pppp1Qpp/2n5/4p3/2B1P3/8/PPPP1PPP/RNB1K1NR b KQk - 0 4", position.getFen());
    }

    @Test
    void testMoveInfo() {
        Position position = Position.getDefault();
        MoveInfo moveInfo = position.getMoveInfo(Move.fromUCI("d2d4"));

        assertEquals("d4", moveInfo.getSan());
        assertEquals(Move.fromUCI("d2d4"), moveInfo.getMove());
        assertEquals(PieceType.PAWN, moveInfo.getMovedPiece().getType());
        assertNull(moveInfo.getCapturedPiece());
        assertFalse(moveInfo.isEnpassant());
        assertFalse(moveInfo.isCheck());
        assertFalse(moveInfo.isCheckmate());
        assertFalse(moveInfo.isCastle());
        assertFalse(moveInfo.isKingSideCastle());
        assertFalse(moveInfo.isQueenSideCastle());
    }

    @Test
    void testMoveInfo_enPassantMove() {
        Position position = Position.fromFen("8/k7/8/5Pp1/8/8/3K4/8 w - g6 0 1");
        Move enPassant = Move.fromUCI("f5g6");

        MoveInfo moveInfo = position.getMoveInfo(enPassant);

        assertTrue(moveInfo.isEnpassant());
        assertEquals(PieceType.PAWN, moveInfo.getCapturedPiece().getType());
    }

    @Test
    void testMoveInfo_kingSideCastling() {
        Position position = Position.fromFen("8/8/8/8/3k4/8/8/4K2R w K - 0 1");
        Move castling = Move.fromUCI("e1g1");

        MoveInfo moveInfo = position.getMoveInfo(castling);

        assertTrue(moveInfo.isKingSideCastle());
        assertFalse(moveInfo.isCheck());
        assertFalse(moveInfo.isCheckmate());
    }

    @Test
    void testMoveInfo_queenSideCastlingWithCheck() {
        Position position = Position.fromFen("r3k3/8/8/3K4/8/8/8/8 b q - 0 1");
        Move castling = Move.fromUCI("e8c8");

        MoveInfo moveInfo = position.getMoveInfo(castling);

        assertTrue(moveInfo.isQueenSideCastle());
        assertTrue(moveInfo.isCheck());
        assertFalse(moveInfo.isCheckmate());
    }

    @Test
    void testMoveInfo_promotionWithMate() {
        Position position = Position.fromFen("7k/5P2/6K1/8/8/8/8/8 w - - 0 1");
        Move move = Move.fromUCI("f7f8r");

        MoveInfo moveInfo = position.getMoveInfo(move);

        assertTrue(moveInfo.isCheckmate());
        assertEquals("f8=R#", moveInfo.getSan());
    }

    @Test
    void testMoveInfo_ambiguousMove_sameFile() {
        Position position = Position.fromFen("8/8/7k/2R5/8/8/2R5/6K1 w - - 0 1");
        Move move = Move.fromUCI("c2c4");

        MoveInfo moveInfo = position.getMoveInfo(move);

        assertEquals("R2c4", moveInfo.getSan());
    }

    @Test
    void testMoveInfo_ambiguousMove_sameRank() {
        Position position = Position.fromFen("8/8/8/6k1/8/8/8/R4RK1 w - - 0 1");
        Move move = Move.fromUCI("f1c1");

        MoveInfo moveInfo = position.getMoveInfo(move);

        assertEquals("Rfc1", moveInfo.getSan());
    }

    @Test
    void testMoveInfo_ambiguousMove_sameRankAndFile() {
        Position position = Position.fromFen("8/1Q6/8/6k1/8/8/8/1Q4KQ w - - 0 1");
        Move move = Move.fromUCI("b1e4");

        MoveInfo moveInfo = position.getMoveInfo(move);

        assertEquals("Qb1e4", moveInfo.getSan());
    }

    @Test
    void testMoveInfoToString() {
        Position position = Position.getDefault();
        MoveInfo moveInfo = position.getMoveInfo(Move.fromUCI("g1f3"));

        assertEquals("MoveInfo{move=Move.fromUCI(g1f3), " +
                "movedPiece=Piece.parseSymbol('N'), " +
                "capturedPiece=null, " +
                "san='Nf3', " +
                "isEnpassant=false, " +
                "isKingSideCastle=false, " +
                "isQueenSideCastle=false, " +
                "isCheck=false, " +
                "isCheckmate=false}", moveInfo.toString());
    }

    @Test
    void testParseSan() {
        Position position = Position.getDefault();

        position.makeMove(position.parseSan("Nc3"));
        position.makeMove(position.parseSan("c5"));

        position.makeMove(position.parseSan("e4"));
        position.makeMove(position.parseSan("g6"));

        position.makeMove(position.parseSan("Nge2"));
        position.makeMove(position.parseSan("Bg7"));

        position.makeMove(position.parseSan("d3"));
        position.makeMove(position.parseSan("Bxc3"));

        position.makeMove(position.parseSan("bxc3"));

        assertEquals("rnbqk1nr/pp1ppp1p/6p1/2p5/4P3/2PP4/P1P1NPPP/R1BQKB1R b KQkq - 0 5", position.getFen());
    }
}