package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import roomescape.domain.Payment;
import roomescape.dto.PaymentInfo;
import roomescape.dto.request.MemberReservationRequest;
import roomescape.infrastructure.TossPaymentProperties;
import roomescape.repository.PaymentRepository;

@Service
public class PaymentService {

    private final RestClient restClient;
    private final TossPaymentProperties tossPaymentProperties;
    private final PaymentRepository paymentRepository;

    public PaymentService(RestClient restClient, TossPaymentProperties tossPaymentProperties, PaymentRepository paymentRepository) {
        this.restClient = restClient;
        this.tossPaymentProperties = tossPaymentProperties;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public Payment findPaymentById(long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("[ERROR] 존재하지 않는 결제정보 입니다"));
    }

    @Transactional
    public Long payment(MemberReservationRequest memberReservationRequest) {
        Long amount = memberReservationRequest.amount();
        String orderId = memberReservationRequest.orderId();
        String paymentKey = memberReservationRequest.paymentKey();

        PaymentInfo paymentInfo = new PaymentInfo(amount, orderId, paymentKey);
        Long paymentId = savePayment(paymentInfo);

        restClient.post()
                .uri(tossPaymentProperties.url().confirm())
                .body(paymentInfo)
                .retrieve()
                .toBodilessEntity();

        return paymentId;
    }

    private Long savePayment(PaymentInfo paymentInfo) {
        Payment payment = paymentInfo.toEntity();
        Payment savedPayment = paymentRepository.save(payment);
        return savedPayment.getId();
    }
}
