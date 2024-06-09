package roomescape.service.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentCancelRequest;
import roomescape.infrastructure.tosspayments.TossPaymentsClient;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;
import roomescape.service.reservation.module.ReservationValidator;

@Service
@Transactional
public class ReservationCancelService {

    private final TossPaymentsClient tossPaymentsClient;
    private final ReservationValidator reservationValidator;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    public ReservationCancelService(TossPaymentsClient tossPaymentsClient,
                                    ReservationValidator reservationValidator,
                                    ReservationRepository reservationRepository,
                                    PaymentRepository paymentRepository
    ) {
        this.tossPaymentsClient = tossPaymentsClient;
        this.reservationValidator = reservationValidator;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findByIdOrThrow(reservationId);

        reservationValidator.validateReservationCancellation(reservation);
        if (paymentRepository.existsByReservationId(reservationId)) {
            cancelPayment(reservation);
        }
        reservationRepository.delete(reservation);
    }

    private void cancelPayment(Reservation reservation) {
        Payment payment = paymentRepository.findByReservationIdOrThrow(reservation.getId());
        tossPaymentsClient.requestPaymentCancel(PaymentCancelRequest.of(payment, "단순 변심"));
        paymentRepository.delete(payment);
    }
}
