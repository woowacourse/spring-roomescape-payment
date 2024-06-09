package roomescape.service;

import static roomescape.exception.ExceptionType.NOT_FOUND_MEMBER;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
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
        if (memberRepository.findById(memberId).isEmpty()) {
            throw new RoomescapeException(NOT_FOUND_MEMBER);
        }
        Payment payment = paymentClient.approve(paymentApproveRequest);
        paymentRepository.save(payment);
    }
}
