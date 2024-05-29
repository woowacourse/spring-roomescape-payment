package roomescape.application;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.payment.PaymentRequest;
import roomescape.application.dto.request.reservation.ReservationRequest;
import roomescape.application.dto.request.reservation.ReservationSearchCondition;
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
    public synchronized Reservation saveReservation(ReservationRequest request) {
        Member member = memberRepository.getById(request.memberId());
        ReservationDetail reservationDetail = reservationDetailFactory.createReservationDetail(
                request.date(), request.timeId(), request.themeId());
        Reservation reservation = reservationFactory.createReservation(reservationDetail, member);
        Reservation savedReservation = reservationRepository.save(reservation);

        if (savedReservation.isReserved()) {
            PaymentRequest paymentRequest = request.toPaymentRequest();
            PaymentResponse paymentResponse = paymentRestClient.confirm(paymentRequest);
            Payment payment = paymentRepository.save(paymentResponse.toPayment());
            savedReservation.setPayment(payment);
        }

        return savedReservation;
    }

    public List<ReservationResponse> findAllReservationsWithoutCancel() {
        List<Reservation> reservations = reservationRepository.findAllByStatus(Status.RESERVED);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> findAllReservationByConditions(ReservationSearchCondition condition) {
        List<Reservation> reservations = reservationRepository.findByPeriodAndThemeAndMember(
                condition.start(), condition.end(), condition.memberId(), condition.themeId());
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<UserReservationResponse> findAllWithRank(Long memberId) {
        List<ReservationWithRank> reservationWithRanks = reservationRepository.findWithRank(memberId);

        List<UserReservationResponse> reservations = reservationWithRanks.stream()
                .filter(ReservationWithRank::isReserved)
                .map(ReservationWithRank::reservation)
                .map(UserReservationResponse::from)
                .toList();

        List<UserReservationResponse> waitings = reservationWithRanks.stream()
                .filter(ReservationWithRank::isWaiting)
                .map(UserReservationResponse::from)
                .toList();

        return Stream.concat(reservations.stream(), waitings.stream())
                .toList();
    }

    public List<ReservationResponse> findAllWaitings() {
        List<Reservation> reservations = reservationRepository.findAllByStatus(Status.WAITING);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
