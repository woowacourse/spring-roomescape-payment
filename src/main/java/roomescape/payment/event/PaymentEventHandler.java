package roomescape.payment.event;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import roomescape.exception.NotFoundException;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.ReservationDeleteEvent;
import roomescape.reservation.service.dto.WaitingApprovedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventHandler {

    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    @EventListener(WaitingApprovedEvent.class)
    public void handleWaitingApproved(WaitingApprovedEvent event) {
        Reservation reservation = reservationRepository.findById(event.reservationId())
                .orElseThrow(() -> new NotFoundException("예약이 존재하지 않습니다, id: " + event.reservationId()));

        paymentService.saveNotPaidPayment(reservation);
    }

    @EventListener(ReservationDeleteEvent.class)
    public void handleReservationDelete(ReservationDeleteEvent event) {
        Optional<Reservation> byId = reservationRepository.findById(event.reservationId());
        System.out.println("byId = " + byId);
        Reservation reservation = reservationRepository.findById(event.reservationId())
                .orElseThrow(() -> new NotFoundException("예약이 존재하지 않습니다, id: " + event.reservationId()));
        paymentService.cancelPayment(reservation.getId());
    }
}
