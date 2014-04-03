package com.pivotal.cf.broker.service.mysql;

import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceInstanceBindingDoesNotExistException;
import com.pivotal.cf.broker.exception.ServiceInstanceBindingExistsException;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.repo.ServiceRepo;
import com.pivotal.cf.broker.service.ServiceInstanceBindingService;
import com.pivotal.cf.broker.util.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by honine on 3/13/14.
 */
@Service
public class MySQLServiceInstanceBindingService implements ServiceInstanceBindingService {

    private static final Logger logger = LoggerFactory.getLogger(MySQLServiceInstanceBindingService.class);

    private static final String getPasswordHashSQLTemplate =
            "SELECT PASSWORD('%s')";
    private static final String createGrantAndUserSQLTemplate =
            "GRANT ALL ON %s.* TO '%s'@'%s' IDENTIFIED BY PASSWORD '%s' WITH %s";
    private static final String deleteGrantAndUserSQLTemplate =
            "DROP USER '%s'@'%s'";

    // plan details
    private static Map<String,Integer> smallPlanOptions = new HashMap<String,Integer>();
    private static Map<String,Integer> largePlanOptions = new HashMap<String,Integer>();
    private static Map<String, Map<String, Integer>> planDetails = new HashMap<String, Map<String,Integer>>();
    static {
        planDetails.put("small-mysql-plan", smallPlanOptions);
        planDetails.put("large-mysql-plan", largePlanOptions);

        smallPlanOptions.put("MAX_QUERIES_PER_HOUR", 100);
        smallPlanOptions.put("MAX_UPDATES_PER_HOUR", 100);
        smallPlanOptions.put("MAX_CONNECTIONS_PER_HOUR", 10);
        smallPlanOptions.put("MAX_USER_CONNECTIONS", 4);

        largePlanOptions.put("MAX_QUERIES_PER_HOUR", 1000);
        largePlanOptions.put("MAX_UPDATES_PER_HOUR", 1000);
        smallPlanOptions.put("MAX_CONNECTIONS_PER_HOUR", 100);
        smallPlanOptions.put("MAX_USER_CONNECTIONS", 16);
    }

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public ServiceInstanceBinding createServiceInstanceBinding(
        String bindingId, ServiceInstance serviceInstance,
        String serviceId, String planId, String appGuid)
            throws ServiceInstanceBindingExistsException, ServiceBrokerException {

        ServiceInstanceBinding binding = getServiceInstanceBinding(bindingId);
        if(binding != null) {
            throw new ServiceInstanceBindingExistsException(binding);
        }

        // basic binding details
        String host = "localhost";
        int port = 3306;
        String dbName = serviceInstance.getId();
        String sysLogDrainURL = "http://drain.eddie.org";

        // create a unique username / password pair for this service binding
        String username =  Utils.generateRandomUsername(5);
        String password = Utils.reverse(username);

        String uri = String.format("jdbc:mysql://%s:%d/%s",
                host,
                port,
                dbName);

        Map<String, Object> creds = new HashMap<String,Object>();
        creds.put("uri", uri);
        creds.put("username", username);
        creds.put("password", password);
        creds.put("host", host);
        creds.put("port", port);

        // compose 'with' clauses
        StringBuilder withClausesBuilder = new StringBuilder();
        Map<String,Integer> planOptions = planDetails.get(planId);
        for(String key : planOptions.keySet()) {
            withClausesBuilder
                    .append(" ")
                    .append(key)
                    .append(" ")
                    .append(planOptions.get(key));
        }
        String withClauses = withClausesBuilder.toString();

        // *** interaction with actual service "provider" to create a service instance binding goes here ***

        try {
            String getPasswordHashSQL = String.format(getPasswordHashSQLTemplate, password);
            String passwordHash = jdbcTemplate.queryForObject(getPasswordHashSQL, String.class);

            logger.info("Got a password hash for " + password + " of: " + passwordHash);

            String createGrantAndUserSQL = String.format(createGrantAndUserSQLTemplate,
                    dbName,
                    username,
                    host,
                    passwordHash,
                    withClauses);
            jdbcTemplate.update(createGrantAndUserSQL);
        }
        catch(Exception e) {
            throw new ServiceBrokerException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }


        // as all is OK, register the created binding in the repo
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
        String deleteGrantAndUserSQL = String .format(deleteGrantAndUserSQLTemplate,
                binding.getCredentials().get("username"),
                binding.getCredentials().get("host"));
        try {
            jdbcTemplate.update(deleteGrantAndUserSQL);
        }
        catch(Exception e) {
            throw new ServiceBrokerException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // as all is OK, remove the binding from the repo
        return serviceRepo.removeBinding(id);
    }

}
