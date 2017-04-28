package com.magicfan;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.magicfan.Connection;
import com.magicfan.ConnectionFactoryImpl;
import com.magicfan.ConnectionPoolImpl;
import com.magicfan.config.Config;
import com.magicfan.config.ConnectionConf;

public class ConnectionPoolImplTest {

	/**
	 * test getConnectionPool getinstance should get the same
	 * instance when multiple calls
	 */
	@Test
	public void testConnectionPoolGetInstance() {
		ConnectionConf info = Config.getInstance().getConfig("mypool");
		ConnectionFactoryImpl factory = new ConnectionFactoryImpl(info);
		try {
			new ConnectionPoolImpl(factory, -1, 1);
			fail("No exception throw!");
		} catch (IllegalArgumentException e) { }
		
		try {
			new ConnectionPoolImpl(factory, 1, -1);
			fail("No exception throw!");
		} catch (IllegalArgumentException e) { }
	}

	/**
	 * test in single thread case, getConnection should return 
	 * a right connection
	 */
	@Test
	public void testSingleThreadGetConnection() {
		ConnectionConf info = Config.getInstance().getConfig("mypool");
		ConnectionFactoryImpl factory = new ConnectionFactoryImpl(info);
		ConnectionPoolImpl impl = new ConnectionPoolImpl(factory, 10, 20);
		assertNotNull(impl.getConnection(10, TimeUnit.SECONDS));
		impl.shutdown();
	}

	/**
	 * test in single thread case, getConnection should release 
	 * a connection
	 */
	@Test
	public void testSingleThreadReleaseConnection() {
		ConnectionConf info = Config.getInstance().getConfig("mypool");
		ConnectionFactoryImpl factory = new ConnectionFactoryImpl(info);
		ConnectionPoolImpl impl = new ConnectionPoolImpl(factory, 10, 20);
		Connection conn = impl.getConnection(10, TimeUnit.SECONDS);
		impl.releaseConnection(conn);
	}
	
	/**
	 * test in single thread case, getConnection should return null 
	 * when connections is used up
	 */
	@Test
	public void testWhenPoolUseupConnection() {
		ConnectionConf info = Config.getInstance().getConfig("mypool");
		ConnectionFactoryImpl factory = new ConnectionFactoryImpl(info);
		ConnectionPoolImpl impl = new ConnectionPoolImpl(factory, 5, 5);
		for (int j= 0; j<5; j++) {
			Connection conn = impl.getConnection(10, TimeUnit.SECONDS);
			assertNotNull(conn);
		}
		
		Connection conn = impl.getConnection(10, TimeUnit.SECONDS);
		assertNull(conn);
		impl.shutdown();
	}
	
	/**
	 * test in single thread case, getConnection should return connection 
	 * when connections is used up for minimum connection but hasn't reach
	 * the maximum connections
	 */
	@Test
	public void testWhenConnectionPoolNeedExpand() {
		ConnectionConf info = Config.getInstance().getConfig("mypool");
		ConnectionFactoryImpl factory = new ConnectionFactoryImpl(info);
		ConnectionPoolImpl impl = new ConnectionPoolImpl(factory, 5, 6);
		for (int j= 0; j<5; j++) {
			Connection conn = impl.getConnection(10, TimeUnit.SECONDS);
			assertNotNull(conn);
		}
		
		Connection conn = impl.getConnection(10, TimeUnit.SECONDS);
		assertEquals(impl.idleConnections.size() + impl.inUsedConnections.size(), 6);
		assertNotNull(conn);
		impl.shutdown();
	}

	/**
	 * test in multiple thread scenario, getConnection should return right 
	 * connections
	 */
	@Test
	public void testMutilthreadGetConnections() {
		ConnectionConf info = Config.getInstance().getConfig("mypool");
		ConnectionFactoryImpl factory = new ConnectionFactoryImpl(info);
		ConnectionPoolImpl impl = new ConnectionPoolImpl(factory, 5, 5);
		
		Runnable r = () -> {
			impl.getConnection(10, TimeUnit.SECONDS);
		};
		
		final int threadnum = 3;
		Thread [] threads = new Thread[threadnum];
		for (int j = 0; j< threadnum; j++) {
			threads[j] = new Thread(r);
			threads[j].start();
			
		}
		
		for (int j = 0; j< threadnum; j++) {
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		assertEquals(impl.idleConnections.size(), 5 - threadnum);
		impl.shutdown();
	}
	
	
	/**
	 * test in multiple thread scenario, getConnection should return right 
	 * connections
	 */
	@Test
	public void testMutilthreadReleaseConnections() {
		ConnectionConf info = Config.getInstance().getConfig("mypool");
		ConnectionFactoryImpl factory = new ConnectionFactoryImpl(info);
		ConnectionPoolImpl impl = new ConnectionPoolImpl(factory, 5, 5);
		
		Runnable r = () -> {
			Connection conn = impl.getConnection(10, TimeUnit.SECONDS);
			impl.releaseConnection(conn);
		};
		
		final int threadnum = 3;
		Thread [] threads = new Thread[threadnum];
		for (int j = 0; j< threadnum; j++) {
			threads[j] = new Thread(r);
			threads[j].start();
			
		}
		
		for (int j = 0; j< threadnum; j++) {
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		assertEquals(impl.idleConnections.size(), 5);
		impl.shutdown();
	}
}
