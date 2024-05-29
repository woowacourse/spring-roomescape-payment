package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.exception.PaymentFailureException;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.entity.MemberReservation;

import java.util.Map;

@Service
public class PaymentService {

    private final TossRestClient restClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          TossRestClient restClient
    ) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
    }

    public void confirmPayment(PaymentRequest request, MemberReservation memberReservation) {
        PaymentResponse response = restClient.post("/confirm", request)
                .orElseThrow(() -> new PaymentFailureException("결제를 승인하던 중 오류가 발생했습니다."));

        Payment payment = Payment.of(response, memberReservation);
        paymentRepository.save(payment);
    }

    public void cancelPayment(MemberReservation memberReservation) {
        paymentRepository.findByMemberReservation(memberReservation)
                .ifPresent(payment -> {
                    postCancelPaymentRequest(payment);
                    paymentRepository.delete(payment);
                });
    }

    private void postCancelPaymentRequest(Payment payment) {
        String uri = "/" + payment.getPaymentKey() + "/cancel";
        Map<String, String> body = Map.of("cancelReason", "고객 변심");
        restClient.post(uri, body);
    }
}
