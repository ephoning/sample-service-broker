package com.pivotal.cf.broker.exception;

import com.pivotal.cf.broker.model.ServiceInstance;

/**
 * Created by honine on 3/25/14.
 */
public class ServiceInstanceIsBoundException extends Exception {
    private static final long serialVersionUID = -1L;

    private ServiceInstance instance;

    public ServiceInstanceIsBoundException(ServiceInstance instance) {
        this.instance = instance;
    }

    public String getMessage() {
        return "ServiceInstance is currently bound: serviceInstance.id = "
                + instance.getId();
    }
}
