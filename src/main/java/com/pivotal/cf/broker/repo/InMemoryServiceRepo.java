package com.pivotal.cf.broker.repo;

import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * in-memory repo for service instances and bindings
 * NOTE: for testing purposes only as this impl:
 * - does not persist => reliant on service broker uptime
 * - does not allow for multiple instances of the service broker to be running (no shared memory)
 *
 * Created by honine on 3/14/14.
 */
@Component
public class InMemoryServiceRepo implements ServiceRepo {

    private Map<String,ServiceInstance> instanceRepo = new HashMap<String, ServiceInstance>();
    private Map<String,ServiceInstanceBinding> bindingRepo = new HashMap<String, ServiceInstanceBinding>();

    @Override
    public ServiceInstance addInstance(String key, ServiceInstance instance) {
        instanceRepo.put(key, instance);
        return instance;
    }

    @Override
    public ServiceInstance removeInstance(String key) {
        ServiceInstance instance = instanceRepo.get(key);
        instanceRepo.remove(key);
        return instance;
    }

    @Override
    public ServiceInstance getInstance(String key) {
        return instanceRepo.get(key);
    }

    @Override
    public List<ServiceInstance> getInstances() {
        List<ServiceInstance> list;
        Collection<ServiceInstance> coll = instanceRepo.values();
        if(coll instanceof List)
            list = (List) coll;
        else
            list  = new ArrayList<ServiceInstance>(coll);
        return list;
    }

    @Override
    public boolean isInstanceBound(String key) {
        boolean bound = false;
        Collection<ServiceInstanceBinding> bindings = bindingRepo.values();
        for(ServiceInstanceBinding binding : bindings) {
            if(binding.getServiceInstanceId().equals(key)) {
                bound = true;
                break;
            }
        }
        return bound;
    }

    @Override
    public ServiceInstanceBinding addBinding(String key, ServiceInstanceBinding binding) {
        bindingRepo.put(key, binding);
        return binding;
    }

    @Override
    public ServiceInstanceBinding removeBinding(String key) {
        ServiceInstanceBinding binding = bindingRepo.get(key);
        bindingRepo.remove(key);
        return binding;
    }

    @Override
    public ServiceInstanceBinding getBinding(String key) {
        return bindingRepo.get(key);
    }

    @Override
    public List<ServiceInstanceBinding> getBindings() {
        List<ServiceInstanceBinding> list;
        Collection<ServiceInstanceBinding> coll = bindingRepo.values();
        if(coll instanceof List)
            list = (List) coll;
        else
            list  = new ArrayList<ServiceInstanceBinding>(coll);
        return list;
    }

}
