package com.pivotal.cf.broker.service.oracle;

import com.pivotal.cf.broker.exception.ServiceBrokerException;
import com.pivotal.cf.broker.exception.ServiceInstanceDoesNotExistException;
import com.pivotal.cf.broker.exception.ServiceInstanceExistsException;
import com.pivotal.cf.broker.exception.ServiceInstanceIsBoundException;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.repo.ServiceRepo;
import com.pivotal.cf.broker.service.ServiceInstanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A simple service instance service that creates and deletes Oracle databases/schemas
 *
 * Created by honine on 3/13/14.
 */
@Service
public class OracleServiceInstanceService implements ServiceInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(OracleServiceInstanceService.class);

    // TODO: REPLACE WITH THE ORACLE EQUIVALENTS!!!
    private static final String createSchemaSQLTemplate = "CREATE SCHEMA %s";
    private static final String deleteSchemaSQLTemplate = "DROP SCHEMA %s";

    @Autowired
    private ServiceRepo serviceRepo;

    @Autowired
    private JdbcTemplate oracleJdbcTemplate;

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

        // *** interaction with actual service "provider" to create the service instance ***
        try {
            String createSchemaSQL = String.format(createSchemaSQLTemplate, serviceInstanceId);
            oracleJdbcTemplate.update(createSchemaSQL);
        }
        catch (Exception e) {
            throw new ServiceBrokerException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // instance creation succeeded; reflect this in the broker state:
        instance = new ServiceInstance(
                serviceInstanceId,
                service.getId(),
                planId,
                organizationGuid,
                spaceGuid,
                "http://mysql.eddie.org/my-dashboard-url"); // note: fake URL

        // as all is OK, add the instance to the repo
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

        // interact with actual service "provider" to delete the service instance:
        try {
            String deleteSchemaSQL = String.format(deleteSchemaSQLTemplate, instance.getId());
            oracleJdbcTemplate.update(deleteSchemaSQL);
        }
        catch(Exception e) {
            throw new ServiceBrokerException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // as all is OK, remove the instance from the repo
        return serviceRepo.removeInstance(id);
    }
}
