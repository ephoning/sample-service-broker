package com.pivotal.cf.broker.repo;

import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import com.pivotal.cf.broker.util.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;

import com.pivotal.cf.broker.repo.ServiceRepo;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * An approach to maintaining the broker's state on a persistent store (RDBMS)
 *
 * It is vital that an implementation like this is used for "real world" broker
 * state representation, as this provides the means for multiple broker instances to
 * share a single state representation
 * Using PCF itself to deploy service broker instances provides us with HA and scalability
 *
 * Taking a "raw" Spring JDBC template approach allows for full control of
 * in memory to DB representation of data; this is needed to allow us to pick a
 * "generic" (JSON) approach to the representation of the key/value set that makes up a
 *  'credentials' instance.
 *  Note the methods to convert between Map and JSON string representations
 *
 * Created by honine on 3/20/14.
 */
@Component
public class PersistedServiceRepo implements ServiceRepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Service Instance Queries
    private static final String addInstanceSQL = "INSERT INTO ServiceInstance " +
            "(SERVICE_INSTANCE_ID, SERVICE_ID, PLAN_ID, ORGANIZATION_GUID, SPACE_GUID, DASHBOARD_URL) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String removeInstanceSQL = "DELETE FROM ServiceInstance " +
            "WHERE SERVICE_INSTANCE_ID = ?";
    private static final String getInstanceSQL = "SELECT * FROM ServiceInstance " +
            "WHERE SERVICE_INSTANCE_ID = ?";
    private static final String getInstancesSQL = "SELECT * FROM ServiceInstance";

    // Service Instance Binding Queries
    private static final String addBindingSQL = "INSERT INTO ServiceInstanceBinding" +
            "(SERVICE_INSTANCE_BINDING_ID, SERVICE_INSTANCE_ID, CREDENTIALS, SYSLOG_DRAIN_URL, APP_GUID) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String removeBindingSQL = "DELETE FROM ServiceInstanceBinding " +
            "WHERE SERVICE_INSTANCE_BINDING_ID = ?";
    private static final String getBindingSQL = "SELECT * FROM ServiceInstanceBinding " +
            "WHERE SERVICE_INSTANCE_BINDING_ID = ?";
    private static final String getBindingsSQL = "SELECT * FROM ServiceInstanceBinding";

    private static final String isBoundSQL = "SELECT COUNT(*) FROM ServiceInstanceBinding " +
            "WHERE SERVICE_INSTANCE_ID = ?";

    /*
     * Service Instances CRUD
     */
    @Override
    public ServiceInstance addInstance(String key, ServiceInstance instance) {
        jdbcTemplate.update(addInstanceSQL, new Object[] {
                key,
                instance.getServiceDefinitionId(),
                instance.getPlanId(),
                instance.getOrganizationGuid(),
                instance.getSpaceGuid(),
                instance.getDashboardUrl()
        });
        return instance;
    }

    @Override
    public ServiceInstance removeInstance(String key) {
        ServiceInstance instance = getInstance(key);
        jdbcTemplate.update(removeInstanceSQL, new Object[]{key});
        return instance;
    }

    @Override
    public ServiceInstance getInstance(String key) {
        ServiceInstance instance = null;
        try {
            instance = jdbcTemplate.queryForObject(getInstanceSQL, new Object[] {key}, new ServiceInstanceRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            // finding nothing is fine
        }
        return instance;
    }

    @Override
    public List<ServiceInstance> getInstances() {
        return jdbcTemplate.query(getInstancesSQL, new ServiceInstanceRowMapper());
    }

    @Override
    public boolean isInstanceBound(String key) {
        return jdbcTemplate.queryForObject(isBoundSQL, Integer.class, key) > 0;
    }

    private class ServiceInstanceRowMapper implements RowMapper<ServiceInstance> {
        @Override
        public ServiceInstance mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ServiceInstance(
                    rs.getString("SERVICE_INSTANCE_ID"),
                    rs.getString("SERVICE_ID"),
                    rs.getString("PLAN_ID"),
                    rs.getString("ORGANIZATION_GUID"),
                    rs.getString("SPACE_GUID"),
                    rs.getString("DASHBOARD_URL"));
        }
    }

    /*
     * Service Instance Bindings CRUD
     */

    @Override
    public ServiceInstanceBinding addBinding(String key, ServiceInstanceBinding binding) {
        jdbcTemplate.update(addBindingSQL, new Object[] {
                key,
                binding.getServiceInstanceId(),
                Utils.mapToJson(binding.getCredentials()),
                binding.getSyslogDrainUrl(),
                binding.getAppGuid()
        });
        return binding;
    }

    @Override
    public ServiceInstanceBinding removeBinding(String key) {
        ServiceInstanceBinding binding = getBinding(key);
        jdbcTemplate.update(removeBindingSQL, new Object[]{key});
        return binding;
    }

    @Override
    public ServiceInstanceBinding getBinding(String key) {
        ServiceInstanceBinding binding = null;
        try {
            binding = jdbcTemplate.queryForObject(getBindingSQL, new Object[]{key}, new ServiceInstanceBindingRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            // finding nothing is fine
        }
        return binding;
    }

    @Override
    public List<ServiceInstanceBinding> getBindings() {
        return jdbcTemplate.query(getBindingsSQL, new ServiceInstanceBindingRowMapper());
    }

    private class ServiceInstanceBindingRowMapper implements RowMapper<ServiceInstanceBinding> {
        @Override
        public ServiceInstanceBinding mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ServiceInstanceBinding(
                    rs.getString("SERVICE_INSTANCE_BINDING_ID"),
                    rs.getString("SERVICE_INSTANCE_ID"),
                    Utils.jsonToMap(rs.getString("CREDENTIALS")),
                    rs.getString("SYSLOG_DRAIN_URL"),
                    rs.getString("APP_GUID"));
        }
    }

}
