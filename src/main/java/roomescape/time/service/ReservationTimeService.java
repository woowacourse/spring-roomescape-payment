package roomescape.time.service;

import static roomescape.exception.ExceptionType.DELETE_USED_TIME;
import static roomescape.exception.ExceptionType.DUPLICATE_RESERVATION_TIME;
import static roomescape.exception.ExceptionType.NOT_FOUND_THEME;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import roomescape.exception.RoomescapeException;
import roomescape.reservation.domain.Reservations;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTimes;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.ReservationTimeRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.entity.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

@Service
public class ReservationTimeService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    public ReservationTimeService(ReservationRepository reservationRepository,
                                  ReservationTimeRepository reservationTimeRepository,
                                  ThemeRepository themeRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
    }

    public ReservationTimeResponse save(ReservationTimeRequest reservationTimeRequest) {
        if (reservationTimeRepository.existsByStartAt(reservationTimeRequest.startAt())) {
            throw new RoomescapeException(DUPLICATE_RESERVATION_TIME, reservationTimeRequest.startAt());
        }
        ReservationTime beforeSavedReservationTime = reservationTimeRequest.toReservationTime();
        ReservationTime savedReservationTime = reservationTimeRepository.save(beforeSavedReservationTime);
        return ReservationTimeResponse.from(savedReservationTime);
    }

    public List<ReservationTimeResponse> findAll() {
        return new ReservationTimes(reservationTimeRepository.findAll()).getReservationTimes().stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    public List<AvailableTimeResponse> findByThemeAndDate(LocalDate date, long themeId) {
        Theme requestedTheme = themeRepository.findById(themeId)
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_THEME, themeId));
        Reservations findReservations = new Reservations(reservationRepository.findByThemeAndDate(requestedTheme, date));

        return new ReservationTimes(reservationTimeRepository.findAll()).getReservationTimes().stream()
                .map(reservationTime -> AvailableTimeResponse.of(reservationTime, findReservations))
                .toList();
    }

    public void delete(long timeId) {
        if (isUsedTime(timeId)) {
            throw new RoomescapeException(DELETE_USED_TIME, timeId);
        }
        reservationTimeRepository.deleteById(timeId);
    }

    private boolean isUsedTime(long timeId) {
        return reservationRepository.existsByTimeId(timeId);
    }
}
