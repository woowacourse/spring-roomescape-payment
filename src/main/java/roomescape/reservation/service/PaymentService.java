package roomescape.reservation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.error.exception.NotFoundException;
import roomescape.reservation.entity.Payment;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.external.client.PaymentRestClient;
import roomescape.reservation.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRestClient paymentRestClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public void create(Payment payment) {
        paymentRepository.save(payment);
        paymentRestClient.approve(payment);
    }

    public Payment findByReservation(Reservation reservation) {
        return paymentRepository.findByReservationId(reservation.getId())
                .orElseThrow(() -> new NotFoundException("결제 정보를 찾을 수 없습니다."));
    }

    public boolean existsByReservation(Reservation reservation) {
        return paymentRepository.existsByReservationId(reservation.getId());
    }
}
