package org.pd.service;

import lombok.Setter;

import java.time.LocalDateTime;

public class TimeProvidertestImpl implements TimeProvider{

    @Setter
    private LocalDateTime currentTime;

    @Override
    public LocalDateTime getLocalDateTime() {
        return currentTime;
    }
}
