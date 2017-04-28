package com.magicfan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;


/**
 * Connection pool implementation.
 *
 * 
 *
 */
public class ConnectionPoolImpl implements ConnectionPool {
   
	/**
	 * testConnection thread start delay time
	 * unit : second
	 */
	private final int DELAY_SECOND = 5;
	
	/**
	 * testConnection thread polling period time
	 * unit : second
	 */
	private final int PERIOD_SECOND = 5;
	
	/**
	 * minConnection in pool
	 */
	private final int minConnections;
	
	/**
	 * maxConnection in pool
	 */
	private final int maxConnections;
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private final ConnectionFactory connectionFactory;
	
	/**
	 * list to hold the idle connections
	 */
	final List<Connection> idleConnections = new ArrayList<>();
	
	/**
	 * list to hold the occupied connections
	 */
	final Map<Connection, String> inUsedConnections = new WeakHashMap<>();
	
	private final static Logger log = Logger.getLogger(ConnectionPoolImpl.class.getName());
	
	 /**
     * Construct a new pool that uses a provided factory to construct
     * connections, and allows a given maximum number of connections 
     * simultaneously.
     *
     * @param factory the factory to use to construct connections
     * @param minConnections the number of minimum connections in pool
     * @param maxConnections the number of simultaneous connections to allow
     */
    public ConnectionPoolImpl(ConnectionFactory factory, int minConnections, 
                              int maxConnections)
    {
        // Your implementation here.
    	if (minConnections <=0) {
    		log.severe("Invalid minConnections number :" + minConnections);
    		throw new IllegalArgumentException("Invalid maxConnections number :" + minConnections);
    	}
    	
    	if (maxConnections <=0) {
    		log.severe("Invalid maxConnections number :" + maxConnections);
    		throw new IllegalArgumentException("Invalid maxConnections number :" + maxConnections);
    	}
    	
    	this.minConnections = minConnections;
    	this.maxConnections = maxConnections;
    	this.connectionFactory = factory;
    	
    	initPool();
    }
    
    /**
     * initialize connection pool
     */
    private synchronized void initPool() {
    	expandPool(minConnections);
    	
    	Runnable r = () -> {
    		log.info("Checking connections are usable ...");
    		synchronized(this) {
    			Iterator<Connection> itr = idleConnections.iterator();
    			while (itr.hasNext()) {
    				Connection conn = itr.next();
    				if (!conn.testConnection()) {
    					log.warning("A connection is removed because it is unusable!");
    					itr.remove();
    				}
    			}
    				
    			if (poolsize() < minConnections) {
    				expandPool(minConnections);
    			}
    		}
    	};
    	
    	scheduler.scheduleWithFixedDelay(r, DELAY_SECOND, PERIOD_SECOND, TimeUnit.SECONDS);
    }
    
    
    /**
     * expand free list pool to targetSize
     * @param targetSize the new free list size
     */
    private void expandPool(int targetSize) {
    	int  errorCount = 0;
    	while( poolsize() < targetSize) {
    		try {
    			idleConnections.add(connectionFactory.newConnection());
    		} catch (ConnectionException e) {
    			errorCount++;
    			log.severe("expanding connection pool has error:  " + e.getMessage());
    			if (errorCount >= minConnections) {
    				log.severe("can't expanding connection pool to " + targetSize + " because fail too many times :" + errorCount);
    				return;
    			}
    		}
    	}
    	log.info("Successfully expandPool to size : " + targetSize);
    }
    
    /**
     * Add connection to used list
     * @param conn a connection
     * @return the given connection will be return
     */
    private Connection addConnectionToUsedList(Connection conn) {
    	inUsedConnections.put(conn, "");
    	return conn;
    }
    
    private int poolsize() {
    	return idleConnections.size() + inUsedConnections.size();
    }

    
    public synchronized Connection getConnection(long delay, TimeUnit units) {
       ExecutorService service  = Executors.newSingleThreadExecutor();
      
       Callable<Connection> c = ()-> {
	       
	       if (poolsize() < minConnections) {
	    	   expandPool(minConnections);
	       }
	       
	       if (idleConnections.size() ==0 && poolsize() < maxConnections) {
	    	   expandPool(maxConnections);
	       }
	       
	       while (idleConnections.size() > 0) {
	    	   Connection conn = idleConnections.remove(0);
	    	   
	    	   if (conn.testConnection()) {
	    		   addConnectionToUsedList(conn);
	    		   log.info("Obtain a a valid connection! idle size = " + idleConnections.size()
	    		    + ", usedIn = " + inUsedConnections.size());
	    		   return conn;
	    	   } else  {
		    	   //not pass the testConnect check, abandon it
		    	   log.warning("A connection can't pass testConn verify, discard it!");
	    	   }
	       }
	       
	       log.warning("Can't get connection from pool because no free connection in pool");
	       return null;
       } ;
       
       Future<Connection> f = service.submit(c);
       
       try {
    	   return f.get(delay, units);
       } catch (TimeoutException | ExecutionException | InterruptedException e) {
    	   log.severe("Getting connection causes timeout, " + e.getMessage());
    	   return null;
       } finally {
    	   service.shutdown();
       }
    }
        
    public synchronized void releaseConnection(Connection connection) {
    	String value = inUsedConnections.remove(connection);
    	if (value != null) {
    		idleConnections.add(connection);
    		log.info("Successfully return a connection to pool, idle size= " + idleConnections.size() + 
    		", usedIn = " + inUsedConnections.size());
    	} else 
    		log.warning("Can't return an object which is not belong to this pool");
    }
    
	@Override
	public synchronized void shutdown() {
		
		//close all used and free connections here
		
		//clean up all resources
		scheduler.shutdown();
		idleConnections.clear();
		inUsedConnections.clear();
	}
}
