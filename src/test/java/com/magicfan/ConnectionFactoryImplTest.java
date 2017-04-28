package com.magicfan;

import static org.junit.Assert.*;

import org.junit.Test;

import com.magicfan.ConnectionException;
import com.magicfan.ConnectionFactory;
import com.magicfan.ConnectionFactoryImpl;
import com.magicfan.config.ConnectionConf;

public class ConnectionFactoryImplTest {

	/**
	 * Test if factory can return connection
	 */
	@Test
	public void testNewConnection() {
		ConnectionConf info = new ConnectionConf();
		info.setUrl("127.0.0.1");
		ConnectionFactory factory = new ConnectionFactoryImpl(info);
		try {
			assertNotNull(factory.newConnection());
		} catch (ConnectionException e) {
			fail("create connection fail!");
		}
	}
}
