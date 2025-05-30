package roomescape.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.impl.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.TossConfirmRequest;
import roomescape.payment.application.dto.TossConfirmResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentGateway;
import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;

@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final TossPaymentGatewayClient tossPaymentGatewayClient;

    public PaymentService(final PaymentRepository paymentRepository,
        final ReservationTimeRepository reservationTimeRepository,
        final ThemeRepository themeRepository,
        final MemberRepository memberRepository,
        final TossPaymentGatewayClient tossPaymentGatewayClient) {
        this.paymentRepository = paymentRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.tossPaymentGatewayClient = tossPaymentGatewayClient;
    }

    @Transactional
    public Payment addPayment(
        final PaymentRequest request,
        final Long memberId
    ) {
        TossConfirmResponse response = tossPaymentGatewayClient.processPaymentConfirm(
            new TossConfirmRequest(request.paymentKey(), request.orderId(), request.amount()));
        ReservationTime reservationTime = getReservationTime(request.timeId());
        Member member = getMember(memberId);
        Theme theme = getTheme(request.themeId());
        Payment payment = new Payment(member, reservationTime, theme, request.date(),
            new PaymentInfo(response.paymentKey(), response.orderId(), response.easyPay().amount()),
            PaymentGateway.TOSS_PAYMENTS);
        return paymentRepository.save(payment);
    }

    private ReservationTime getReservationTime(final Long timeId) {
        return reservationTimeRepository.findById(timeId)
            .orElseThrow(() -> new NotFoundException("선택한 예약 시간이 존재하지 않습니다."));
    }

    private Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new NotFoundException("선택한 멤버가 존재하지 않습니다."));
    }

    private Theme getTheme(final Long themeId) {
        return themeRepository.findById(themeId)
            .orElseThrow(() -> new NotFoundException("선택한 테마가 존재하지 않습니다."));
    }
}
