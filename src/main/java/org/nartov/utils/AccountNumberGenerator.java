package org.nartov.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class AccountNumberGenerator {

    private final AtomicLong counter = new AtomicLong(10000);

    public Long getNextAccountNumber() {
        return counter.getAndIncrement();
    }
}
