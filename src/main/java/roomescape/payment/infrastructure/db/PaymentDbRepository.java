package roomescape.payment.infrastructure.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.global.exception.ResourceNotFoundException;
import roomescape.payment.model.Payment;
import roomescape.payment.model.PaymentRepository;

@Repository
@RequiredArgsConstructor
public class PaymentDbRepository implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Payment getLatestByReservationId(Long reservationId) {
        return paymentJpaRepository.findTopByReservationIdOrderByCreatedAtDesc(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 예약 ID(id:" + reservationId + ")로 가장 최근 결제 정보를 찾을 수 없습니다."));
    }

    @Override
    public Payment getById(Long id) {
        return paymentJpaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "해당 ID(id:" + id + ")로 가장 최근 결제 정보를 찾을 수 없습니다."));
    }
}
