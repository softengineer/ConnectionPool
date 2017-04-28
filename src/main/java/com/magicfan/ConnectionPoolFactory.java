package com.magicfan;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.magicfan.config.Config;
import com.magicfan.config.ConnectionConf;

/**
 * 
 * Connection pool entrance class<br>
 * 
 * Use this class to get connection pool by specified name,
 * which is defined in ConnectionPoolConfig.xml
 * 
 * once a pool is returned successfully , you can use
 * getConnection, releaseConnection to get / return connection
 * from pool
 * 
 */

public class ConnectionPoolFactory {
	 private static Logger log = Logger.getLogger(ConnectionPoolFactory.class.getName());
	 private static Map<String, ConnectionPool> pools = new HashMap<>();
	 private static ConnectionPoolFactory factory = new ConnectionPoolFactory();
	 
	/**
	 * use to initialing factory class and add
	 * hook shutdown to close all pool instance
	 * when jvm exits
	 */
	 private ConnectionPoolFactory () {
		 Runnable r =  ()-> { 
				pools.keySet().stream().forEach(
					(e)->{
					log.info("Shutting down pool : " + e);
					pools.get(e).shutdown();
				 });
		 };
		 Runtime.getRuntime().addShutdownHook(new Thread(r));
	 }
	 
	 /**
	  * get factory instance
	  * @return factory
	  */
	 public static ConnectionPoolFactory getInstance() {
		 return factory;
	 }
	 
	 /**
	  * get a connection pool by given name, the same install will be returned
	  * with same pool name
	  * @param  poolname poolname in configration
	  *         file
	  * @return a initialized connection pool
	  * @throws ConnectionException 
	  *         when given name can't be found
	  */
     public synchronized ConnectionPool getConnectionPool(String poolname) throws ConnectionException{
    	 if (!pools.containsKey(poolname)) {
    		 ConnectionConf conf = Config.getInstance().getConfig(poolname);
    		 if (conf == null) {
    			 throw new ConnectionException("No such pool config : " + poolname);
    		 } else {
    			 log.info("Initializing pool : " + poolname);
    			 ConnectionPool pool = new ConnectionPoolImpl(new ConnectionFactoryImpl(conf), 
    					 conf.getMinpoolsize(), 
    					 conf.getMaxpoolsize());
    			 pools.put(poolname, pool);
    		 }
    	 }
    	 return pools.get(poolname);
     }
}
