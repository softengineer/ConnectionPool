package com.magicfan;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Demo {
	private static Logger log = Logger.getLogger(Demo.class.getName());
	
	public static void main(String[] args) {
		log.info("Demo is started ....");
		boolean error = false;
		ConnectionPoolFactory factory = ConnectionPoolFactory.getInstance();
		try {
			String poolname = "mypool";
			log.info("Trying to get a pool named " + poolname);
			ConnectionPool pool = factory.getConnectionPool(poolname);
			
			if (pool != null) {
				log.info("Getting pool named " + poolname + " successfully");
			} else {
				log.info("Getting pool named " + poolname + " failed!");
				error = true;
			}
			
			log.info("Beginning to get connection...");
			Connection conn = pool.getConnection(5, TimeUnit.SECONDS);
			if (conn != null) {
				log.info("Getting connection successfully");
			} else {
				log.info("Getting connection failed!");
				error = true;
			}
			
			log.info("Returning connection to pool ....");
			pool.releaseConnection(conn);
			log.info("Releasing connection successfully");
			
			log.info("Shutdowning the pool ....");
			pool.shutdown();
		} catch (ConnectionException e) {
			log.warning("There are the error : " + e.getMessage());
			error = true;
		}
		
		if (!error) {
			log.info("All operations are successfully, demo is complete!");
		} else {
			log.info("There are some errors occured, please contact with author");
		}
	}

}
