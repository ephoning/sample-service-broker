package com.pivotal.cf.broker.service.foo;

import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceInstanceBindingExistsException;
import com.pivotal.cf.broker.exception.ServiceInstanceBindingDoesNotExistException;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.repo.ServiceRepo;
import com.pivotal.cf.broker.service.ServiceInstanceBindingService;
import com.pivotal.cf.broker.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by honine on 3/13/14.
 */
@Service
public class FooServiceInstanceBindingService implements ServiceInstanceBindingService {

    private static final Logger logger = LoggerFactory.getLogger(FooServiceInstanceBindingService.class);

    @Autowired
    private ServiceRepo serviceRepo;


    @Override
    public ServiceInstanceBinding createServiceInstanceBinding(
        String bindingId, ServiceInstance serviceInstance,
        String serviceId, String planId, String appGuid)
            throws ServiceInstanceBindingExistsException, ServiceBrokerException {

        ServiceInstanceBinding binding = getServiceInstanceBinding(bindingId);
        if(binding != null) {
            throw new ServiceInstanceBindingExistsException(binding);
        }

        // interact with actual service "provider" to create a service instance binding goes here

        // for this "demo", we just construct a set of credentials that can be used to access the built-in
        // FooService (see: com.pivotal.cf.broker.controller.foo.FooServiceController)

        // create a unique username / password pair for this service binding
        // (note that as we rely on a simple authentication provider (see: com.pivotal.cf.broker.auth.ReverseAuthenticationProvider)
        // that verifies credentials based on whether username and password or mirror images of one-another, we generate
        // a username and associated password accordingly...)
        String username =  generateRandomUsername(5);
        String password = reverse(username);

        String host = Utils.getAppUri();

        String uri = String.format("http://%s:%s@%s/foo/%s",
                username,
                password,
                host,
                serviceInstance.getId());
        Map<String, Object> creds = new HashMap<String,Object>();
        creds.put("uri", uri);
        creds.put("username", username);
        creds.put("password", password);
        creds.put("host", "localhost");
        creds.put("port", 8080);

        String sysLogDrainURL = "http://drain.eddie.org";

        binding =
            new ServiceInstanceBinding(bindingId, serviceInstance.getId(), creds, sysLogDrainURL, appGuid);

        serviceRepo.addBinding(bindingId, binding);
        return binding;
    }

    @Override
    public ServiceInstanceBinding getServiceInstanceBinding(String id) {
        return serviceRepo.getBinding(id);
    }

    @Override
    public ServiceInstanceBinding deleteServiceInstanceBinding(String id)
            throws ServiceBrokerException, ServiceInstanceBindingDoesNotExistException {


        ServiceInstanceBinding binding = getServiceInstanceBinding(id);
        if (binding == null) {
            throw new ServiceInstanceBindingDoesNotExistException(id);
        }

        // *** interaction with actual service "provider" to remove a service instance binding goes here ***

        return serviceRepo.removeBinding(id);
    }

    private String generateRandomUsername(int length) {
        return UUID.randomUUID().toString().substring(0,length);
    }

    private String reverse(String s) {
        return new StringBuffer(s).reverse().toString();
    }
}
