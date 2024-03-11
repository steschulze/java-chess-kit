package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
                pieceCounts.put(piece.getType(), value);
            }
        }
        return pieceCounts;
    }

    public Piece getKing(Color color) {
        for (Piece piece : board) {
            if (piece != null && piece.getColor() == color && piece.getType() == PieceType.KING) {
                return piece;
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

}
