package models.database;

import models.position.StopPoint;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/** Given a stop point address, and test if it can be fetched from DB
 * Created by Haoming on 20/05/17.
 */
public class SearchStopPointTest {
	@Test
	public void preciseSearch() throws Exception {
		String address = "16 Vicki street, sockburn";
		List<StopPoint> stopPoints = SQLExecutor.fetchStopPointsByString(address, 1);
		Assert.assertEquals(1, stopPoints.size());
		Assert.assertEquals("16 Vicki Street, Sockburn, Christchurch", stopPoints.get(0).getFull());
	}

	@Test
	public void vagueSearch() throws Exception {
		String address = "49 houn ilam";
		List<StopPoint> stopPoints = SQLExecutor.fetchStopPointsByString(address, 1);
		Assert.assertEquals(1, stopPoints.size());
		Assert.assertEquals("49 Hounslow Street, Ilam, Christchurch", stopPoints.get(0).getFull());
	}

	@Test
	public void suburbSearch() throws Exception {
		String suburb = " Sockburn";
		List<StopPoint> stopPoints = SQLExecutor.fetchStopPointsByString(suburb, 10);
		Assert.assertEquals(10, stopPoints.size());
		stopPoints.forEach(sp -> Assert.assertEquals("Sockburn", sp.getSuburb()));
	}

}