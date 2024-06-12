package roomescape.infra.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import roomescape.domain.event.TimeoutEventPublisher;
import roomescape.domain.reservation.Reservation;

@Component
@RequiredArgsConstructor
public class PaymentTimeoutEventPublisher implements TimeoutEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishTimeoutEvent(Reservation reservation) {
        publisher.publishEvent(new PaymentTimeoutEvent(this, reservation.getId()));
    }
}
