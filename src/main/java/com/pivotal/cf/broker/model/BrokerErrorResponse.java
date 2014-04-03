package com.pivotal.cf.broker.model;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * The response sent to the cloud controller when a bind
 * request is successful.
 * 
 * @author sgreenberg@gopivotal.com
 */
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class BrokerErrorResponse {

    String description;

	public BrokerErrorResponse() {}

	public BrokerErrorResponse(String description) {
        this.description = description;
	}

	@JsonSerialize
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}
	
}
