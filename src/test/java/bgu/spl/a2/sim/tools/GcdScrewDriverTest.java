package bgu.spl.a2.sim.tools;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.a2.sim.Product;

public class GcdScrewDriverTest {
	GcdScrewDriver testDriver;
	Product testProduct;
	@Before
	public void setUp() throws Exception {
		testDriver=new GcdScrewDriver();
		testProduct=new Product(12,"test");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetType() {
		assertEquals(testDriver.getType(), "gs-driver");
	}

	@Test
	public void testUseOn() {
		
		assertEquals(3 , testDriver.useOn(testProduct));
	}
	@Test
	public void testUseOnWithParts() {
		Product testPart1=new Product(23, "part1");
		Product testPart2=new Product(15, "part2");
		testProduct.addPart(testPart1);
		testProduct.addPart(testPart2);
		assertEquals(7 , testDriver.useOn(testProduct));
	}

}
