package bgu.spl.a2;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class VersionMonitorTest {
	VersionMonitor test_monitor;
	
	@Before
	public void setUp() throws Exception {
		test_monitor=new VersionMonitor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetVersion() {
		assertEquals(0,test_monitor.getVersion());
	}

	@Test
	public void testIncOnce() {
		int pre=test_monitor.getVersion();
		test_monitor.inc();
		assertEquals(pre+1,test_monitor.getVersion());
	}
	
	@Test
	public void testIncTwice() {
		int pre=test_monitor.getVersion();
		test_monitor.inc();
		test_monitor.inc();
		assertEquals(pre+2,test_monitor.getVersion());
	}


	@Test
	public void testAwait() {
		int pre=test_monitor.getVersion();
		Thread t= new Thread(() -> {
		       try
		       {
				test_monitor.await(pre);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				fail("intterupted exception");
			}
		      
		    });
		t.start();
		test_monitor.inc();
		assertTrue(pre!=test_monitor.getVersion());

	}
	@Test
	public void testAwait2() {
		
		int pre=test_monitor.getVersion();
		try {
			test_monitor.await(pre+2);
		} catch (InterruptedException e) {
			fail("interrupted early");
		}
		assertTrue(pre+2!=test_monitor.getVersion());		
	}
	
	
	
}
