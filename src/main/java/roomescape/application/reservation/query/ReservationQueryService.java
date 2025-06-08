package roomescape.application.reservation.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.application.payment.PaymentQueryService;
import roomescape.application.payment.PaymentResult;
import roomescape.application.reservation.query.dto.ReservationResult;
import roomescape.application.reservation.query.dto.ReservationSearchCondition;
import roomescape.application.reservation.query.dto.ReservationWithStatusAndPaymentResult;
import roomescape.application.reservation.query.dto.ReservationWithStatusResult;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.repository.ReservationRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;
    private final PaymentQueryService paymentQueryService;

    public List<ReservationResult> findAll() {
        final List<Reservation> reservations = reservationRepository.findAllWithMemberAndTimeAndTheme();
        return reservations.stream()
                .map(ReservationResult::from)
                .toList();
    }

    public List<ReservationResult> findReservationsBy(final ReservationSearchCondition reservationSearchCondition) {
        final List<Reservation> reservations = reservationRepository.findByThemeIdAndMemberIdAndDateBetween(
                reservationSearchCondition.themeId(),
                reservationSearchCondition.memberId(),
                reservationSearchCondition.from(),
                reservationSearchCondition.to()
        );
        return reservations.stream()
                .map(ReservationResult::from)
                .toList();
    }

    public List<ReservationWithStatusAndPaymentResult> getReservationsWithStatusAndPayment(final Long memberId) {
        final List<ReservationWithStatusResult> reservationWithStatusResults =
                reservationRepository.findAllByMemberId(memberId)
                        .stream()
                        .map(ReservationWithStatusResult::from)
                        .toList();

        final List<Long> reservationIds = reservationWithStatusResults.stream()
                .map(ReservationWithStatusResult::reservationId)
                .toList();

        final Map<Long, PaymentResult> paymentResultByReservationId =
                paymentQueryService.getAllPaymentResultsByReservationIds(reservationIds);

        return reservationWithStatusResults.stream().
                map(reservationWithStatusResult -> ReservationWithStatusAndPaymentResult.from(
                        reservationWithStatusResult,
                        paymentResultByReservationId.get(reservationWithStatusResult.reservationId())))
                .toList();
    }
}
