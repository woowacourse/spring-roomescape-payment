package roomescape.core.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.core.domain.Member;
import roomescape.core.domain.Payment;
import roomescape.core.domain.PaymentStatus;
import roomescape.core.domain.Reservation;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.repository.MemberRepository;
import roomescape.core.repository.PaymentRepository;
import roomescape.core.repository.ReservationRepository;
import roomescape.infrastructure.PaymentClient;

@Service
public class PaymentService {
    protected static final String MEMBER_NOT_FOUND_EXCEPTION_MESSAGE = "존재하지 않는 회원입니다.";
    protected static final String RESERVATION_NOT_FOUND_EXCEPTION_MESSAGE = "존재하지 않는 예약입니다.";
    protected static final String PAYMENT_NOT_FOUND_EXCEPTION_MESSAGE = "해당 예약의 결제 내역이 존재하지 않습니다.";

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final PaymentClient paymentClient;

    public PaymentService(final PaymentRepository paymentRepository, final ReservationRepository reservationRepository,
                          final MemberRepository memberRepository, final PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.memberRepository = memberRepository;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(final ReservationResponse reservationResponse,
                                                 final ReservationPaymentRequest request,
                                                 final LoginMember loginMember) {
        final Reservation reservation = getReservation(reservationResponse.getId());
        final Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION_MESSAGE));

        final PaymentRequest paymentRequest = new PaymentRequest(reservation.getId(), request);
        return getPaymentConfirmResponse(paymentRequest, reservation, member);
    }

    @Transactional
    public PaymentConfirmResponse confirmPayment(final PaymentRequest request, final LoginMember loginMember) {
        final Reservation reservation = getReservation(request.getReservationId());
        final Member member = memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION_MESSAGE));

        return getPaymentConfirmResponse(request, reservation, member);
    }

    private Reservation getReservation(final Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException(RESERVATION_NOT_FOUND_EXCEPTION_MESSAGE));
    }

    private PaymentConfirmResponse getPaymentConfirmResponse(final PaymentRequest paymentRequest,
                                                             final Reservation reservation, final Member member) {
        final PaymentConfirmResponse response = paymentClient.getPaymentConfirmResponse(paymentRequest);
        final Payment payment = new Payment(reservation, member, response.getPaymentKey(), response.getOrderId(),
                response.getTotalAmount(), PaymentStatus.CONFIRMED);

        paymentRepository.save(payment);
        return response;
    }

    @Transactional
    public void cancel(final long id) {
        final Reservation reservation = getReservation(id);

        if (paymentRepository.existsByReservation(reservation)) {
            final Payment payment = paymentRepository.findByReservation(reservation)
                    .orElseThrow(() -> new IllegalArgumentException(PAYMENT_NOT_FOUND_EXCEPTION_MESSAGE));

            paymentClient.getPaymentCancelResponse(payment.getPaymentKey());
            payment.cancel();
        }
    }
}
