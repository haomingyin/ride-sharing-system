package models;

import org.junit.Assert;
import org.junit.Test;

import java.time.DayOfWeek;
import java.util.Set;

import static org.junit.Assert.*;

/** Test recurrence bit mask to days of week set
 * Created by Haoming on 20/05/17.
 */
public class RecurrenceUtilityTest {
	@Test
	public void parseBitmaskToSet() throws Exception {
		int bitmask = 0;
		// add Monday
		bitmask |= 1;
		// add Thursday
		bitmask |= (1 << 3);
		// add Saturday
		bitmask |= (1 << 5);

		Set<DayOfWeek> dayOfWeekSet = RecurrenceUtility.parseBitmaskToSet(bitmask);
		Assert.assertTrue(dayOfWeekSet.contains(DayOfWeek.MONDAY));
		Assert.assertTrue(dayOfWeekSet.contains(DayOfWeek.THURSDAY));
		Assert.assertTrue(dayOfWeekSet.contains(DayOfWeek.SATURDAY));
	}

}