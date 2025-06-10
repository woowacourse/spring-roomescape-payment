package roomescape.payment.application;

import static roomescape.reservation.domain.PaymentStatus.SUCCESS;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.impl.NotFoundException;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentGateway;
import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.repository.ReservationRepository;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossPaymentGatewayClient tossPaymentGatewayClient;
    private final ReservationRepository reservationRepository;

    public PaymentService(
        final PaymentRepository paymentRepository,
        final TossPaymentGatewayClient tossPaymentGatewayClient,
        final ReservationRepository reservationRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentGatewayClient = tossPaymentGatewayClient;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public PaymentResponse addPayment(final PaymentRequest request) {
        TossConfirmResponse response = tossPaymentGatewayClient.processPaymentConfirm(
            new TossConfirmRequest(request.paymentKey(), request.orderId(), request.amount()));
        Reservation reservation = getReservation(request.reservationId());
        Payment payment = new Payment(
            reservation,
            new PaymentInfo(response.paymentKey(), response.orderId(), response.easyPay().amount()),
            PaymentGateway.TOSS_PAYMENTS);
        Payment saved = paymentRepository.save(payment);
        reservation.changePaymentStatus(SUCCESS);
        return PaymentResponse.from(saved);
    }

    private Reservation getReservation(final Long reservationId) {
        return reservationRepository.findById(reservationId)
            .orElseThrow(() -> new NotFoundException("찾으려는 예약이 존재하지 않습니다."));
    }
}
