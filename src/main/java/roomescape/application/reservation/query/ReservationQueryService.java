package roomescape.application.reservation.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.reservation.query.dto.ReservationResult;
import roomescape.application.reservation.query.dto.ReservationSearchCondition;
import roomescape.application.reservation.query.dto.ReservationWithStatusResult;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.repository.ReservationRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservationQueryService {

    private final ReservationRepository reservationRepository;

    public ReservationQueryService(final ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

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

    public List<ReservationWithStatusResult> findReservationsWithStatus(final Long memberId) {
        return reservationRepository.findAllByMemberId(memberId)
                .stream()
                .map(ReservationWithStatusResult::from)
                .toList();
    }
}
