package de.stefanet.javachesskit;

import java.util.List;
import java.util.Map;

/**
 * Represents an attack table used for move generation.
 *
 * <p>An attack table is a precomputed table used for generating piece attacks.
 * It is used to speed up move generation by avoiding the need to calculate attacks on the fly.
 */
public class AttackTable {
    private long[] maskTable;
    private List<Map<Long, Long>> attackTable;

    /**
     * Constructs a new AttackTable with the specified mask table and attack table.
     *
     * @param maskTable   the mask table
     * @param attackTable the attack table
     */
    public AttackTable(long[] maskTable, List<Map<Long, Long>> attackTable) {
        this.maskTable = maskTable;
        this.attackTable = attackTable;
    }

    /**
     * Gets the mask table.
     *
     * @return the mask table
     */
    public long[] getMaskTable() {
        return maskTable;
    }

    /**
     * Gets the attack table.
     *
     * @return the attack table
     */
    public List<Map<Long, Long>> getAttackTable() {
        return attackTable;
    }
}
