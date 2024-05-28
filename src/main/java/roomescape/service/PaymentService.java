package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.payment.PaymentDto;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(final PaymentClient paymentClient,
                          final PaymentRepository paymentRepository,
                          final ReservationRepository reservationRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public void confirmPayment(final PaymentDto paymentDto, final Long reservationId) {
        paymentClient.confirmPayment(paymentDto);
        final Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(reservationId + "에 해당하는 예약이 없습니다."));
        final Payment payment = paymentDto.toPayment(reservation);
        paymentRepository.save(payment);
    }
}
