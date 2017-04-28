# ConnectionPool
ConnectionPool : A java connection pooling

Author: David Fan
Updated: 31 March 2017
Version: 0.0.1

1. What is ConnectionPool?

A Java-based connection pooling utility, supporting connection validation, and easy configuration using a pool configuration file.

2. Why would I use it?

Applications using connection pool frequently. For example, a sftp client may use pool mechanism to cache all connections to sftp server, a database pool uses pool to cache all connections to database. A connection pool maintains a pool of opened connections so the application can simply grab one when it needs to, use it, and then hand it back, eliminating much of the long wait for the creation of connections.

3. How to package it?

3.1 Prerequisite :

To compile connection pooling source code, Apache maven must be installed firstly

3.2 Usage :
cd <ConnectionPoolSrc_HOME>

3.2.1. package
mvn package

After compiling, the connection pool jar connectionpool-x.x.x.jar should be found under "target" directory

3.2.2. runtest
mvn test

You can find test report under "target/surefire-reports"

3.2.3 codo coverage
mvn cobertura:cobertura

You can find the coverage report under target/sites

4. How do I use it ?

4.1 Configuration 
In common scenario,  connectionpool-x.x.x.jar should be put in CLASSPATH, a detailed connection config should be configured in a file named "ConectionPoolConfig.xml" and put it under config directory or in CLASSPATH, a pool example in this file is the following

<?xml version="1.0" encoding="UTF-8"?> 
<connectionpools>
        <!-- pool name -->
	<connectionpool name = "mypool" >

 	  <!--url  pool connection url -->
	  <property name = "url">sftp://192.168.1.2</property>

          <!-- minpoolsize the minimum pool size when initializing -->
          <property name = "minpoolsize" ></property>

          <!-- maxpoolsize the maximum pool size -->
	  <property name = "maxpoolsize" ></property>

          <!-- username connection username -->
	  <property name = "username" ></property>

          <!-- username connection password -->
	  <property name = "password" ></property>

	</connectionpool>
</connectionpools>

You can define the arbitrary pool items in configuration file

4.2 Calling pooling from Java code

Once pool configuration is ready, you can get pool from poolFactory by the following code.

ConnectionPoolFactory factory = ConnectionPoolFactory.getInstance();
try {
   ConnectionPool pool = factory.getConnectionPool("mypool");
   Connection conn = pool.getConnection(10, TimeUnit.SECOND);
} catch (ConnectionException e) {
   // put error handle code here
}

5. Documents

Connection pooling JAVADoc API is located in <ConnectionPoolSrc_HOME>/doc

6. Logging
Java connection pooling uses java util logging as logger, by default, it uses the log configuration in JRE/lib, however, you can assign the log configuration file by yourselve, before starting app, assign -Djava.util.logging.config.file=<log config file path> to java command

7. Connection pool demo
You can use the following command to see a connection pooling demo

$cd ${connectionpool src}
$mvn compile
$mvn exec:java -Dexec.mainClass="com.magicfan.example.Demo"

