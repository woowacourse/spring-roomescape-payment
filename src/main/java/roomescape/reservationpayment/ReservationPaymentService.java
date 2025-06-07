package roomescape.reservationpayment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.booking.reservation.TossPaymentConfirmCommandFactory;
import roomescape.external.tosspayment.TossPaymentAdapter;
import roomescape.external.tosspayment.dto.TossPaymentConfirmCommand;
import roomescape.reservationpayment.dto.ReservationPaymentRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationPaymentService {

    private final ReservationPaymentRepository reservationPaymentRepository;
    private final TossPaymentConfirmCommandFactory tossPaymentConfirmCommandFactory;
    private final TossPaymentAdapter tossPaymentAdapter;

    @Transactional
    public void confirmPayment(final ReservationPaymentRequest request) {
        ReservationPayment reservationPayment = new ReservationPayment(request.paymentKey(), request.amount(), request.orderId(), request.reservation());
        ReservationPayment savedReservationPayment = reservationPaymentRepository.save(reservationPayment);

        TossPaymentConfirmCommand command = tossPaymentConfirmCommandFactory.toPaymentConfirmCommand(request);
        try {
            tossPaymentAdapter.confirmPayment(command);
        } catch (Exception e) {
            log.error("결제 승인 api 요청 실패", e);
            throw e;
        }
        log.info("[{}] EVENT: PAYMENT_CONFIRMED, reservationId={}, paymentKey={}",
                MDC.get("requestId"),
                savedReservationPayment.getReservation().getId(),
                savedReservationPayment.getPaymentKey());
    }
}
