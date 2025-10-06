package org.pd.service;

import lombok.Value;

@Value
public class BusinessException extends RuntimeException {

    private String businessMessage;

    public BusinessException(ErrorMessage errorMessage) {
        this.businessMessage = errorMessage.getMessage();
    }
}
