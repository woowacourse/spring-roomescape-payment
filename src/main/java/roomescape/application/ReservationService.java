package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.request.reservation.ReservationPaymentRequest;
import roomescape.application.dto.request.reservation.ReservationRequest;
import roomescape.application.dto.request.reservation.ReservationSearchCondition;
import roomescape.application.dto.request.reservation.UserReservationRequest;
import roomescape.application.dto.response.payment.PaymentResponse;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.application.dto.response.reservation.UserReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationFactory;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationWithRank;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailFactory;
import roomescape.exception.AuthenticationException;
import roomescape.exception.AuthorizationException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReservationFactory reservationFactory;
    private final ReservationDetailFactory reservationDetailFactory;
    private final PaymentClient paymentRestClient;
    private final PaymentRepository paymentRepository;

    @Transactional
    public ReservationResponse saveReservation(UserReservationRequest request, Long memberId) {
        Reservation reservation = saveReservation(memberId, request.date(), request.timeId(), request.themeId());
        if (reservation.isPaymentPending()) {
            Payment payment = savePayment(request.toPaymentRequest());
            reservation.completePayment(payment);
        }
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse saveReservationByAdmin(ReservationRequest request) {
        Reservation reservation = saveReservation(
                request.memberId(),
                request.date(),
                request.timeId(),
                request.themeId());
        reservation.toReserved();
        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse paymentForPending(ReservationPaymentRequest request, Long memberId) {
        Reservation reservation = reservationRepository.getReservation(request.reservationId());
        rejectIfNotOwner(reservation, memberId);
        Payment payment = savePayment(request.toPaymentRequest());
        reservation.completePayment(payment);
        return ReservationResponse.from(reservation);
    }

    private Reservation saveReservation(Long memberId, LocalDate date, Long timeId, Long themeId) {
        Member member = memberRepository.findMember(memberId).orElseThrow(AuthenticationException::new);
        ReservationDetail reservationDetail = reservationDetailFactory.createReservationDetail(date, timeId, themeId);
        Reservation reservation = reservationFactory.createReservation(reservationDetail, member);
        return reservationRepository.save(reservation);
    }

    private Payment savePayment(PaymentRequest paymentRequest) {
        PaymentResponse paymentResponse = paymentRestClient.confirmPayment(paymentRequest);
        return paymentRepository.save(paymentResponse.toPayment());
    }

    private void rejectIfNotOwner(Reservation reservation, Long memberId) {
        if (reservation.isNotOwner(memberId)) {
            throw new AuthorizationException();
        }
    }

    public List<ReservationResponse> findAllReservedReservations() {
        List<Reservation> reservations = reservationRepository.findAll(Status.RESERVED);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findAllReservationByConditions(ReservationSearchCondition condition) {
        List<Reservation> reservations = reservationRepository.findReservation(
                condition.start(), condition.end(), condition.memberId(), condition.themeId());
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<UserReservationResponse> findAllWithRank(Long memberId) {
        List<ReservationWithRank> reservationWithRanks = reservationRepository.findWithRank(memberId);

        return reservationWithRanks.stream()
                .map(UserReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findAllWaitings() {
        List<Reservation> reservations = reservationRepository.findAll(Status.WAITING);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
