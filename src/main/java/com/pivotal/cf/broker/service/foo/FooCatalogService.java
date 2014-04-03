package com.pivotal.cf.broker.service.foo;

import java.util.HashMap;
import java.util.Map;

import com.pivotal.cf.broker.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.pivotal.cf.broker.model.Catalog;
import com.pivotal.cf.broker.model.ServiceDefinition;

/**
 * An implementation of the CatalogService that gets the catalog injected (ie configure 
 * in spring config)
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
@Component
public class FooCatalogService implements CatalogService {

	private Catalog fooCatalog;
	private Map<String,ServiceDefinition> serviceDefs = new HashMap<String,ServiceDefinition>();
	
	@Autowired
	public FooCatalogService(Catalog fooCatalog) {
		this.fooCatalog = fooCatalog;
		initializeMap();
	}
	
	private void initializeMap() {
		for (ServiceDefinition def: fooCatalog.getServiceDefinitions()) {
			serviceDefs.put(def.getId(), def);
		}
	}
	
	@Override
	public Catalog getCatalog() {
		return fooCatalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return serviceDefs.get(serviceId);
	}

}
