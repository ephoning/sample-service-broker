package com.pivotal.cf.broker.exception;

import com.pivotal.cf.broker.model.ServiceInstanceBinding;

/**
 * Thrown when a duplicate request to bind to a service instance is 
 * received.
 * 
 * @author sgreenberg@gopivotal.com
 */
public class ServiceInstanceBindingDoesNotExistException extends Exception {

	private static final long serialVersionUID = -1L;

	private String bindingId;

	public ServiceInstanceBindingDoesNotExistException(String bindingId) {
		this.bindingId = bindingId;
	}
	
	public String getMessage() {
        return "ServiceInstanceBinding does not exist: id = " + bindingId;
	}
}
