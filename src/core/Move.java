package core;

import java.util.Objects;

public class Move {

    private Square source;
    private Square target;
    private PieceType promotion;

    public Move(Square source, Square target, PieceType promotion) {
        this.source = source;
        this.target = target;
        this.promotion = promotion;

        if(promotion != null){
            assert target.isBackrank();
            assert promotion == PieceType.ROOK || promotion == PieceType.KNIGHT
                    || promotion == PieceType.BISHOP || promotion == PieceType.QUEEN;
        }
    }

    public Move (Square source, Square target){
        this(source, target, null);
    }

    public String getUciMove(){
        String promotionSymbol = "";
        if(this.promotion != null) {
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
