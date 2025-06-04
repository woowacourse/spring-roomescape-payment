package roomescape.payment.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.processor.PaymentProcessor;
import roomescape.payment.processor.PaymentType;
import roomescape.payment.repository.PaymentRepositoryInterface;
import roomescape.reservation.domain.Reservation;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final List<PaymentProcessor> processors;
    private final PaymentRepositoryInterface paymentRepository;

    //외부 결제사에 승인 요청
    //응답을 받아 내부 Payment 엔티티 생성
    //DB에 저장
    //=> 이 플로우가 명세되었으면 좋겠음.
    public Payment processPayment(
            final PaymentType type,
            final PaymentConfirmRequest request,
            final Reservation reservation) {
        final PaymentProcessor paymentProcessor = getPaymentProcessor(type);
        final PaymentConfirmResponse response = paymentProcessor.processPayment(request);
        final Payment payment = new Payment(
                response.getPaymentKey(),
                response.getOrderId(),
                response.getTotalAmount(),
                reservation
        );
        final Payment savedPayment = paymentRepository.save(payment);

        return savedPayment;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    private PaymentProcessor getPaymentProcessor(final PaymentType type) {
        return processors.stream()
                .filter(processor -> processor.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 결제 수단입니다."));
    }

    public void deleteByReservationId(final Long reservationId) {
        paymentRepository.deleteByReservationId(reservationId);
    }
}
