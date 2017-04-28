package com.magicfan;

/**
 * Marker superclass for exceptions thrown by a Connection or ConnectionFactory
 */
public class ConnectionException extends Exception {
    
	private static final long serialVersionUID = 1L;

	public ConnectionException() {
        super();
    }

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable nested) {
        super(message, nested);
    }
}
