package de.stefanet.javachesskit;

/**
 * Enum representing the possible states of a chess position.
 */
public enum Status {
    /**
     * The position is valid.
     */
    VALID,

    /**
     * The position has no white king.
     */
    NO_WHITE_KING,

    /**
     * The position has no black king.
     */
    NO_BLACK_KING,

    /**
     * The position has too many kings.
     */
    TOO_MANY_KINGS,

    /**
     * The position has too many white pawns.
     */
    TOO_MANY_WHITE_PAWNS,

    /**
     * The position has too many black pawns.
     */
    TOO_MANY_BLACK_PAWNS,

    /**
     * The position has pawns on the first or eighth rank.
     */
    PAWNS_ON_BACKRANK,

    /**
     * The position has too many white pieces.
     */
    TOO_MANY_WHITE_PIECES,

    /**
     * The position has too many black pieces.
     */
    TOO_MANY_BLACK_PIECES,

    /**
     * The position has invalid castling rights.
     */
    BAD_CASTLING_RIGHTS,

    /**
     * The position has an invalid en passant square.
     */
    INVALID_EP_SQUARE,

    /**
     * The position has the side not to move in check.
     */
    OPPOSITE_CHECK,

    /**
     * The position is empty.
     */
    EMPTY,

    /**
     * The position has too many checkers.
     */
    TOO_MANY_CHECKERS,

    /**
     * The position has an impossible check.
     */
    IMPOSSIBLE_CHECK
}
