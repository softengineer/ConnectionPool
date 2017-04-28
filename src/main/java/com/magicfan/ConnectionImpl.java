package com.magicfan;

import java.util.logging.Logger;

import com.magicfan.config.ConnectionConf;

/**
 * ConnectionImplement
 * 
 * represent a connection object
 *
 */

public class ConnectionImpl implements Connection {
	
	private static Logger log = Logger.getLogger(ConnectionImpl.class.getName());
	private boolean isConnected = false;
	
	public ConnectionImpl(ConnectionConf info) {
		log.info("creating connection object");
		connect();
	}
	
	private void connect() {
		isConnected = true;
	}

	@Override
	public boolean testConnection() {
		return isConnected;
	}
}
