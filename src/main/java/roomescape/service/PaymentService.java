package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;
import roomescape.infra.PaymentRestClient;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
public class PaymentService {

    private final PaymentRestClient paymentRestClient;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            PaymentRestClient paymentRestClient,
            ReservationRepository reservationRepository,
            PaymentRepository paymentRepository
    ) {
        this.paymentRestClient = paymentRestClient;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse savePaymentAndUpdateReservationStatus(PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new RoomescapeException(ExceptionType.NOT_FOUND_RESERVATION));

        Payment payment = paymentRestClient.requestPaymentApproval(request);
        Payment saved = paymentRepository.save(payment);
        reservation.updatePayment(payment);

        return new PaymentResponse(saved.getId(), saved.getPaymentKey(), saved.getOrderId(), saved.getAmount());
    }
}
