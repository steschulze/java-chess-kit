package de.stefanet.javachesskit.core;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Move {

    private final Square source;
    private final Square target;
    private final PieceType promotion;

    public Move(Square source, Square target, PieceType promotion) {
        this.source = source;
        this.target = target;
        this.promotion = promotion;

        if (promotion != null) {
            assert target.isBackrank();
            assert promotion == PieceType.ROOK || promotion == PieceType.KNIGHT
                    || promotion == PieceType.BISHOP || promotion == PieceType.QUEEN;
        }
    }

    public Move(Square source, Square target) {
        this(source, target, null);
    }

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

    public Square getSource() {
        return source;
    }

    public Square getTarget() {
        return target;
    }

    public PieceType getPromotion() {
        return promotion;
    }

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
