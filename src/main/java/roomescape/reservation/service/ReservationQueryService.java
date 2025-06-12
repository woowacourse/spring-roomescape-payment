package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.NotFoundException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationSpecifications;
import roomescape.reservation.repository.dto.MyReservationWithTossPayment;
import roomescape.schedule.domain.ReservationSchedule;

@Service
@Transactional(readOnly = true)
public class ReservationQueryService {
    private final ReservationRepository reservationRepository;

    public ReservationQueryService(final ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<MyReservationWithTossPayment> findAllMyReservationWithTossPayment(final Long memberId) {
        return reservationRepository.findAllWithPaymentByMemberId(memberId);
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
