package roomescape.service;

import static roomescape.exception.ExceptionType.NOT_FOUND_MEMBER;
import static roomescape.exception.ExceptionType.NOT_FOUND_RESERVATION;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Reservation;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.dto.ApproveApiResponse;
import roomescape.dto.PaymentApproveRequest;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;
import roomescape.repository.ReservationRepository;

@Service
@Transactional
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository,
                          MemberRepository memberRepository, ReservationRepository reservationRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
        this.reservationRepository = reservationRepository;
    }

    public void approve(PaymentApproveRequest paymentApproveRequest, long memberId) {
        memberRepository.findById(memberId).orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER));
        Reservation reservation = reservationRepository.findById(paymentApproveRequest.reservationId())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_RESERVATION));
        ApproveApiResponse approve = paymentClient.approve(paymentApproveRequest);
        Payment payment = new Payment(approve.paymentKey(), approve.totalAmount(), reservation);
        paymentRepository.save(payment);
    }
}
