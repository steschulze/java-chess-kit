package de.stefanet.javachesskit.board0x88;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents chess moves consisting of a source square, a target square, and optionally a promotion type.
 */
public class Move {

    private final Square source;
    private final Square target;
    private final PieceType promotion;

    /**
     * Constructs a Move with the specified source square, target square, and promotion type.
     *
     * @param source    The source square of the move.
     * @param target    The target square of the move.
     * @param promotion The promotion type (can be null), either Rook, Knight, Bishop or Queen.
     * @throws IllegalArgumentException If the move is invalid, only in cases of promotion not null.
     */
    public Move(Square source, Square target, PieceType promotion) {
        this.source = source;
        this.target = target;
        this.promotion = promotion;

        if (promotion != null) {
            if (!target.isBackrank()) {
                throw new IllegalArgumentException("Invalid move: target square is not on backrank");
            }
            if (!PieceType.promotionTypes().contains(promotion)) {
                throw new IllegalArgumentException("Invalid move: invalid promotion type " + promotion);
            }
        }
    }

    /**
     * Constructs a Move with the specified source square and target square.
     *
     * @param source The source square of the move.
     * @param target The target square of the move.
     */
    public Move(Square source, Square target) {
        this(source, target, null);
    }

    /**
     * Creates a Move from the given UCI (Universal Chess Interface) notation.
     *
     * @param move The move string in UCI format.
     * @return A Move object representing the specified move.
     * @throws IllegalArgumentException If the move string is not in the correct format.
     * @see <a href="https://www.chessprogramming.org/UCI">UCI</a>
     */
    public static Move fromUCI(String move) {
        Pattern uci_regex = Pattern.compile("^([a-h][1-8])([a-h][1-8])([rnbq]?)$");
        Matcher matcher = uci_regex.matcher(move);
        if (!matcher.matches()) throw new IllegalArgumentException("No uci format: " + move);

        Square source = Square.fromName(matcher.group(1));
        Square target = Square.fromName(matcher.group(2));
        PieceType promotion = null;
        if (!matcher.group(3).isEmpty()) {
            promotion = PieceType.fromSymbol(matcher.group(3).charAt(0));
        }


        return new Move(source, target, promotion);
    }

    /**
     * Gets the source square of the move.
     *
     * @return The source square of the move.
     */
    public Square getSource() {
        return source;
    }

    /**
     * Gets the target square of the move.
     *
     * @return The target square of the move.
     */
    public Square getTarget() {
        return target;
    }

    /**
     * Gets the promotion type of the move (if any).
     *
     * @return The promotion type of the move, or null if there is no promotion.
     */
    public PieceType getPromotion() {
        return promotion;
    }

    /**
     * Gets the move string in UCI (Universal Chess Interface) notation.
     *
     * @return The move string in UCI format.
     * @see <a href="https://www.chessprogramming.org/UCI">UCI</a>
     */
    public String getUciMove() {
        String promotionSymbol = "";
        if (this.promotion != null) {
            promotionSymbol = String.valueOf(this.promotion.getSymbol());
        }

        return this.source.getName() + this.target.getName() + promotionSymbol;
    }

    @Override
    public String toString() {
        return String.format("Move.fromUCI(%s)", this.getUciMove());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move other = (Move) o;
        return this.getUciMove().equals(other.getUciMove());
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, promotion);
    }
}
