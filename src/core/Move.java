package core;

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
}
