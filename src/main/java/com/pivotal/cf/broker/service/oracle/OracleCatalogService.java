package com.pivotal.cf.broker.service.oracle;

import com.pivotal.cf.broker.model.Catalog;
import com.pivotal.cf.broker.model.ServiceDefinition;
import com.pivotal.cf.broker.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the CatalogService that gets the catalog injected (ie configure 
 * in spring config)
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
@Component
public class OracleCatalogService implements CatalogService {

	private Catalog oracleCatalog;
	private Map<String,ServiceDefinition> serviceDefs = new HashMap<String,ServiceDefinition>();
	
	@Autowired
	public OracleCatalogService(Catalog oracleCatalog) {
		this.oracleCatalog = oracleCatalog;
		initializeMap();
	}
	
	private void initializeMap() {
		for (ServiceDefinition def: oracleCatalog.getServiceDefinitions()) {
			serviceDefs.put(def.getId(), def);
		}
	}
	
	@Override
	public Catalog getCatalog() {
		return oracleCatalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return serviceDefs.get(serviceId);
	}

}
