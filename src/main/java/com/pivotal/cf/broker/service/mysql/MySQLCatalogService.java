package com.pivotal.cf.broker.service.mysql;

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
public class MySQLCatalogService implements CatalogService {

	private Catalog mysqlCatalog;
	private Map<String,ServiceDefinition> serviceDefs = new HashMap<String,ServiceDefinition>();
	
	@Autowired
	public MySQLCatalogService(Catalog mysqlCatalog) {
		this.mysqlCatalog = mysqlCatalog;
		initializeMap();
	}
	
	private void initializeMap() {
		for (ServiceDefinition def: mysqlCatalog.getServiceDefinitions()) {
			serviceDefs.put(def.getId(), def);
		}
	}
	
	@Override
	public Catalog getCatalog() {
		return mysqlCatalog;
	}

	@Override
	public ServiceDefinition getServiceDefinition(String serviceId) {
		return serviceDefs.get(serviceId);
	}

}
