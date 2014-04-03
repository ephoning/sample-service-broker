package com.pivotal.cf.broker.exception;

import org.springframework.http.HttpStatus;

/**
 * General exception for underlying broker errors (like connectivity to the service
 * being brokered).
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public class ServiceBrokerException extends Exception {
	
	private static final long serialVersionUID = -5544859893499349135L;
	private String message;
    private HttpStatus statusCode;
	
	public ServiceBrokerException(String message, HttpStatus statusCode) {
		this.message = message;
        this.statusCode = statusCode;
	}
	
	public String getMessage() {

        return message;
	}

    public HttpStatus getStatusCode() { return statusCode; }
}