package roomescape.reservation.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.NotFoundException;
import roomescape.global.exception.ViolationException;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationTimeRepository;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.ThemeRepository;
import roomescape.reservation.dto.response.AvailableReservationTimeResponse;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReservationTimeService {
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;
    private final ThemeRepository themeRepository;

    public ReservationTimeService(ReservationTimeRepository reservationTimeRepository,
                                  ReservationRepository reservationRepository,
                                  ThemeRepository themeRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
        this.themeRepository = themeRepository;
    }

    public ReservationTime create(ReservationTime reservationTime) {
        return reservationTimeRepository.save(reservationTime);
    }

    public List<ReservationTime> findAll() {
        return reservationTimeRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        reservationTimeRepository.findById(id).ifPresent(time -> {
            validateHasReservation(time);
            reservationTimeRepository.delete(time);
        });
    }

    private void validateHasReservation(ReservationTime reservationTime) {
        int reservationCount = reservationRepository.countByTime(reservationTime);
        if (reservationCount > 0) {
            throw new ViolationException("해당 예약 시간의 예약 건이 존재합니다.");
        }
    }

    public List<AvailableReservationTimeResponse> findAvailableReservationTimes(LocalDate date, Long themeId) {
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("해당 ID의 테마가 없습니다."));
        Set<Long> reservedTimeIds = new HashSet<>(reservationRepository.findAllTimeIdsByDateAndTheme(date, theme));
        List<ReservationTime> times = reservationTimeRepository.findAll();

        return times.stream()
                .map(reservationTime -> {
                    boolean isReserved = reservedTimeIds.contains(reservationTime.getId());
                    return AvailableReservationTimeResponse.of(reservationTime, isReserved);
                })
                .collect(Collectors.toList());
    }
}
