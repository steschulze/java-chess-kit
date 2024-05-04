package de.stefanet.javachesskit;

/**
 * Outcome of a game with termination reason and winning color.
 */
public class Outcome {
    private Termination termination;
    private Color winner;

    /**
     * Constructs a new Outcome with the specified termination and winner.
     *
     * @param termination the termination reason
     * @param winner      the winning color
     */
    public Outcome(Termination termination, Color winner) {
        this.termination = termination;
        this.winner = winner;
    }

    /**
     * Returns the result of the game in PGN format.
     *
     * @return the result of the game in PGN format.
     *         Returns "1-0" if white wins, "0-1" if black wins and "1/2-1/2" if the game is a draw.
     * @see <a href="https://www.thechessdrum.net/PGN_Reference.txt">PGN Format</a>
     */
    public String result() {
        return winner == null ? "1/2-1/2" : winner == Color.WHITE ? "1-0" : "0-1";
    }

    /**
     * Returns the termination reason.
     *
     * @return the termination reason
     */
    public Termination getTermination() {
        return termination;
    }

    /**
     * Returns the winning color.
     *
     * @return the winning color
     */
    public Color getWinner() {
        return winner;
    }

    @Override
    public String toString() {
        return "Outcome{" + "termination=" + termination + ", winner=" + winner + '}';
    }
}
