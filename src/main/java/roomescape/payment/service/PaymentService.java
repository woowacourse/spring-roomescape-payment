package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.common.exception.PaymentException;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.Member;
import roomescape.member.service.usecase.MemberQueryUseCase;
import roomescape.payment.controller.dto.PaymentVerificationWebRequest;
import roomescape.payment.controller.dto.PaymentVerificationWebResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentVerification;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.repository.PaymentVerificationRepository;
import roomescape.payment.service.dto.CreatePaymentServiceRequest;
import roomescape.payment.service.dto.PaymentConfirmRequest;
import roomescape.payment.service.dto.PaymentConfirmResponse;

@Service
public class PaymentService {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final MemberQueryUseCase memberQueryUseCase;
    private final RestClient restClient;

    private final PaymentRepository paymentRepository;
    private final PaymentVerificationRepository paymentVerificationRepository;

    public PaymentService(
            final MemberQueryUseCase memberQueryUseCase,
            final PaymentVerificationRepository paymentVerificationRepository,
            final PaymentRepository paymentRepository,
            final Builder restClientBuilder
    ) {
        this.memberQueryUseCase = memberQueryUseCase;
        this.paymentVerificationRepository = paymentVerificationRepository;
        this.paymentRepository = paymentRepository;

        restClient = restClientBuilder
                .defaultStatusHandler(new PaymentErrorHandler())
                .defaultHeader("Authorization", getAuthorization())
                .defaultHeader("Content-Type", "application/json")
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }

    public PaymentVerificationWebResponse createPaymentVerification(
            final PaymentVerificationWebRequest paymentVerificationWebRequest,
            final Long memberId
    ) {
        final Member member = memberQueryUseCase.get(memberId);

        paymentVerificationRepository.save(new PaymentVerification(
                paymentVerificationWebRequest.orderId(),
                paymentVerificationWebRequest.amount(),
                member
        ));

        return new PaymentVerificationWebResponse(
                paymentVerificationWebRequest.orderId(),
                paymentVerificationWebRequest.amount(),
                MemberInfo.from(member)
        );
    }

    public Payment create(final CreatePaymentServiceRequest createPaymentServiceRequest) {
        return paymentRepository.save(Payment.builder()
                .orderId(createPaymentServiceRequest.orderId())
                .paymentKey(createPaymentServiceRequest.paymentKey())
                .amount(createPaymentServiceRequest.amount())
                .build()
        );
    }

    public PaymentConfirmResponse confirm(
            final PaymentConfirmRequest request,
            final Long memberId
    ) {
        validatePaymentConfirm(request, memberId);

        return restClient.post()
                .uri("/confirm")
                .body(request)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }

    private void validatePaymentConfirm(
            final PaymentConfirmRequest request,
            final Long memberId
    ) {
        final PaymentVerification paymentVerification = getPaymentVerificationByOrderId(request.orderId());

        if (!paymentVerification.isSamePayment(
                request.orderId(),
                request.amount(),
                memberId)
        ) {
            throw new PaymentException(HttpStatus.BAD_REQUEST, "잘못된 결제 승인 요청입니다.");
        }
    }

    private PaymentVerification getPaymentVerificationByOrderId(final String orderId) {
        final List<PaymentVerification> paymentVerifications = paymentVerificationRepository.findByOrderId(orderId);

        if (paymentVerifications.isEmpty()) {
            throw new PaymentException(HttpStatus.NOT_FOUND, "orderId가 존재하지 않습니다.");
        }

        return paymentVerifications.getFirst();
    }

    private String getAuthorization() {
        return "Basic " + encodeToBase64((SECRET_KEY + ":"));
    }

    private String encodeToBase64(final String value) {
        final Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(value.getBytes(StandardCharsets.UTF_8)));
    }
}
