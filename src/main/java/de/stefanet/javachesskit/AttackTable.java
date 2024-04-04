package de.stefanet.javachesskit;

import java.util.List;
import java.util.Map;

public class AttackTable {
	private long[] maskTable;
	private List<Map<Long, Long>> attackTable;

	public AttackTable(long[] maskTable, List<Map<Long, Long>> attackTable) {
		this.maskTable = maskTable;
		this.attackTable = attackTable;
	}

	public long[] getMaskTable() {
		return maskTable;
	}

	public List<Map<Long, Long>> getAttackTable() {
		return attackTable;
	}
}
