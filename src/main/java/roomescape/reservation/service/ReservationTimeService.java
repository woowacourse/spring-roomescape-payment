package roomescape.reservation.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.dto.AvailableReservationTimeResponse;
import roomescape.reservation.dto.TimeResponse;
import roomescape.reservation.dto.TimeSaveRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;

@Service
public class ReservationTimeService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationTimeService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    public TimeResponse save(TimeSaveRequest timeSaveRequest) {
        reservationTimeRepository.findByStartAt(timeSaveRequest.startAt()).ifPresent(empty -> {
            throw new IllegalArgumentException("이미 존재하는 예약 시간 입니다.");
        });

        ReservationTime reservationTime = timeSaveRequest.toReservationTime();
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        return TimeResponse.toResponse(savedReservationTime);
    }

    @Transactional(readOnly = true)
    public TimeResponse findById(Long id) {
        ReservationTime reservationTime = reservationTimeRepository.fetchById(id);

        return TimeResponse.toResponse(reservationTime);
    }

    @Transactional(readOnly = true)
    public List<AvailableReservationTimeResponse> findAvailableTimes(LocalDate date, Long themeId) {
        List<Long> bookedTimeIds = reservationRepository.findTimeIdsByDateAndThemeId(date, themeId);

        return reservationTimeRepository.findAllOrderByStartAt().stream()
                .map(reservationTime -> AvailableReservationTimeResponse.toResponse(
                                reservationTime,
                                bookedTimeIds.contains(reservationTime.getId())
                        )
                ).toList();
    }

    @Transactional(readOnly = true)
    public List<TimeResponse> findAll() {
        return reservationTimeRepository.findAllOrderByStartAt().stream()
                .map(TimeResponse::toResponse)
                .toList();
    }

    public void delete(Long id) {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findReservationTimesThatReservationReferById(id);
        if (!reservationTimes.isEmpty()) {
            throw new IllegalArgumentException("해당 시간으로 예약된 내역이 있습니다.");
        }
        reservationTimeRepository.deleteById(id);
    }
}
