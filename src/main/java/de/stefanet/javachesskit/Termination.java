package de.stefanet.javachesskit;

/**
 * Enum representing the possible terminations of a chess game.
 */
public enum Termination {
    /**
     * End of the game by Checkmate.
     */
    CHECKMATE,
    /**
     * End of the game by Stalemate.
     */
    STALEMATE,
    /**
     * End of the game by Insufficient material.
     */
    INSUFFICIENT_MATERIAL,
    /**
     * End of the game by claiming the Fifty moves rule.
     */
    FIFTY_MOVES,
    /**
     * End of the game by Seventyfive moves rule.
     */
    SEVENTYFIVE_MOVES,
    /**
     * End of the game by claiming the Threefold repetition rule.
     */
    THREEFOLD_REPETITION,
    /**
     * End of the game by Fivefold repetition rule.
     */
    FIVEFOLD_REPETITION
}
