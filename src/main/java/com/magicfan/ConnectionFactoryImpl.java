package com.magicfan;

import java.util.logging.Logger;

import com.magicfan.config.ConnectionConf;

/**
 * The implementation of ConnectionFactory
 *
 */

public class ConnectionFactoryImpl implements ConnectionFactory {
	private static Logger log = Logger.getLogger(ConnectionFactoryImpl.class.getName());
	private final ConnectionConf conf;
	
	public ConnectionFactoryImpl (ConnectionConf conf) {
		this.conf = conf;
	}

	@Override
	public Connection newConnection() throws ConnectionException {
		 if (!conf.valid()) {
			 log.severe("Connection can't be created because : " + conf.toString());
			 throw new ConnectionException("Connection can't be created because : " + conf.toString());
		 }
		 
	     return new ConnectionImpl(conf);
		
	}

}
