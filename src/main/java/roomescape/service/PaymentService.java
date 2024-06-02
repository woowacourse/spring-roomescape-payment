package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;
import roomescape.infra.PaymentClient;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public PaymentService(
            PaymentClient paymentClient,
            ReservationRepository reservationRepository,
            PaymentRepository paymentRepository
    ) {
        this.paymentClient = paymentClient;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentResponse payReservation(PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new RoomescapeException(ExceptionType.NOT_FOUND_RESERVATION));

        Payment payment = paymentClient.requestPaymentApproval(request);
        Payment saved = paymentRepository.save(payment);
        reservation.updatePayment(payment);

        return new PaymentResponse(saved.getId(), saved.getPaymentKey(), saved.getOrderId(), saved.getAmount());
    }
}
