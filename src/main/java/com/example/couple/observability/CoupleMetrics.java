package com.example.couple.observability;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class CoupleMetrics {

    private final Counter invitationAcceptedCounter;
    private final Counter invitationRejectedCounter;
    private final Counter invitationAcceptFailedCounter;

    public CoupleMetrics(MeterRegistry meterRegistry) {
        this.invitationAcceptedCounter = Counter.builder("couple.invitation.accepted")
                .description("Total number of accepted invitations")
                .tag("domain", "couple")
                .register(meterRegistry);

        this.invitationRejectedCounter = Counter.builder("couple.invitation.rejected")
                .description("Total number of rejected invitations")
                .tag("domain", "couple")
                .register(meterRegistry);

        this.invitationAcceptFailedCounter = Counter.builder("couple.invitation.accept.failed")
                .description("Total number of failed accept attempts")
                .tag("domain", "couple")
                .register(meterRegistry);
    }

    public void invitationAccepted() {
        invitationAcceptedCounter.increment();
    }

    public void invitationRejected() {
        invitationRejectedCounter.increment();
    }

    public void invitationAcceptFailed() {
        invitationAcceptFailedCounter.increment();
    }
}
