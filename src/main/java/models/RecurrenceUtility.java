package models;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

public class RecurrenceUtility {

	public static Set<DayOfWeek> parseBitmaskToSet(int bitmask) {
		Set<DayOfWeek> daysOfWeek = new HashSet<>();
		for (int i = 0; i < 7; i++) {
			if ((bitmask & (1 << i)) != 0) {
				daysOfWeek.add(DayOfWeek.of(i + 1));
			}
		}
		return daysOfWeek;
	}
}
