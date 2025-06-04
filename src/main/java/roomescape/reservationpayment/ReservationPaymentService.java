package roomescape.reservationpayment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.booking.reservation.TossPaymentConfirmCommandFactory;
import roomescape.payment.TossPaymentAdapter;
import roomescape.payment.dto.TossPaymentConfirmCommand;
import roomescape.payment.dto.TossPaymentConfirmResponse;
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
        TossPaymentConfirmCommand command = tossPaymentConfirmCommandFactory.toPaymentConfirmCommand(request);
        TossPaymentConfirmResponse response;

        try {
            response = tossPaymentAdapter.confirmPayment(command);
        } catch (Exception e) {
            log.error("결제 승인 실패", e);
            throw e;
        }
        ReservationPayment reservationPayment = new ReservationPayment(response.paymentKey(), response.totalAmount(), response.orderId(), request.reservation());
        reservationPaymentRepository.save(reservationPayment);
    }
}
