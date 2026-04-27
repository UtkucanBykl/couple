package com.example.couple.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserMetrics {
    private final Counter loginPasswordUnmatchCounter;

    public UserMetrics(MeterRegistry registry) {
        loginPasswordUnmatchCounter = Counter.builder(
                "login.passowrd-unmatch"
        ).description("Invalid password").register(registry);
    }

    public void increaseLoginPasswordUnmatch(){
        loginPasswordUnmatchCounter.increment();
    }

}
