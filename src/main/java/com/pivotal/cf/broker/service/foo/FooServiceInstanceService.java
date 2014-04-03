package com.pivotal.cf.broker.service.foo;

import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceInstanceIsBoundException;
import com.pivotal.cf.broker.exception.ServiceInstanceDoesNotExistException;
import com.pivotal.cf.broker.exception.ServiceInstanceExistsException;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.repo.ServiceRepo;
import com.pivotal.cf.broker.service.ServiceInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by honine on 3/13/14.
 */
@Service
public class FooServiceInstanceService implements ServiceInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(FooServiceInstanceService.class);

    @Autowired
    private ServiceRepo serviceRepo;

    @Override
    public List<ServiceInstance> getAllServiceInstances() {
        return serviceRepo.getInstances();
    }

    @Override
    public ServiceInstance createServiceInstance(
        ServiceDefinition service, String serviceInstanceId, String planId, String organizationGuid, String spaceGuid)
            throws ServiceInstanceExistsException, ServiceBrokerException {

        ServiceInstance instance = getServiceInstance(serviceInstanceId);
        if(instance != null) {
            throw new ServiceInstanceExistsException(instance);
        }

        // interaction with actual service "provider" to create a service instance goes here...

        instance =
            new ServiceInstance(serviceInstanceId, service.getId(), planId, organizationGuid, spaceGuid, "http://foo.eddie.org/my-dashboard-url");

        serviceRepo.addInstance(serviceInstanceId, instance);
        return instance;
    }

    @Override
    public ServiceInstance getServiceInstance(String id) {
        return serviceRepo.getInstance(id);
    }

    @Override
    public ServiceInstance deleteServiceInstance(String id)
            throws ServiceBrokerException, ServiceInstanceDoesNotExistException, ServiceInstanceIsBoundException {

        ServiceInstance instance = getServiceInstance(id);
        if (instance == null) {
            throw new ServiceInstanceDoesNotExistException(id);
        }

        if(serviceRepo.isInstanceBound(id)) {
            throw new ServiceInstanceIsBoundException(instance);
        }

        // interaction with actual service "provider" to delete a service instance goes here...

        return serviceRepo.removeInstance(id);
    }
}
