package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationSpecifications;
import roomescape.schedule.domain.ReservationSchedule;

@Service
@Transactional(readOnly = true)
public class ReservationQueryService {
    private final ReservationRepository reservationRepository;

    public ReservationQueryService(final ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> findAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservation(final Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("예약을 찾을 수 없습니다."));
    }

    public List<Reservation> findAllMyReservation(final Long memberId) {
        return reservationRepository.findAllByMember_Id(memberId);
    }

    public List<Reservation> findAllReservationsWithFilter(
            final Long memberId,
            final Long themeId,
            final LocalDate fromDate,
            final LocalDate toDate
    ) {
        final Specification<Reservation> spec = Specification
                .where(ReservationSpecifications.hasMemberId(memberId))
                .and(ReservationSpecifications.hasThemeId(themeId))
                .and(ReservationSpecifications.dateAfterOrEqual(fromDate))
                .and(ReservationSpecifications.dateBeforeOrEqual(toDate));
        return reservationRepository.findAll(spec);
    }

    public boolean existsReservationInTime(Long timeId) {
        return reservationRepository.existsBySchedule_ReservationTime_Id(timeId);
    }

    public boolean existsReservation(final ReservationSchedule schedule) {
        return reservationRepository.existsByScheduleId(schedule.getId());
    }

    public boolean existsReservationInTheme(final Long themeId) {
        return reservationRepository.existsBySchedule_Theme_Id(themeId);
    }
}
