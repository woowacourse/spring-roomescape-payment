package roomescape.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import roomescape.exception.NotFoundException;
import roomescape.payment.service.PaymentEventProcessor;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.dto.ReservationDeleteEvent;
import roomescape.reservation.service.dto.WaitingApprovedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final ReservationRepository reservationRepository;
    private final PaymentEventProcessor paymentEventProcessor;

    @EventListener(WaitingApprovedEvent.class)
    public void handleWaitingApproved(WaitingApprovedEvent event) {
        log.info("WaitingApprovedEvent 수신 - reservationId={}", event.reservationId());

        Reservation reservation = getReservationById(event.reservationId());

        log.info("NotPaid 결제 저장 시작 - reservationId={}", reservation.getId());
        paymentEventProcessor.saveNotPaidPayment(reservation);
    }

    @EventListener(ReservationDeleteEvent.class)
    public void handleReservationDelete(ReservationDeleteEvent event) {
        log.info("ReservationDeleteEvent 수신 - reservationId={}", event.reservationId());

        Reservation reservation = getReservationById(event.reservationId());

        log.info("결제 취소 처리 시작 - reservationId={}", reservation.getId());
        paymentEventProcessor.cancelPayment(reservation.getId());
    }

    private Reservation getReservationById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("예약 조회 실패 - reservationId={}", reservationId);
                    return new NotFoundException("예약이 존재하지 않습니다, id: " + reservationId);
                });
    }
}
