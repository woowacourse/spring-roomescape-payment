package roomescape.service.query;

import org.springframework.stereotype.Service;
import roomescape.domain.payment.Payment;
import roomescape.dto.payment.PaymentResponseDto;
import roomescape.exception.NotFoundException;
import roomescape.repository.JpaPaymentRepository;

@Service
public class PaymentQueryService {

    private final JpaPaymentRepository paymentRepository;

    public PaymentQueryService(JpaPaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponseDto findByReservationId(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new NotFoundException("해당하는 결제 정보를 찾을 수 없습니다."));
        return new PaymentResponseDto(payment.getPaymentKey(), payment.getOrderId(), payment.getTotalAmount());
    }
}
