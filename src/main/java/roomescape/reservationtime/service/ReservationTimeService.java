package roomescape.reservationtime.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.ReservationException;
import roomescape.reservation.repository.RegistrationQueryRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.repository.ReservationTimeRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final RegistrationQueryRepository registrationQueryRepository;

    public ReservationTimeResponse saveTime(final ReservationTimeRequest request) {
        final ReservationTime reservationTime = reservationTimeRepository.save(ReservationTime.from(request.startAt()));
        log.info("예약 시간 생성 완료 - id={}, startAt={}",
                reservationTime.getId(), reservationTime.getStartAt());
        return new ReservationTimeResponse(reservationTime);
    }

    public List<ReservationTimeResponse> findAll() {
        final List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();
        return reservationTimes.stream()
                .map(ReservationTimeResponse::new)
                .toList();
    }

    public List<AvailableReservationTimeResponse> findAllReservationTime(final LocalDate date, final Long themeId) {
        return reservationTimeRepository.findAllAvailable(date, themeId);
    }

    @Transactional
    public void delete(final Long id) {
        if (registrationQueryRepository.existsByTimeId(id)) {
            throw new ReservationException("해당 시간으로 예약된 건이 존재합니다.");
        }
        reservationTimeRepository.deleteById(id);
        log.info("예약 시간 삭제 완료 - id={}", id);
    }
}
