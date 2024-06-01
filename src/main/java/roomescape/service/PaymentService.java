package roomescape.service;

import static roomescape.exception.ExceptionType.NOT_FOUND_MEMBER;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.dto.ApproveApiResponse;
import roomescape.dto.PaymentApproveRequest;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository,
                          MemberRepository memberRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
    }

    public void approve(PaymentApproveRequest paymentApproveRequest, long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER));
        ApproveApiResponse approve = paymentClient.approve(paymentApproveRequest);
        Payment payment = new Payment(approve.orderId(), approve.paymentKey(), approve.totalAmount());
        paymentRepository.save(payment);
    }
}
