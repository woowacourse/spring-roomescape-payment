package roomescape.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import roomescape.domain.event.CancelEventPublisher;
import roomescape.domain.reservation.Reservation;

@Component
@RequiredArgsConstructor
public class ReservationCancelEventPublisher implements CancelEventPublisher {
    private final ApplicationEventPublisher publisher;

    @Override
    public void publishPaymentPendingEvent(Reservation reservation) {
        publisher.publishEvent(new PaymentPendingEvent(reservation.getId()));
    }
}
