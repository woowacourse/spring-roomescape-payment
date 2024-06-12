package roomescape.domain.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.domain.reservation.dto.ReservationTimeDto;
import roomescape.domain.reservation.dto.SaveReservationTimeRequest;
import roomescape.domain.reservation.model.Reservation;
import roomescape.domain.reservation.model.ReservationDate;
import roomescape.domain.reservation.model.ReservationTime;
import roomescape.domain.reservation.model.ReservationTimeAvailabilities;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationTimeRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationTimeService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationTimeService(
            final ReservationRepository reservationRepository,
            final ReservationTimeRepository reservationTimeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    public List<ReservationTimeDto> getReservationTimes() {
        return reservationTimeRepository.findAll()
                .stream()
                .map(ReservationTimeDto::from)
                .toList();
    }

    public ReservationTimeDto saveReservationTime(final SaveReservationTimeRequest request) {
        validateReservationTimeDuplication(request);

        final ReservationTime savedReservationTime = reservationTimeRepository.save(request.toModel());
        return ReservationTimeDto.from(savedReservationTime);
    }

    private void validateReservationTimeDuplication(final SaveReservationTimeRequest request) {
        if (reservationTimeRepository.existsByStartAt(request.startAt())) {
            throw new IllegalArgumentException("이미 존재하는 예약시간이 있습니다.");
        }
    }

    public void deleteReservationTime(final Long reservationTimeId) {
        validateReservationTimeExist(reservationTimeId);
        reservationTimeRepository.deleteById(reservationTimeId);
    }

    private void validateReservationTimeExist(final Long reservationTimeId) {
        if (reservationRepository.existsByTimeId(reservationTimeId)) {
            throw new IllegalArgumentException("예약에 포함된 시간 정보는 삭제할 수 없습니다.");
        }
    }

    public ReservationTimeAvailabilities getAvailableReservationTimes(final LocalDate date, final Long themeId) {
        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        final List<Reservation> reservations = reservationRepository.findAllByDateAndTheme_Id(new ReservationDate(date), themeId);

        return ReservationTimeAvailabilities.of(reservationTimes, reservations);
    }
}
