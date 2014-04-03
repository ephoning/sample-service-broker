package com.pivotal.cf.broker.repo;

import com.pivotal.cf.broker.model.ServiceInstance;
import com.pivotal.cf.broker.model.ServiceInstanceBinding;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by honine on 3/26/14.
 */
/*
public class PersistedServiceRepoTest {
    @Test
    public void dummy() {

    }
}
*/

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:root-context.xml")
public class PersistedServiceRepoTest {

    @Autowired
    ServiceRepo repo;

    private static ServiceInstance fooInstance = new ServiceInstance(
            "foo",
            "service-def-id",
            "plan-id",
            "organization-guid",
            "space-guid",
            "http://dashboard-url");

    private static ServiceInstance barInstance = new ServiceInstance(
            "bar",
            "service-def-id",
            "plan-id",
            "organization-guid",
            "space-guid",
            "http://dashboard-url");

    private static ServiceInstance abcInstance = new ServiceInstance(
            "abc",
            "service-def-id",
            "plan-id",
            "organization-guid",
            "space-guid",
            "http://dashboard-url");

    private static ServiceInstance xyzInstance = new ServiceInstance(
            "xyz",
            "service-def-id",
            "plan-id",
            "organization-guid",
            "space-guid",
            "http://dashboard-url");

    @After
    public void cleanup() {
        List<ServiceInstanceBinding> bindings = repo.getBindings();
        for(ServiceInstanceBinding b : bindings) {
            repo.removeBinding(b.getId());
        }
        List<ServiceInstance> instances = repo.getInstances();
        for(ServiceInstance i : instances) {
            repo.removeInstance(i.getId());
        }
    }

    @Test
    public void addServiceInstanceTest() {
        repo.addInstance(fooInstance.getId(), fooInstance);
        assertNotNull(repo.getInstance(fooInstance.getId()));
    }

    @Test
    public void unknownServiceInstanceTest() {
        assertNull(repo.getInstance(barInstance.getId()));
    }

    @Test
    public void serviceInstancesCountTest() {
        repo.addInstance(abcInstance.getId(), abcInstance);
        repo.addInstance(xyzInstance.getId(), xyzInstance);
        assertEquals(2, repo.getInstances().size());
    }

    Map<String,Object> creds = new HashMap<String,Object>();

    private static ServiceInstanceBinding fooBinding = new ServiceInstanceBinding(
            "foo-binding",
            "foo",
            null,
            "http://syslog-drain-url",
            "appGuid");

    @Test
    public void addServiceInstanceBindingTest() {
        repo.addInstance(fooInstance.getId(), fooInstance);
        repo.addBinding(fooBinding.getId(), fooBinding);
        assertNotNull(repo.getInstance(fooInstance.getId()));
    }

    @Test(expected = org.springframework.dao.DataIntegrityViolationException.class)
    public void failAddServiceInstanceBindingTest() {
        repo.addBinding(fooBinding.getId(), fooBinding);
    }
}
