package roomescape.payment.service.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@RequiredArgsConstructor
@Service
public class PaymentCommandUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentVerificationRepository paymentVerificationRepository;

    private final MemberQueryUseCase memberQueryUseCase;

    public Payment create(final CreatePaymentServiceRequest createPaymentServiceRequest) {
        return paymentRepository.save(Payment.builder()
                .orderId(createPaymentServiceRequest.orderId())
                .paymentKey(createPaymentServiceRequest.paymentKey())
                .amount(createPaymentServiceRequest.amount())
                .build()
        );
    }

    public PaymentVerificationWebResponse createPaymentVerification(
            final String orderId,
            final int amount,
            final Long memberId
    ) {
        final Member member = memberQueryUseCase.get(memberId);
        final PaymentVerification paymentVerification = new PaymentVerification(orderId, amount, member);

        paymentVerificationRepository.save(paymentVerification);

        return new PaymentVerificationWebResponse(orderId, amount, MemberInfo.from(member));
    }
}
