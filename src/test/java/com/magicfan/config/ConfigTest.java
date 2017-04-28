package com.magicfan.config;

import static org.junit.Assert.*;

import org.junit.Test;

import com.magicfan.config.Config;
import com.magicfan.config.ConnectionConf;

public class ConfigTest {

	@Test
	public void testGetInstance() {
		Config config = Config.getInstance();
		assertNotNull(config);
		
		Config config1 = Config.getInstance();
		assertEquals(config, config1);
	}

	@Test
	public void testGetConfig() {
		ConnectionConf info = Config.getInstance().getConfig("mypool");
		assertNotNull(info);
	}
}
