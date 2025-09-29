package org.example.service;

import lombok.Value;

@Value
public class BusinessException extends RuntimeException {

    private String businessMessage;

}
