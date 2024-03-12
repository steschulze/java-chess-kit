package de.stefanet.javachesskit.core;

import java.util.*;
import java.util.regex.Pattern;

public class Position {
    private Piece[] board;
    private Color turn;
    private String castling;
    private Character epFile;
    private int halfMoves;
    private int moveNumber;

    public Position() {
        board = new Piece[128];
        turn = Color.WHITE;
        castling = "";
        epFile = null;
        halfMoves = 0;
        moveNumber = 1;
    }

    public Position copy() {
        Position copy = new Position();
        copy.setFen(this.getFen());
        return copy;
    }

    public Piece get(Square square) {
        return board[square.get0x88Index()];
    }

    public void set(Square square, Piece piece) {
        board[square.get0x88Index()] = piece;

        for (char type : new char[]{'K', 'Q', 'k', 'q'}) {
            if (!getTheoreticalCastlingRight(type)) {
                setCastlingRight(type, false);
            }
        }
    }

    public void clear() {
        board = new Piece[128];
        castling = "";
    }

    public void reset() {
        setFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Color getTurn() {
        return turn;
    }

    public void setTurn(Color turn) {
        this.turn = turn;
    }

    public void toggleTurn() {
        turn = turn.other();
    }

    public boolean getCastlingRight(char type) {
        assert "KQkq".indexOf(type) != -1;
        return castling.indexOf(type) != -1;
    }

    public boolean getTheoreticalCastlingRight(char type) {
        assert "KQkq".indexOf(type) != -1;
        if (type == 'K' || type == 'Q') {
            if (get(Square.fromName("e1")) != null && !get(Square.fromName("e1")).equals(new Piece(PieceType.KING, Color.WHITE))) {
                return false;
            }
            if (type == 'K') {
                return get(Square.fromName("h1")) != null && get(Square.fromName("h1")).equals(new Piece(PieceType.ROOK, Color.WHITE));
            } else {
                return get(Square.fromName("a1")) != null && get(Square.fromName("a1")).equals(new Piece(PieceType.ROOK, Color.WHITE));
            }
        } else {
            if (get(Square.fromName("e8")) != null && !get(Square.fromName("e8")).equals(new Piece(PieceType.KING, Color.BLACK))) {
                return false;
            }
            if (type == 'k') {
                return get(Square.fromName("h8")) != null && get(Square.fromName("h8")).equals(new Piece(PieceType.ROOK, Color.BLACK));
            } else {
                return get(Square.fromName("a8")) != null && get(Square.fromName("a8")).equals(new Piece(PieceType.ROOK, Color.BLACK));
            }
        }
    }

    public void setCastlingRight(char type, boolean status) {
        assert "KQkq".indexOf(type) != -1;
        assert !status || getTheoreticalCastlingRight(type);

        StringBuilder sb = new StringBuilder();
        for (char t : new char[]{'K', 'Q', 'k', 'q'}) {
            if (type == t) {
                if (status) {
                    sb.append(t);
                }
            } else if (getCastlingRight(t)) {
                sb.append(t);
            }
        }
        castling = sb.toString();
    }

    public Character getEpFile() {
        return epFile;
    }

    public void setEpFile(Character file) {
        assert file == null || "abcdefgh".indexOf(file) != -1;
        epFile = file;
    }

    public int getHalfMoves() {
        return halfMoves;
    }

    public void setHalfMoves(int halfMoves) {
        assert halfMoves >= 0;
        this.halfMoves = halfMoves;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    public Map<PieceType, Integer> getPieceCounts(String color) {
        assert color.equals("w") || color.equals("b") || color.equals("wb") || color.equals("bw");

        Map<PieceType, Integer> pieceCounts = new HashMap<>();

        for (Piece piece : board) {
            if (piece != null && color.contains(piece.getColor().shortName())) {
                int value = pieceCounts.getOrDefault(piece.getType(), 0);
                value++;
                pieceCounts.put(piece.getType(), value);
            }
        }
        return pieceCounts;
    }

    public Square getKingSquare(Color color) {
        for (Square square : Square.getAll()) {
            Piece piece = board[square.get0x88Index()];
            if (piece != null && piece.getColor() == color && piece.getType() == PieceType.KING) {
                return square;
            }
        }
        return null;
    }

    public String getFen() {
        StringBuilder fen = new StringBuilder();
        int emptyCount = 0;

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                int i = new Square(x, y).get0x88Index();
                Piece piece = board[i];
                if (piece == null) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        fen.append(emptyCount);
                        emptyCount = 0;
                    }
                    fen.append(piece.getSymbol());
                }
            }
            if (emptyCount > 0) {
                fen.append(emptyCount);
                emptyCount = 0;
            }
            if (y > 0) {
                fen.append('/');
            }
        }

        fen.append(' ').append(turn.shortName()).append(' ');

        if (castling.isEmpty()) {
            fen.append('-');
        } else {
            fen.append(castling);
        }

        fen.append(' ');

        if (epFile == null) {
            fen.append('-');
        } else {
            fen.append(epFile).append(turn == Color.WHITE ? '6' : '3');
        }

        fen.append(' ').append(halfMoves).append(' ').append(moveNumber);

        return fen.toString();
    }

    public void setFen(String fen) {
        String[] parts = fen.split(Pattern.quote(" "));
        assert parts.length == 6;

        String[] rows = parts[0].split("/");
        assert rows.length == 8;

        int index = 0x70;
        for (char c: parts[0].toCharArray()) {
            if(c == '/'){
                index -= 24;
            } else if (c >= '1' && c <= '8') {
                index += Character.getNumericValue(c);
            } else {
                this.board [index] = Piece.fromSymbol(c);
                index++;
            }
        }

        assert parts[1].equals("w") || parts[1].equals("b");
        turn = Color.fromSymbol(parts[1].charAt(0));

        assert parts[2].matches("^(K?Q?k?q?|-)$");
        castling = parts[2];

        assert parts[3].matches("^(-|[a-h][36])$");
        epFile = parts[3].equals("-") ? null : parts[3].charAt(0);

        halfMoves = Integer.parseInt(parts[4]);
        moveNumber = Integer.parseInt(parts[5]);

        for (char type : new char[]{'K', 'Q', 'k', 'q'}){
            if (!getTheoreticalCastlingRight(type)) {
                setCastlingRight(type, false);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return getFen().equals(position.getFen());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFen());
    }

    @Override
    public String toString() {
        return String.format("Position.fromFen(%s)", getFen());
    }

    public boolean isKingAttacked(Color color){
        Square square = this.getKingSquare(color);

        if(square == null) return false;

        return this.isAttacked(color.other(), square);
    }

    private boolean isAttacked(Color color, Square square) {
        return !this.getAttackers(color, square).isEmpty();
    }

    private List<Square> getAttackers(Color color, Square square) {
        List<Square> attackingSquares = new ArrayList<>();

        for (Square source : Square.getAll()){
            Piece piece = get(source);
            if(piece == null || piece.getColor() != color) continue;

            int diff = source.get0x88Index() - square.get0x88Index();
            int index = diff + 119;

            if ((Board.ATTACKS[index] & (1 << piece.getType().ordinal())) != 0){
                if(piece.getType() == PieceType.PAWN){
                    if (diff > 0){
                        if (piece.getColor() == Color.WHITE){
                            attackingSquares.add(source);
                        }
                    }else {
                        if (piece.getColor() == Color.BLACK){
                            attackingSquares.add(source);
                        }
                    }
                }

                if(piece.getType() == PieceType.KNIGHT || piece.getType() == PieceType.KING){
                    attackingSquares.add(source);
                }

                int offset = Board.RAYS[index];
                int targetIndex = source.get0x88Index() + offset;
                boolean blocked = false;
                while (targetIndex != square.get0x88Index()){
                    if(this.board[targetIndex] != null){
                        blocked = true;
                        break;
                    }
                    targetIndex += offset;
                }
                if (!blocked){
                    attackingSquares.add(source);
                }
            }
        }
        return attackingSquares;
    }

    public List<Move> getPseudoLegalMoves(){
        List<Move> moves = new ArrayList<>();

        int [] PAWN_OFFSETS = new int[]{16, 32, 17, 15};
        Map<PieceType, int[]> PIECE_OFFSETS = new HashMap<>();
        PIECE_OFFSETS.put(PieceType.KNIGHT, new int[]{-33, -31, -18, -14, 14, 18, 31, 33});
        PIECE_OFFSETS.put(PieceType.BISHOP, new int[]{-17, -15, 15, 17});
        PIECE_OFFSETS.put(PieceType.ROOK, new int[]{-16, -1, 1, 16});
        PIECE_OFFSETS.put(PieceType.QUEEN, new int[]{-17, -16, -15, -1, 1, 15, 16, 17});
        PIECE_OFFSETS.put(PieceType.KING, new int[]{-17, -16, -15, -1, 1, 15, 16, 17});

        for (Square square : Square.getAll()){
            Piece piece = get(square);
            if(piece == null || piece.getColor() != this.getTurn()) continue;

            if(piece.getType() == PieceType.PAWN){
                int index = square.get0x88Index() + PAWN_OFFSETS[0] * getTurn().forwardDirection();
                Square targetSquare = Square.from0x88Index(index);
                if(get(targetSquare) == null){
                    if(targetSquare.isBackrank()){
                        for (PieceType promoteTo : PieceType.promotionTypes()) {
                            moves.add(new Move(square, targetSquare, promoteTo));
                        }
                    }
                    index = square.get0x88Index() + PAWN_OFFSETS[1] * getTurn().forwardDirection();
                    targetSquare = Square.from0x88Index(index);
                    if((this.getTurn() == Color.WHITE && square.getRank() == 2)
                            || (this.getTurn() == Color.BLACK && square.getRank() == 7) && get(targetSquare) == null){
                        moves.add(new Move(square, targetSquare));
                    }
                }
                for (int i = 2; i<PAWN_OFFSETS.length; i++){
                    index = square.get0x88Index() + PAWN_OFFSETS[i] * getTurn().forwardDirection();
                    if((index & 0x88) != 0) continue;
                    targetSquare = Square.from0x88Index(index);
                    Piece capturedPiece = get(targetSquare);
                    if(capturedPiece != null && capturedPiece.getColor() != this.getTurn()){
                        if(targetSquare.isBackrank()){
                            for (PieceType promoteTo : PieceType.promotionTypes()) {
                                moves.add(new Move(square, targetSquare, promoteTo));
                            }
                        } else {
                            moves.add(new Move(square, targetSquare));
                        }
                    } else if ((capturedPiece == null) && epFile != null && (targetSquare.getFile() == epFile.charValue())) {
                        moves.add(new Move(square, targetSquare));
                    }
                }
            } else {
                for(int offset: PIECE_OFFSETS.get(piece.getType())){
                    int index = square.get0x88Index();
                    for (;;){
                        index += offset;
                        if((index & 0x88) != 0) break;
                        Square targetSquare = Square.from0x88Index(index);
                        Piece targetPiece = get(targetSquare);
                        if(targetPiece == null){
                            moves.add(new Move(square, targetSquare));
                        } else{
                            if(targetPiece.getColor() == this.getTurn())break;
                            moves.add(new Move(square, targetSquare));
                            break;
                        }

                        if(piece.getType() == PieceType.KNIGHT || piece.getType() == PieceType.KING) break;
                    }
                }
            }
        }

        Color opponent = getTurn().other();
        char kingSideCastling = this.getTurn() == Color.WHITE? 'K' : 'k';
        if(getCastlingRight(kingSideCastling)){
            int kingIndex = this.getKingSquare(this.getTurn()).get0x88Index();
            int targetIndex = kingIndex + 2;
            if(board[kingIndex + 1] == null && board[targetIndex] == null && !this.isCheck()
                    && this.isAttacked(opponent, Square.from0x88Index(kingIndex +1))
                    && this.isAttacked(opponent, Square.from0x88Index(targetIndex))){
                moves.add(new Move(Square.from0x88Index(kingIndex), Square.from0x88Index(targetIndex)));
            }
        }

        char queenSideCastling = this.getTurn() == Color.WHITE? 'Q' : 'q';
        if(getCastlingRight(queenSideCastling)){
            int kingIndex = this.getKingSquare(this.getTurn()).get0x88Index();
            int targetIndex = kingIndex - 2;
            if(board[kingIndex - 1] == null && board[kingIndex - 2] == null
                    && board[kingIndex - 3] == null && !this.isCheck()
                    && this.isAttacked(opponent, Square.from0x88Index(kingIndex -1))
                    && this.isAttacked(opponent, Square.from0x88Index(targetIndex))){
                moves.add(new Move(Square.from0x88Index(kingIndex), Square.from0x88Index(targetIndex)));
            }
        }

        return moves;
    }

    public List<Move> getLegalMoves(){
        List<Move> moves = new ArrayList<>();
        for(Move move : getPseudoLegalMoves()){
            Position copiedPosition = this.copy();
            copiedPosition.makeMove(move);
            if(! copiedPosition.isKingAttacked(this.getTurn())){
                moves.add(move);
            }
        }
        return moves;
    }

    public void makeMove(Move move) {
        Piece movingPiece = this.get(move.getSource());
        boolean hasCaptured = this.get(move.getTarget()) != null;
        this.set(move.getTarget(), movingPiece);
        this.set(move.getSource(), null);
        this.epFile = null;

        this.toggleTurn();

        if(movingPiece.getType() == PieceType.PAWN){
            if(move.getTarget().getFile() != move.getSource().getFile() && !hasCaptured){
                if(this.getTurn() == Color.BLACK){
                    this.board[move.getTarget().get0x88Index() + 16] = null;
                } else {
                    this.board[move.getTarget().get0x88Index() - 16] = null;
                }
                hasCaptured = true;
            }

            if(Math.abs(move.getTarget().getRank() - move.getSource().getRank()) == 2){
                this.epFile = move.getTarget().getFile();
            }

            if(move.getPromotion() != null) {
                this.set(move.getTarget(), new Piece(move.getPromotion(), movingPiece.getColor()));
            }
        } else if (movingPiece.getType() == PieceType.KING) {
            int steps = move.getTarget().getX() - move.getSource().getX();

            if(Math.abs(steps) == 2){
                int rookTargetIndex;
                int rookSourceIndex;
                if(steps == 2){
                    rookTargetIndex = move.getTarget().get0x88Index() - 1;
                    rookSourceIndex = move.getTarget().get0x88Index() + 1;
                } else {
                    rookTargetIndex = move.getTarget().get0x88Index() + 1;
                    rookSourceIndex = move.getTarget().get0x88Index() - 2;
                }
                this.board[rookTargetIndex] = this.board[rookSourceIndex];
                this.board[rookSourceIndex] = null;
            }
        }

        if(movingPiece.getType() == PieceType.PAWN || hasCaptured){
            this.halfMoves = 0;
        } else{
            this.halfMoves++;
        }

        if ((this.getTurn() == Color.WHITE)){
            this.moveNumber++;
        }
    }

    public boolean isCheck() {
        return this.isKingAttacked(this.getTurn());
    }

    public boolean isCheckmate() {
        if(!isCheck()) return false;
        return getLegalMoves().isEmpty();
    }

    public boolean isStalemate() {
        if(isCheck()) return false;
        return getLegalMoves().isEmpty();
    }

    public boolean isInsufficientMaterial() {
        Map<PieceType, Integer> pieceCounts = getPieceCounts("wb");
        int sum = pieceCounts.values().stream().mapToInt(Integer::intValue).sum();
        if(sum == 2){
            return true;
        } else if (sum == 3) {
            return pieceCounts.get(PieceType.BISHOP) == 1 || pieceCounts.get(PieceType.KNIGHT) == 1;
        } else if (sum == 2 + pieceCounts.get(PieceType.BISHOP)) {
            boolean whiteHasBishop = this.getPieceCounts("w").get(PieceType.BISHOP) != 0;
            boolean blackHasBishop = this.getPieceCounts("b").get(PieceType.BISHOP) != 0;

            if(whiteHasBishop && blackHasBishop) {
                Boolean color = null;

                for(Square square : Square.getAll()){
                    Piece piece = this.get(square);
                    if(piece != null && piece.getType() == PieceType.BISHOP){
                        if(color != null && color != square.isLight()) return false;
                        color = square.isLight();
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return this.isCheckmate() || this.isStalemate() || this.isInsufficientMaterial();
    }

    public static Position getDefault() {
        Position defaultPosition = new Position();
        defaultPosition.reset();
        return defaultPosition;
    }

    public static Position fromFen(String fen) {
        Position position = new Position();
        position.setFen(fen);
        return position;
    }

    public MoveInfo getMoveInfo(Move move) {
        assert getLegalMoves().contains(move);

        Position copiedPosition = this.copy();
        copiedPosition.makeMove(move);

        Piece movedPiece = get(move.getSource());
        Piece capturedPiece = get(move.getTarget());

        boolean enPassant = false;

        if(movedPiece.getType() == PieceType.PAWN){
            if(move.getTarget().getFile() != move.getSource().getFile() && capturedPiece == null){
                enPassant = true;
                capturedPiece = new Piece(PieceType.PAWN, copiedPosition.getTurn());
            }
        }

        boolean isKingSideCastling = movedPiece.getType() == PieceType.KING
                && (move.getTarget().getX() - move.getSource().getX() == 2);
        boolean isQueenSideCastling = movedPiece.getType() == PieceType.KING
                && (move.getTarget().getX() - move.getSource().getX() == -2);

        boolean isCheck = copiedPosition.isCheck();
        boolean isCheckmate = copiedPosition.isCheckmate();

        StringBuilder san = new StringBuilder();
        if (isKingSideCastling){
            san.append("O-O");
        } else if (isQueenSideCastling) {
            san.append("O-O-O");
        } else {
            if (movedPiece.getType() != PieceType.PAWN){
                san.append(Character.toUpperCase(movedPiece.getType().getSymbol()));
            }

            san.append(getDisambiguatedMove(move));

            if (capturedPiece != null){
                if(movedPiece.getType() == PieceType.PAWN){
                    san.append(move.getSource().getFile());
                }
                san.append("x");
            }

            san.append(move.getTarget().getName());

            if (move.getPromotion() != null) {
                san.append("=").append(Character.toUpperCase(move.getPromotion().getSymbol()));
            }
        }

        if (isCheck) {
            san.append("+");
        } else if (isCheckmate) {
            san.append("#");
        }

        if (enPassant){
            san.append(" (e.p.)");
        }

        return new MoveInfo(move, movedPiece,capturedPiece, san.toString(),
                enPassant, isKingSideCastling, isQueenSideCastling, isCheck, isCheckmate);
    }

    private String getDisambiguatedMove(Move move){
        boolean sameFile = false;
        boolean sameRank = false;
        Piece piece = get(move.getSource());

        for(Move otherMove: getLegalMoves()){
            Piece otherPiece = get(otherMove.getSource());
            if(piece == otherPiece && move.getSource() != otherMove.getSource()
                    && move.getTarget() == otherMove.getTarget()){
                sameFile = move.getSource().getFile() == otherMove.getSource().getFile();
                sameRank = move.getSource().getRank() == otherMove.getSource().getRank();

                if (sameFile && sameRank) break;
            }
        }

        if (sameFile && sameRank) {
            return move.getSource().getName();
        } else if (sameFile) {
            return String.valueOf(move.getSource().getRank());
        } else if (sameRank) {
            return String.valueOf(move.getSource().getFile());
        } else {
            return "";
        }
    }

}
