package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.config.TossPaymentRestClient;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
public class PaymentService {

    private final TossPaymentRestClient tossPaymentRestClient;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            TossPaymentRestClient tossPaymentRestClient,
            ReservationRepository reservationRepository,
            PaymentRepository paymentRepository
    ) {
        this.tossPaymentRestClient = tossPaymentRestClient;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }


    // TODO: 이름
    @Transactional
    public PaymentResponse askPayment(PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new RoomescapeException(ExceptionType.NOT_FOUND_RESERVATION));

        Payment payment = tossPaymentRestClient.pay(request);
        Payment saved = paymentRepository.save(payment);
        reservation.updatePayment(payment);

        return new PaymentResponse(saved.getId(), saved.getPaymentKey(), saved.getOrderId(), saved.getAmount());
    }
}
