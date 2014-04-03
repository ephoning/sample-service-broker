package com.pivotal.cf.broker.controller.foo;

import com.pivotal.cf.broker.controller.BaseController;
import com.pivotal.cf.broker.controller.CatalogController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.pivotal.cf.broker.model.Catalog;
import com.pivotal.cf.broker.repo.ServiceRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by honine on 3/19/14.
 */
@Controller
public class FooServiceController  extends BaseController {

    public static final String BASE_PATH = "/foo";

    private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);

    private ServiceRepo serviceRepo;

    @Autowired
    public FooServiceController(ServiceRepo serviceRepo) {
        this.serviceRepo = serviceRepo;
    }

    @RequestMapping(value = BASE_PATH + "/{serviceInstanceId}", method = RequestMethod.GET)
    public @ResponseBody String execFooService(@PathVariable("serviceInstanceId") String serviceInstanceId, HttpServletRequest request, HttpServletResponse response) {
        logger.info("GET: " + BASE_PATH + ", execFooService() for: " + serviceInstanceId);

        if(serviceRepo.getInstance(serviceInstanceId) != null) {
            if(serviceRepo.isInstanceBound(serviceInstanceId)) {
                return "You just accessed the FOO service 'instance': " + serviceInstanceId;
            }
            else {
                return "You just tried to access a non-bound FOO service 'instance': " + serviceInstanceId;
            }
        }
        else {
            return "You just tried to access a non-existent FOO service 'instance': " + serviceInstanceId;
        }
    }

}
