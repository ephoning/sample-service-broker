package com.pivotal.cf.broker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.pivotal.cf.broker.model.Catalog;
import com.pivotal.cf.broker.service.CatalogService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * See: Source: http://docs.cloudfoundry.com/docs/running/architecture/services/writing-service.html
 * 
 * @author sgreenberg@gopivotal.com
 */
@Controller
public class CatalogController extends BaseController {

    public static final float SUPPORTED_VERSION = 2.1F;

	public static final String BASE_PATH = "/v2/catalog";
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
	
	private CatalogService catalogService;
	
	@Autowired 
	public CatalogController(CatalogService catalogService) {
		this.catalogService = catalogService;
	}
	
	@RequestMapping(value = BASE_PATH, method = RequestMethod.GET)
    public @ResponseBody Catalog getCatalog(@RequestHeader(value = "X-Broker-Api-Version") String version,
                                            HttpServletRequest request, HttpServletResponse response) {
		logger.info("GET: " + BASE_PATH + ", getCatalog() + api version: " + version);

        if(Float.parseFloat(version) != SUPPORTED_VERSION) {
            response.setStatus(HttpServletResponse.SC_PRECONDITION_FAILED);
            return null;
        }
        else {
    		return catalogService.getCatalog();
        }
	}
	
}
