package com.magicfan;

import static org.junit.Assert.*;

import org.junit.Test;

import com.magicfan.ConnectionException;
import com.magicfan.ConnectionPool;
import com.magicfan.ConnectionPoolFactory;

public class ConnectionPoolFactoryTest {

	/**
	 * test get instance should return the same object 
	 * in multiple calls
	 */
	@Test
	public void testGetInstance() {
		ConnectionPoolFactory factory = ConnectionPoolFactory.getInstance();
		ConnectionPoolFactory factory1 = ConnectionPoolFactory.getInstance();
		assertEquals(factory, factory1);
	}

	/**
	 * test getConnectionpool should get pool when specified right name
	 */
	@Test
	public void testGetConnectionPool() {
		ConnectionPoolFactory factory = ConnectionPoolFactory.getInstance();
		try {
			ConnectionPool pool = factory.getConnectionPool("mypool");
			assertNotNull(pool);
			
		} catch (ConnectionException e) {
			fail("get pool failed!");
		}
		try {
			ConnectionPool poolUnknown = factory.getConnectionPool("poolUnkonw");
			assertNull(poolUnknown);
			fail("get pool failed!");
		} catch (ConnectionException e) {
			
		}
	}
}