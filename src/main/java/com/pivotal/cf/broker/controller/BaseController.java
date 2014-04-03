package com.pivotal.cf.broker.controller;

import javax.servlet.http.HttpServletResponse;

import com.pivotal.cf.broker.model.BrokerErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pivotal.cf.broker.model.ErrorMessage;
import com.pivotal.cf.broker.exception.ServiceBrokerException;
/**
 * Base controller.
 * 
 * @author sgreenberg@gopivotal.com
 *
 */
public abstract class BaseController {

	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			HttpMessageNotReadableException ex, 
			HttpServletResponse response) {
	    return getErrorResponse(ex.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			MethodArgumentNotValidException ex, 
			HttpServletResponse response) {
	    BindingResult result = ex.getBindingResult();
	    String message = "Missing required fields:";
	    for (FieldError error: result.getFieldErrors()) {
	    	message += " " + error.getField();
	    }
		return getErrorResponse(message, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<ErrorMessage> handleException(
			Exception ex, 
			HttpServletResponse response) {
		logger.warn("Exception", ex);
	    return getErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public ResponseEntity<ErrorMessage> getErrorResponse(String message, HttpStatus status) {
		return new ResponseEntity<ErrorMessage>(new ErrorMessage(message), 
				status);
	}


    /**
     * a standard approach to dealing with broker errors
     * note that the use 'BrokerErrorResponse' results in the wrapping of the actual fault message
     * into a JSON constructs that label that message with 'description'
     * @param ex
     * @param response
     * @return
     */
    @ExceptionHandler(ServiceBrokerException.class)
    @ResponseBody
    public ResponseEntity<BrokerErrorResponse> handleException(
            ServiceBrokerException ex,
            HttpServletResponse response) {
        logger.warn("Service Broker Exception", ex);

        return new ResponseEntity<BrokerErrorResponse>(new BrokerErrorResponse(ex.getMessage()), ex.getStatusCode());
    }

}
