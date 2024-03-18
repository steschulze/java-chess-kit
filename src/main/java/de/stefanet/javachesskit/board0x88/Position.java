package de.stefanet.javachesskit.board0x88;

import de.stefanet.javachesskit.Board;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a chess position.
 */
public class Position implements Board {
    private Piece[] board;
    private Color turn;
    private String castling;
    private Character epFile;
    private int halfMoves;
    private int moveNumber;

    /**
     * Constructs a new Position with the default piece setup.
     */
    public Position() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    /**
     * Constructs a new Position with the given FEN string.
     *
     * @param fen The FEN string representing the position.
     */
    public Position(String fen) {
        this.castling = "KQkq";
        setFen(fen);
    }

    /**
     * Creates a deep copy of this Position object.
     *
     * @return A new Position object that is a copy of this position.
     */
    @Override
    public Position copy() {
        Position copy = new Position();
        copy.setFen(this.getFen());
        return copy;
    }

    /**
     * Returns the piece at the given square.
     *
     * @param square The square to get the piece from.
     * @return The piece at the specified square or null if the square is empty.
     */
    @Override
    public Piece get(Square square) {
        return board[square.get0x88Index()];
    }

    /**
     * Sets the piece at the given square.
     *
     * @param square The square to set the piece at.
     * @param piece  The piece to set, if null the square is cleared.
     */
    @Override
    public void set(Square square, Piece piece) {
        board[square.get0x88Index()] = piece;

        for (char type : new char[]{'K', 'Q', 'k', 'q'}) {
            if (!getTheoreticalCastlingRight(type)) {
                setCastlingRight(type, false);
            }
        }
    }

    /**
     * Removes all pieces from the board and clears the castling rights.
     */
    @Override
    public void clear() {
        board = new Piece[128];
        castling = "";
    }

