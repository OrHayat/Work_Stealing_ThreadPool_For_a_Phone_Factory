package bgu.spl.a2.sim.tools;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.a2.sim.Product;

public class RandomSumPliersTest {
	RandomSumPliers testDriver;
	Product testProduct;
	@Before
	public void setUp() throws Exception {
		testDriver=new RandomSumPliers();
		testProduct=new Product(12,"test");
	}

	@After
	public void tearDown() throws Exception {
	}


	@Test
	public void testGetType() {
		assertEquals(testDriver.getType(), "rs-pliers");
	}

	@Test
	public void testUseOnSame() {
		Product testProductTag=new Product(12,"test");
		assertEquals(testDriver.useOn(testProduct),testDriver.useOn(testProductTag));
	}
	@Test
	public void testUseOnDifferent() {
		Product testProductTag=new Product(13,"test");
		
		assertTrue(testDriver.useOn(testProduct)!=testDriver.useOn(testProductTag));

	}
	
	@Test
	public void testUseOnWithPartsDiff() {
		Product testPart1=new Product(23, "part1");
		Product testPart2=new Product(15, "part2");
		testProduct.addPart(testPart1);
		testProduct.addPart(testPart2);
		
		Product testProductTag=new Product(13,"test");
		Product testPart3=new Product(11, "part1");
		Product testPart4=new Product(48, "part2");
		testProductTag.addPart(testPart3);
		testProductTag.addPart(testPart4);
		assertTrue(testDriver.useOn(testProduct)!=testDriver.useOn(testProductTag));
	}
	@Test
	public void testUseOnWithPartsSame() {
		Product testPart1=new Product(23, "part1");
		Product testPart2=new Product(15, "part2");
		testProduct.addPart(testPart1);
		testProduct.addPart(testPart2);
		
		Product testProductTag=new Product(12,"test");
		Product testPart3=new Product(23, "part1");
		Product testPart4=new Product(15, "part2");
		testProductTag.addPart(testPart3);
		testProductTag.addPart(testPart4);
		assertEquals(testDriver.useOn(testProduct),testDriver.useOn(testProductTag));	}

}
