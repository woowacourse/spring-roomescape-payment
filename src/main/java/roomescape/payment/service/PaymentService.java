package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Base64.Encoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.Member;
import roomescape.member.service.usecase.MemberQueryUseCase;
import roomescape.payment.controller.dto.PaymentVerificationWebRequest;
import roomescape.payment.controller.dto.PaymentVerificationWebResponse;
import roomescape.payment.domain.PaymentVerification;
import roomescape.payment.repository.PaymentVerificationRepository;
import roomescape.payment.service.dto.PaymentConfirmRequest;
import roomescape.payment.service.dto.PaymentConfirmResponse;

@Service
public class PaymentService {

    private static final String SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";

    private final MemberQueryUseCase memberQueryUseCase;
    private final PaymentVerificationRepository paymentVerificationRepository;
    private final RestClient restClient;

    public PaymentService(
            final MemberQueryUseCase memberQueryUseCase,
            final PaymentVerificationRepository paymentVerificationRepository,
            final Builder restClientBuilder
    ) {
        this.memberQueryUseCase = memberQueryUseCase;
        this.paymentVerificationRepository = paymentVerificationRepository;

        restClient = restClientBuilder
                .defaultStatusHandler(new PaymentErrorHandler())
                .defaultHeader("Authorization", getAuthorization())
                .defaultHeader("Content-Type", "application/json")
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .build();
    }

    public PaymentVerificationWebResponse createPrepayment(
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

    public PaymentConfirmResponse confirm(final PaymentConfirmRequest request) {
        return restClient.post()
                .uri("/confirm")
                .body(request)
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }

    private String getAuthorization() {
        return "Basic " + encodeToBase64((SECRET_KEY + ":"));
    }

    private String encodeToBase64(final String value) {
        final Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(value.getBytes(StandardCharsets.UTF_8)));
    }
}
