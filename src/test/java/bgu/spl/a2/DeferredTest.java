package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeferredTest {
	Deferred<Integer> test_deff;
	int testedInt;
	@Before
	public void setUp() throws Exception {
		test_deff = new Deferred<Integer>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetBeforeResolve() {
		
		try{
			test_deff.get();
			fail("should throw illegalStateException");
		}catch(IllegalStateException e){
			 
		}
        catch (Exception e) {
            fail("expected different exception");
        }
	}

	@Test
	public void testGetAfterResolve() 
	{
		testedInt= 21;
		Runnable testRun = ()->{testedInt=testedInt+testedInt;};
		//test_deff.whenResolved(testRun);
		testRun.run();
		test_deff.resolve(2);
		assertEquals(new Integer(2),test_deff.get());
		

	}
	
	@Test
	public void testIsResolved() {
		testedInt= 21;
		//Runnable testRun = ()->{testedInt=testedInt+testedInt;};
		//test_deff.whenResolved(testRun);
		test_deff.resolve(2);
		assertTrue(test_deff.isResolved());
	}

	@Test
	public void testResolve() {
		test_deff.resolve(42);
		try{
			test_deff.resolve(42);
			fail("should throw illegalStateException");
		}catch(IllegalStateException e){
			
		}
        catch (Exception e) {
            fail("expected different exception");
        }
	}

	@Test
	public void testWhenResolvedAfterResolve() {
		testedInt= 21;
		Runnable testRun = ()->{testedInt=testedInt+testedInt;};
		test_deff.resolve(2);
		test_deff.whenResolved(testRun);
		assertEquals(42, testedInt);	
	}
	
	@Test
	public void testWhenResolvedBeforeResolve() {
		testedInt= 21;
		Runnable testRun = ()->{testedInt=testedInt+testedInt;};
		test_deff.whenResolved(testRun);
		test_deff.resolve(2);
		assertEquals(42, testedInt);	
	}
	
	@Test
	public void testWhenResolvedBeforeResolvetTwoCallbacks() {
		testedInt= 21;
		Runnable testRun = ()->{testedInt=testedInt+testedInt;};
		Runnable testRun2 = ()->{testedInt=testedInt-testedInt;};
		test_deff.whenResolved(testRun);
		test_deff.whenResolved(testRun2);
		test_deff.resolve(2);
		assertEquals(0, testedInt);	
	}
	
	@Test
	public void testWhenResolvedDependancyOnGet()
	{
		testedInt= 21;
		Runnable testRun = ()->{testedInt=testedInt+test_deff.get();};
		test_deff.whenResolved(testRun);
		test_deff.resolve(2);
		assertEquals(23, testedInt);	
	
	}
	
}
