package roomescape.payment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import roomescape.exception.PaymentFailureException;
import roomescape.exception.ResourceNotFoundException;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.infrastructure.TossPaymentRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.entity.MemberReservation;

import java.util.Map;

@Service
public class PaymentService {

    private final TossPaymentRestClient restClient;
    private final PaymentRepository paymentRepository;
    private final PaymentKeyEncodingService paymentKeyEncodingService;
    @Value("${toss.url.confirm-payment}")
    private String confirmUrl;
    @Value("${toss.url.cancel-payment}")
    private String cancelUrl;

    public PaymentService(PaymentRepository paymentRepository,
                          TossPaymentRestClient restClient,
                          PaymentKeyEncodingService paymentKeyEncodingService
    ) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
        this.paymentKeyEncodingService = paymentKeyEncodingService;
    }

    public Payment findByMemberReservation(MemberReservation memberReservation) {
        return paymentRepository.findByMemberReservation(memberReservation)
                .orElseThrow(() -> new ResourceNotFoundException("해당 예약은 결제가 존재하지 않습니다."));
    }

    public void confirmPayment(PaymentRequest request, MemberReservation memberReservation) {
        PaymentResponse response = restClient.post(confirmUrl, request)
                .orElseThrow(() -> new PaymentFailureException("결제를 승인하던 중 오류가 발생했습니다."));

        Payment payment = Payment.of(response, memberReservation, paymentKeyEncodingService);
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
        String plainPaymentKey = getPlainPaymentKey(payment);
        String uri = String.format(cancelUrl, plainPaymentKey);
        Map<String, String> body = Map.of("cancelReason", "고객 변심");
        restClient.post(uri, body);
    }

    public String getPlainPaymentKey(Payment payment) {
        return payment.getPaymentKey(paymentKeyEncodingService);
    }
}
