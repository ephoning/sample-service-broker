package com.pivotal.cf.broker.repo;

import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;

import java.util.*;

/**
 * in-memory repo for service instances and bindings
 * NOTE: for testing purposes only as this impl:
 * - does not persist => reliant on service broker uptime
 * - does not allow for multiple instances of the service broker to be running (no shared memory)
 *
 * Created by honine on 3/14/14.
 */
public interface ServiceRepo {
    public ServiceInstance addInstance(String key, ServiceInstance instance);

    public ServiceInstance removeInstance(String key);

    public ServiceInstance getInstance(String key);

    public List<ServiceInstance> getInstances();

    public boolean isInstanceBound(String key);

    public ServiceInstanceBinding addBinding(String key, ServiceInstanceBinding binding);

    public ServiceInstanceBinding removeBinding(String key);

    public ServiceInstanceBinding getBinding(String key);

    public List<ServiceInstanceBinding> getBindings();
}