    /**
     * Resets the position to the default position.
     */
    @Override
    public void reset() {
        setFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    /**
     * Gets the color of the player whose turn it is.
     *
     * @return The color representing the player whose turn it is (WHITE or BLACK).
     */
    @Override
    public Color getTurn() {
        return turn;
    }

    /**
     * Sets the color of the player whose turn it is.
     *
     * @param turn The color representing the player whose turn it is (WHITE or BLACK).
     */
    @Override
    public void setTurn(Color turn) {
        this.turn = turn;
    }

    /**
     * Toggles the turn between WHITE and BLACK.
     * For example, if the current turn is WHITE, it changes it to BLACK, and vice versa.
     */
    @Override
    public void toggleTurn() {
        turn = turn.other();
    }

    /**
     * Checks if a specific castling right is currently available.
     *
     * @param type The type of castling right to check (K, Q, k, or q).
     * @return True if the specified castling right is available, false otherwise.
     * @throws IllegalArgumentException If the type of castling right is invalid
     */
    @Override
    public boolean getCastlingRight(char type) {
        if ("KQkq".indexOf(type) == -1) throw new IllegalArgumentException("Invalid type of castling right: " + type);
        return castling.indexOf(type) != -1;
    }

    /**
     * Checks if a specific theoretical castling right is valid based on the current board position.
     *
     * @param type The type of castling right to check (K, Q, k, or q).
     * @return True if the specified castling right is theoretically valid, false otherwise.
     * @throws IllegalArgumentException If the type of castling right is invalid
     */
    @Override
    public boolean getTheoreticalCastlingRight(char type) {
        if ("KQkq".indexOf(type) == -1) throw new IllegalArgumentException("Invalid type of castling right: " + type);
        if (type == 'K' || type == 'Q') {
            if (get(Square.fromName("e1")) == null || !get(Square.fromName("e1")).equals(Piece.fromTypeAndColor(PieceType.KING, Color.WHITE))) {
                return false;
            }
            if (type == 'K') {
                return get(Square.fromName("h1")) != null && get(Square.fromName("h1")).equals(Piece.fromTypeAndColor(PieceType.ROOK, Color.WHITE));
            } else {
                return get(Square.fromName("a1")) != null && get(Square.fromName("a1")).equals(Piece.fromTypeAndColor(PieceType.ROOK, Color.WHITE));
            }
        } else {
            if (get(Square.fromName("e8")) == null || !get(Square.fromName("e8")).equals(Piece.fromTypeAndColor(PieceType.KING, Color.BLACK))) {
                return false;
            }
            if (type == 'k') {
                return get(Square.fromName("h8")) != null && get(Square.fromName("h8")).equals(Piece.fromTypeAndColor(PieceType.ROOK, Color.BLACK));
            } else {
                return get(Square.fromName("a8")) != null && get(Square.fromName("a8")).equals(Piece.fromTypeAndColor(PieceType.ROOK, Color.BLACK));
            }
        }
    }

    /**
     * Checks if en passant capture is theoretical possible on the specified file.
     *
     * @param file The file where en passant capture is to be checked.
     * @return {@code true} if en passant capture is possible on the specified file, {@code false} otherwise.
     * @throws IllegalArgumentException if the specified file is invalid.
     */
    public boolean checkEnPassant(char file) {
        if ("abcdefgh".indexOf(file) == -1) throw new IllegalArgumentException("Invalid file " + file);
        int rank = getTurn() == Color.WHITE ? 5 : 4;
        int captureRank = getTurn() == Color.WHITE ? 6 : 3;

        Square pawnSquare = Square.fromRankAndFile(rank, file);
        Piece capturePiece = get(pawnSquare);
        if (capturePiece == null || capturePiece.getType() != PieceType.PAWN || capturePiece.getColor() != getTurn().other()) {
            return false;
        }

        Square captureSquare = Square.fromRankAndFile(captureRank, file);
        if (get(captureSquare) != null) return false;

        if (this.getTurn() == Color.WHITE) {
            if (file > 'a') {
                //check left file
                Square square = Square.fromRankAndFile(rank, (char) (file - 1));
                Piece piece = get(square);
                if (piece != null && piece.getSymbol() == 'P') return true;
            }
            if (file < 'h') {
                //check right file
                Square square = Square.fromRankAndFile(rank, (char) (file + 1));
                Piece piece = get(square);
                if (piece != null && piece.getSymbol() == 'P') return true;
            }
        } else {
            if (file > 'a') {
                //check left file
                Square square = Square.fromRankAndFile(rank, (char) (file - 1));
                Piece piece = get(square);
                if (piece != null && piece.getSymbol() == 'p') return true;
            }
            if (file < 'h') {
                //check right file
                Square square = Square.fromRankAndFile(rank, (char) (file + 1));
                Piece piece = get(square);
                if (piece != null && piece.getSymbol() == 'p') return true;
            }
        }
        return false;
    }

    /**
     * Sets the availability of a specific castling right.
     *
     * @param type   The type of castling right to set (K, Q, k, or q).
     * @param status The status indicating whether the castling right should be available (true) or not (false).
     * @throws IllegalArgumentException If the type of castling right is invalid
     */
    @Override
    public void setCastlingRight(char type, boolean status) {
        if ("KQkq".indexOf(type) == -1) throw new IllegalArgumentException("Invalid type of castling right: " + type);
        if (status && !getTheoreticalCastlingRight(type))
            throw new IllegalArgumentException("Castling not possible for the type " + type);

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

    /**
     * Gets the file of the en passant target square.
     *
     * @return The file of the en passant target square (a, b, c, d, e, f, g, h),
     * or null if there is no en passant target square.
     */
    @Override
    public Character getEpFile() {
        return epFile;
    }

    /**
     * Sets the file of the en passant target square.
     *
     * @param file The file of the en passant target square to set (a, b, c, d, e, f, g, h).
     * @throws IllegalArgumentException if the specified file is null or not a valid file (not in the range a-h).
     */
    @Override
    public void setEpFile(Character file) {
        if (file == null || "abcdefgh".indexOf(file) == -1) {
            throw new IllegalArgumentException("Invalid En passant file");
        }
        epFile = file;
    }

    /**
     * Gets the number of half moves since the last capture or pawn advance.
     *
     * @return The number of half moves since the last capture or pawn advance.
     */
    @Override
    public int getHalfMoves() {
        return halfMoves;
    }

    /**
     * Sets the number of half moves since the last capture or pawn advance.
     *
     * @param halfMoves The number of half moves to set, must be non-negative.
     * @throws IllegalArgumentException if the specified number of half moves is negative.
     */
    @Override
    public void setHalfMoves(int halfMoves) {
        if (halfMoves < 0) {
            throw new IllegalArgumentException("Number of half moves must be non-negative");
        }
        this.halfMoves = halfMoves;
    }

    /**
     * Gets the number of the current full move.
     * The full move counter starts at 1 and is incremented after each white move.
     *
     * @return The number of the current full move.
     */
    @Override
    public int getMoveNumber() {
        return moveNumber;
    }

    /**
     * Sets the number of the current full move.
     *
     * @param moveNumber The number of the current full move.
     */
    @Override
    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    /**
     * Gets the counts of each piece type for the specified color on the board.
     *
     * @param color The color of the pieces to count. It can be "w" for white, "b" for black, "wb" or "bw" for both white and black.
     * @return A map containing the counts of each piece type for the specified color. The keys represent the piece types (PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING), and the values represent the counts of each piece type.
     * @throws IllegalArgumentException if the specified color string is invalid.
     */
    @Override
    public Map<PieceType, Integer> getPieceCounts(String color) {
        if (!color.equals("w") && !color.equals("b") && !color.equals("wb") && !color.equals("bw"))
            throw new IllegalArgumentException("Invalid color string: " + color);

        Map<PieceType, Integer> pieceCounts = new HashMap<>();
        pieceCounts.put(PieceType.PAWN, 0);
        pieceCounts.put(PieceType.ROOK, 0);
        pieceCounts.put(PieceType.KNIGHT, 0);
        pieceCounts.put(PieceType.BISHOP, 0);
        pieceCounts.put(PieceType.QUEEN, 0);
        pieceCounts.put(PieceType.KING, 0);

        for (Piece piece : board) {
            if (piece != null && color.contains(piece.getColor().shortName())) {
                int value = pieceCounts.getOrDefault(piece.getType(), 0);
                value++;
                pieceCounts.put(piece.getType(), value);
            }
        }
        return pieceCounts;
    }

    /**
     * Gets the square where the specified color's king is located.
     *
     * @param color The color of the king to locate.
     * @return The square where the specified color's king is located, or null if the king is not found on the board.
     */
    @Override
    public Square getKingSquare(Color color) {
        for (Square square : Square.getAll()) {
            Piece piece = board[square.get0x88Index()];
            if (piece != null && piece.getColor() == color && piece.getType() == PieceType.KING) {
                return square;
            }
        }
        return null;
    }

    /**
     * Gets the FEN (Forsyth-Edwards Notation) representation of the current position.
     *
     * @return The FEN string representing the current position.
     */
    @Override
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

    /**
     * Sets the position based on the given FEN (Forsyth-Edwards Notation) string.
     *
     * @param fen The FEN string representing the position to set.
     * @throws InvalidMoveException If the provided FEN string is invalid.
     */
    @Override
    public void setFen(String fen) {
        String[] parts = fen.split(" ");
        if (parts.length != 6) {
            throw new InvalidMoveException("FEN must have 6 parts, but only has " + parts.length);
        }

        String[] rows = parts[0].split("/");
        if (rows.length != 8) {
            throw new InvalidMoveException("Position part of the FEN must have 8 rows, but only has " + rows.length);
        }

        for (String row : rows) {
            int rowSum = 0;
            boolean previousWasNumber = false;

            for(char c : row.toCharArray()) {
                if(c >= '1' && c <= '8') {
                    if (previousWasNumber) {
                        throw new InvalidMoveException("Position part of the FEN is invalid Several numbers in a row");
                    }

                    rowSum += Character.getNumericValue(c);
                    previousWasNumber = true;
                } else if ("pnbrkqPNBRKQ".indexOf(c) != -1) {
                    rowSum++;
                    previousWasNumber = false;
                } else {
                    throw new InvalidMoveException("Position part of the FEN is invalid: Invalid character " + c);
                }
            }

            if(rowSum != 8) {
                throw new InvalidMoveException("Position part of the FEN is invalid: Invalid row length");
            }
        }

        if (!parts[1].equals("w") && !parts[1].equals("b")) {
            throw new InvalidMoveException("Turn part of the FEN is invalid: Expected w or b, but was " + parts[1]);
        }
        if (!Pattern.matches("^(KQ?k?q?|Qk?q?|kq?|q|-)$", parts[2])) {
            throw new InvalidMoveException("Castling part of the FEN is invalid");
        }
        if (!Pattern.matches("^(-|[a-h][36])$", parts[3])) {
            throw new InvalidMoveException("En-passant part of the FEN is invalid");
        }
        if (!Pattern.matches("^(0|[1-9][0-9]*)$", parts[4])) {
            throw new InvalidMoveException("Half move part of the FEN is invalid");
        }
        if (!Pattern.matches("^[1-9][0-9]*$", parts[5])) {
            throw new InvalidMoveException("Full move part of the FEN is invalid");
        }

        this.board = new Piece[128];
        int index = 0x70;

        for (char c : parts[0].toCharArray()) {
            if (c == '/') {
                index -= 24;
            } else if (c >= '1' && c <= '8') {
                index += Character.getNumericValue(c);
            } else {
                this.board[index] = new Piece(c);
                index++;
            }
        }

        turn = Color.fromSymbol(parts[1].charAt(0));

        for (char type : new char[]{'K', 'Q', 'k', 'q'}) {
            this.setCastlingRight(type, parts[2].indexOf(type) != -1);
        }

        epFile = parts[3].equals("-") ? null : parts[3].charAt(0);

        halfMoves = Integer.parseInt(parts[4]);
        moveNumber = Integer.parseInt(parts[5]);
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

    /**
     * Checks whether the king of the specified color is under attack.
     *
     * @param color The color of the king to check.
     * @return {@code true} if the king is under attack, otherwise {@code false}.
     */
    public boolean isKingAttacked(Color color) {
        Square square = this.getKingSquare(color);

        if (square == null) return false;

        return this.isAttacked(color.other(), square);
    }

    /**
     * Checks whether the specified square is attacked by any piece of the given color.
     *
     * @param color  The color of the pieces to check for attackers.
     * @param square The square to check for attacks.
     * @return {@code true} if the square is attacked by any piece of the specified color, otherwise {@code false}.
     */
    private boolean isAttacked(Color color, Square square) {
        return !this.getAttackers(color, square).isEmpty();
    }

    /**
     * Gets a list of squares from which pieces of the specified color are attacking the given square.
     *
     * @param color  The color of the attacking pieces.
     * @param square The square being attacked.
     * @return A list of squares from which pieces of the specified color are attacking the given square.
     */
    private List<Square> getAttackers(Color color, Square square) {
        List<Square> attackingSquares = new ArrayList<>();

        for (Square source : Square.getAll()) {
            Piece piece = get(source);
            if (piece == null || piece.getColor() != color) continue;

            int diff = source.get0x88Index() - square.get0x88Index();
            int index = diff + 119;

            if ((BoardUtility.ATTACKS[index] & (1 << piece.getType().ordinal())) != 0) {
                if (piece.getType() == PieceType.PAWN) {
                    if (diff > 0) {
                        if (piece.getColor() == Color.BLACK) {
                            attackingSquares.add(source);
                        }
                    } else {
                        if (piece.getColor() == Color.WHITE) {
                            attackingSquares.add(source);
                        }
                    }
                    continue;
                }

                if (piece.getType() == PieceType.KNIGHT || piece.getType() == PieceType.KING) {
                    attackingSquares.add(source);
                }

                int offset = BoardUtility.RAYS[index];
                int targetIndex = source.get0x88Index() + offset;
                boolean blocked = false;
                while (targetIndex != square.get0x88Index()) {
                    if (this.board[targetIndex] != null) {
                        blocked = true;
                        break;
                    }
                    targetIndex += offset;
                }
                if (!blocked) {
                    attackingSquares.add(source);
                }
            }
        }
        return attackingSquares;
    }

    /**
     * Gets a list of all pseudo-legal moves for the current position.
     * Pseudo-legal moves include all moves that could be made according to the rules of chess
     * and might leave or put the current player's king in check .
     *
     * @return A list of all pseudo-legal moves for the current position.
     */
    public List<Move> getPseudoLegalMoves() {
        List<Move> moves = new ArrayList<>();

        int[] PAWN_OFFSETS = new int[]{16, 32, 17, 15};
        Map<PieceType, int[]> PIECE_OFFSETS = new HashMap<>();
        PIECE_OFFSETS.put(PieceType.KNIGHT, new int[]{-33, -31, -18, -14, 14, 18, 31, 33});
        PIECE_OFFSETS.put(PieceType.BISHOP, new int[]{-17, -15, 15, 17});
        PIECE_OFFSETS.put(PieceType.ROOK, new int[]{-16, -1, 1, 16});
        PIECE_OFFSETS.put(PieceType.QUEEN, new int[]{-17, -16, -15, -1, 1, 15, 16, 17});
        PIECE_OFFSETS.put(PieceType.KING, new int[]{-17, -16, -15, -1, 1, 15, 16, 17});

        for (Square square : Square.getAll()) {
            Piece piece = get(square);
            if (piece == null || piece.getColor() != this.getTurn()) continue;

            if (piece.getType() == PieceType.PAWN) {
                int index = square.get0x88Index() + PAWN_OFFSETS[0] * getTurn().forwardDirection();
                Square targetSquare = Square.from0x88Index(index);
                if (get(targetSquare) == null) {
                    if (targetSquare.isBackrank()) {
                        for (PieceType promoteTo : PieceType.promotionTypes()) {
                            moves.add(new Move(square, targetSquare, promoteTo));
                        }
                        continue;
                    }
                    moves.add(new Move(square, targetSquare));
                    index = square.get0x88Index() + PAWN_OFFSETS[1] * getTurn().forwardDirection();
                    targetSquare = Square.from0x88Index(index);
                    if ((this.getTurn() == Color.WHITE && square.getRank() == 2)
                            || (this.getTurn() == Color.BLACK && square.getRank() == 7) && get(targetSquare) == null) {
                        moves.add(new Move(square, targetSquare));
                    }
                }
                for (int i = 2; i < PAWN_OFFSETS.length; i++) {
                    index = square.get0x88Index() + PAWN_OFFSETS[i] * getTurn().forwardDirection();
                    if ((index & 0x88) != 0) continue;
                    targetSquare = Square.from0x88Index(index);
                    Piece capturedPiece = get(targetSquare);
                    if (capturedPiece != null && capturedPiece.getColor() != this.getTurn()) {
                        if (targetSquare.isBackrank()) {
                            for (PieceType promoteTo : PieceType.promotionTypes()) {
                                moves.add(new Move(square, targetSquare, promoteTo));
                            }
                        } else {
                            moves.add(new Move(square, targetSquare));
                        }
                    } else if ((capturedPiece == null) && epFile != null && (targetSquare.getFile() == epFile)) {
                        moves.add(new Move(square, targetSquare));
                    }
                }
            } else {
                for (int offset : PIECE_OFFSETS.get(piece.getType())) {
                    int index = square.get0x88Index();
                    for (; ; ) {
                        index += offset;
                        if ((index & 0x88) != 0) break;
                        Square targetSquare = Square.from0x88Index(index);
                        Piece targetPiece = get(targetSquare);
                        if (targetPiece == null) {
                            moves.add(new Move(square, targetSquare));
                        } else {
                            if (targetPiece.getColor() == this.getTurn()) break;
                            moves.add(new Move(square, targetSquare));
                            break;
                        }

                        if (piece.getType() == PieceType.KNIGHT || piece.getType() == PieceType.KING) break;
                    }
                }
            }
        }

        Color opponent = getTurn().other();
        char kingSideCastling = this.getTurn() == Color.WHITE ? 'K' : 'k';
        if (getCastlingRight(kingSideCastling)) {
            int kingIndex = this.getKingSquare(this.getTurn()).get0x88Index();
            int targetIndex = kingIndex + 2;
            if (board[kingIndex + 1] == null && board[targetIndex] == null && !this.isCheck()
                    && !this.isAttacked(opponent, Square.from0x88Index(kingIndex + 1))
                    && !this.isAttacked(opponent, Square.from0x88Index(targetIndex))) {
                moves.add(new Move(Square.from0x88Index(kingIndex), Square.from0x88Index(targetIndex)));
            }
        }

        char queenSideCastling = this.getTurn() == Color.WHITE ? 'Q' : 'q';
        if (getCastlingRight(queenSideCastling)) {
            int kingIndex = this.getKingSquare(this.getTurn()).get0x88Index();
            int targetIndex = kingIndex - 2;
            if (board[kingIndex - 1] == null && board[kingIndex - 2] == null
                    && board[kingIndex - 3] == null && !this.isCheck()
                    && !this.isAttacked(opponent, Square.from0x88Index(kingIndex - 1))
                    && !this.isAttacked(opponent, Square.from0x88Index(targetIndex))) {
                moves.add(new Move(Square.from0x88Index(kingIndex), Square.from0x88Index(targetIndex)));
            }
        }

        return moves;
    }

    /**
     * Gets a list of all legal moves for the current position.
     * Legal moves are a subset of pseudo-legal moves and are those that do not leave the current player's king in check
     * after the move has been made.
     *
     * @return A list of all legal moves for the current position.
     */
    public List<Move> getLegalMoves() {
        List<Move> moves = new ArrayList<>();
        for (Move move : getPseudoLegalMoves()) {
            Position copiedPosition = this.copy();
            copiedPosition.makeMove(move, false);
            if (!copiedPosition.isKingAttacked(this.getTurn())) {
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * Makes a move on the current position with move validation.
     * This method is a shorthand for calling {@code makeMove(move, true)}, which includes validation to ensure that the move
     * is legal.
     *
     * @param move The move to be made.
     */
    public void makeMove(Move move) {
        makeMove(move, true);
    }

    /**
     * Makes a move on the current position, optionally validating its legality.
     * If the {@code validate} parameter is set to true, the method will check if the move is legal before making it.
     * If the move is not legal, an {@link IllegalArgumentException} will be thrown.
     * After making the move, the turn is toggled to the opposite player, and other game state variables are updated
     * accordingly.
     *
     * @param move     The move to be made.
     * @param validate Whether to validate the legality of the move.
     * @throws IllegalArgumentException If the {@code validate} parameter is true and the move is not legal.
     */
    public void makeMove(Move move, boolean validate) {
        if (validate && !getLegalMoves().contains(move))
            throw new IllegalArgumentException("Move is illegal in current position");

        Piece movingPiece = this.get(move.getSource());
        boolean hasCaptured = this.get(move.getTarget()) != null;
        this.set(move.getTarget(), movingPiece);
        this.set(move.getSource(), null);
        this.epFile = null;

        this.toggleTurn();

        if (movingPiece.getType() == PieceType.PAWN) {
            if (move.getTarget().getFile() != move.getSource().getFile() && !hasCaptured) {
                if (this.getTurn() == Color.BLACK) {
                    this.board[move.getTarget().get0x88Index() + 16] = null;
                } else {
                    this.board[move.getTarget().get0x88Index() - 16] = null;
                }
                hasCaptured = true;
            }

            if (Math.abs(move.getTarget().getRank() - move.getSource().getRank()) == 2) {
                if (checkEnPassant(move.getTarget().getFile())) {
                    this.epFile = move.getTarget().getFile();
                } else {
                    this.epFile = null;
                }
            }

            if (move.getPromotion() != null) {
                this.set(move.getTarget(), Piece.fromTypeAndColor(move.getPromotion(), movingPiece.getColor()));
            }
        } else if (movingPiece.getType() == PieceType.KING) {
            int steps = move.getTarget().getX() - move.getSource().getX();

            if (Math.abs(steps) == 2) {
                int rookTargetIndex;
                int rookSourceIndex;
                if (steps == 2) {
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

        if (movingPiece.getType() == PieceType.PAWN || hasCaptured) {
            this.halfMoves = 0;
        } else {
            this.halfMoves++;
        }

        if ((this.getTurn() == Color.WHITE)) {
            this.moveNumber++;
        }
    }

    /**
     * Checks if the current player is in check.
     *
     * @return {@code true} if the current player is in check, {@code false} otherwise.
     */
    public boolean isCheck() {
        return this.isKingAttacked(this.getTurn());
    }

    /**
     * Checks if the current player is in checkmate.
     *
     * @return {@code true} if the current player is in checkmate, {@code false} otherwise.
     */
    public boolean isCheckmate() {
        if (!isCheck()) return false;
        return getLegalMoves().isEmpty();
    }

    /**
     * Checks if the game is in a stalemate position.
     *
     * @return {@code true} if the game is in stalemate, {@code false} otherwise.
     */
    public boolean isStalemate() {
        if (isCheck()) return false;
        return getLegalMoves().isEmpty();
    }

    /**
     * Checks if the current game position has insufficient material for either player to force a checkmate.
     *
     * @return {@code true} if the game position has insufficient material, {@code false} otherwise.
     */
    public boolean isInsufficientMaterial() {
        Map<PieceType, Integer> pieceCounts = getPieceCounts("wb");
        int sum = pieceCounts.values().stream().mapToInt(Integer::intValue).sum();
        if (sum == 2) {
            return true;
        } else if (sum == 3) {
            return pieceCounts.get(PieceType.BISHOP) == 1 || pieceCounts.get(PieceType.KNIGHT) == 1;
        } else if (sum == 2 + pieceCounts.get(PieceType.BISHOP)) {
            boolean whiteHasBishop = this.getPieceCounts("w").get(PieceType.BISHOP) != 0;
            boolean blackHasBishop = this.getPieceCounts("b").get(PieceType.BISHOP) != 0;

            if (whiteHasBishop && blackHasBishop) {
                Boolean color = null;

                for (Square square : Square.getAll()) {
                    Piece piece = this.get(square);
                    if (piece != null && piece.getType() == PieceType.BISHOP) {
                        if (color != null && color != square.isLight()) return false;
                        color = square.isLight();
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the game is over, either due to checkmate, stalemate, or insufficient material.
     *
     * @return {@code true} if the game is over, {@code false} otherwise.
     */
    public boolean isGameOver() {
        return this.isCheckmate() || this.isStalemate() || this.isInsufficientMaterial();
    }

    /**
     * Generates move information for the given move, including SAN (Standard Algebraic Notation) representation,
     * captured piece, special flags (en passant, castling, check, checkmate), and the moved piece.
     *
     * @param move The move for which move information is generated.
     * @return MoveInfo object containing move details.
     * @throws IllegalArgumentException if the given move is not a legal move in the current position.
     */
    public MoveInfo getMoveInfo(Move move) {
        if (!getLegalMoves().contains(move)) throw new IllegalArgumentException("Move is illegal in current position");

        Position copiedPosition = this.copy();
        copiedPosition.makeMove(move);

        Piece movedPiece = get(move.getSource());
        Piece capturedPiece = get(move.getTarget());

        boolean enPassant = false;

        if (movedPiece.getType() == PieceType.PAWN) {
            if (move.getTarget().getFile() != move.getSource().getFile() && capturedPiece == null) {
                enPassant = true;
                capturedPiece = Piece.fromTypeAndColor(PieceType.PAWN, copiedPosition.getTurn());
            }
        }

        boolean isKingSideCastling = movedPiece.getType() == PieceType.KING
                && (move.getTarget().getX() - move.getSource().getX() == 2);
        boolean isQueenSideCastling = movedPiece.getType() == PieceType.KING
                && (move.getTarget().getX() - move.getSource().getX() == -2);

        boolean isCheck = copiedPosition.isCheck();
        boolean isCheckmate = copiedPosition.isCheckmate();

        StringBuilder san = new StringBuilder();
        if (isKingSideCastling) {
            san.append("O-O");
        } else if (isQueenSideCastling) {
            san.append("O-O-O");
        } else {
            if (movedPiece.getType() != PieceType.PAWN) {
                san.append(Character.toUpperCase(movedPiece.getType().getSymbol()));
            }

            san.append(getDisambiguatedMove(move));

            if (capturedPiece != null) {
                if (movedPiece.getType() == PieceType.PAWN) {
                    san.append(move.getSource().getFile());
                }
                san.append("x");
            }

            san.append(move.getTarget().getName());

            if (move.getPromotion() != null) {
                san.append("=").append(Character.toUpperCase(move.getPromotion().getSymbol()));
            }
        }

        if (isCheckmate) {
            san.append("#");
        } else if (isCheck) {
            san.append("+");
        }

        if (enPassant) {
            san.append(" (e.p.)");
        }

        return new MoveInfo(move, movedPiece, capturedPiece, san.toString(),
                enPassant, isKingSideCastling, isQueenSideCastling, isCheck, isCheckmate);
    }

    /**
     * Parses a move in Standard Algebraic Notation (SAN) format and returns the corresponding Move object.
     *
     * @param san The move in SAN format to parse.
     * @return The Move object corresponding to the parsed SAN move.
     * @throws InvalidMoveException if the SAN string does not represent a valid move.
     */
    public Move parseSan(String san) {
        san = san.replace('0', 'O');
        if (san.equals("O-O") || san.equals("O-O-O")) {
            int rank = this.getTurn() == Color.WHITE ? 1 : 8;
            Square source = Square.fromRankAndFile(rank, 'e');
            Square target;
            if (san.equals("O-O")) {
                target = Square.fromRankAndFile(rank, 'g');
            } else {
                target = Square.fromRankAndFile(rank, 'c');
            }
            return new Move(source, target);
        } else {
            Pattern pattern = Pattern.compile("^([NBKRQ])?([a-h])?([1-8])?[-x]?([a-h][1-8])(=?[nbrqkNBRQK])?[+#]?\\Z");
            Matcher matcher = pattern.matcher(san);
            if (!matcher.matches()) throw new InvalidMoveException("Invalid SAN " + san);

            PieceType pieceType;

            if (matcher.group(1) != null) {
                char pieceSymbol = matcher.group(1).toLowerCase().charAt(0);
                pieceType = PieceType.fromSymbol(pieceSymbol);
            } else {
                pieceType = PieceType.PAWN;
            }


            Square target = Square.fromName(matcher.group(4));
            PieceType promotionType = null;
            Square source = null;

            for (Move move : this.getLegalMoves()) {
                Piece piece = get(move.getSource());
                if(piece!= null && piece.getType() == pieceType && move.getTarget().get0x88Index() == target.get0x88Index()) {
                    if(matcher.group(2) != null && matcher.group(2).charAt(0) != move.getSource().getFile()){
                        continue;
                    }
                    if(matcher.group(3) != null && !matcher.group(3).equals(String.valueOf(move.getSource().getRank()))) {
                        continue;
                    }

                    if (source != null) throw new AmbiguousMoveException("Ambiguous SAN " + san);
                    source = move.getSource();

                }
            }

            if (source == null) throw new InvalidMoveException("Found no valid move for san " + san);

            if(matcher.group(5) != null) {
                char promotionSymbol = matcher.group(5).toLowerCase().charAt(0);
                promotionType = PieceType.fromSymbol(promotionSymbol);
            }

            return new Move(source, target, promotionType);
        }
    }

    /**
     * Returns a disambiguated move notation based on other legal moves on the board.
     *
     * @param move The move for which disambiguation is needed.
     * @return A disambiguated move notation if disambiguation is required, otherwise an empty string.
     */
    private String getDisambiguatedMove(Move move) {
        boolean sameFile = false;
        boolean sameRank = false;
        Piece piece = get(move.getSource());

        for (Move otherMove : getLegalMoves()) {
            Piece otherPiece = get(otherMove.getSource());
            if (piece.getType() == otherPiece.getType() && move.getSource().get0x88Index() != otherMove.getSource().get0x88Index()
                    && move.getTarget().get0x88Index() == otherMove.getTarget().get0x88Index()) {
                if (move.getSource().getFile() == otherMove.getSource().getFile()) {
                    sameFile = true;
                }
                if (move.getSource().getRank() == otherMove.getSource().getRank()) {
                    sameRank = true;
                }

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
