package bgu.spl.a2.sim.tools;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import bgu.spl.a2.sim.Product;

public class NextPrimeHammerTest {
	NextPrimeHammer testDriver;
	Product testProduct;
	@Before
	public void setUp() throws Exception {
		testDriver=new NextPrimeHammer();
		testProduct=new Product(12,"test");
	}


	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetType() {
		assertEquals(testDriver.getType(),"np-hammer");

	}

	@Test
	public void testUseOn() 
	{

		assertEquals(13 , testDriver.useOn(testProduct));


	}

	@Test
	public void testGetNextPrime() {
		long res=testDriver.GetNextPrime(50123450);
		assertTrue(50123459==res);
	}
	@Test
	public void testGetNextPrimefalse() {
		long res=testDriver.GetNextPrime(50123450);
		assertFalse(50123460==res);
	}
	
	@Test
	public void testUseOnWithParts() {
		Product testPart1=new Product(23, "part1");
		Product testPart2=new Product(15, "part2");
		testProduct.addPart(testPart1);
		testProduct.addPart(testPart2);
		assertEquals(59 , testDriver.useOn(testProduct));
	}


}
