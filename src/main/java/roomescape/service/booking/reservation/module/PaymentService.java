package roomescape.service.booking.reservation.module;

import org.springframework.stereotype.Service;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.infrastructure.payment.TossPaymentClient;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private final TossPaymentClient tossPaymentClient;

    public PaymentService(final PaymentRepository paymentRepository, TossPaymentClient tossPaymentClient) {
        this.paymentRepository = paymentRepository;
        this.tossPaymentClient = tossPaymentClient;
    }

    public void pay(PaymentRequest paymentRequest, final Reservation reservation) { // TODO: 얘도 트랜잭션 적용되나?
        PaymentResponse paymentResponse = tossPaymentClient.confirm(paymentRequest);
        paymentRepository.save(paymentResponse.toEntity(reservation));
    }
}
