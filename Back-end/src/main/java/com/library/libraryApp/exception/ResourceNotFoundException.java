package com.library.libraryApp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND) //404
public class ResourceNotFoundException extends RuntimeException {

    private String resourceName;
    private String fieldName;
    private Long fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Long fieldValue) {
        super(resourceName + " not found with " + fieldName + " : " + fieldValue);
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
