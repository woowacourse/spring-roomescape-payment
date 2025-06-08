package roomescape.reservationtime.service;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.request.ReservationTimeCreateRequest;
import roomescape.reservationtime.dto.response.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.response.ReservationTimeResponse;
import roomescape.reservationtime.exception.ReservationTimeAlreadyExistsException;
import roomescape.reservationtime.exception.ReservationTimeInUseException;
import roomescape.reservationtime.repository.ReservationTimeRepository;

@Slf4j
@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(final ReservationTimeRepository reservationTimeRepository,
                                  final ReservationRepository reservationRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<ReservationTimeResponse> getReservationTimes() {
        return reservationTimeRepository.findAll().stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    public void delete(Long id) {
        if (reservationRepository.existsByTimeId(id)) {
            log.info("해당 시간에 대한 예약 존재로 인한 삭제 실패 timeId = {}", id);
            throw new ReservationTimeInUseException("해당 시간에 대한 예약이 존재하여 삭제할 수 없습니다.");
        }
        reservationTimeRepository.deleteById(id);
    }

    public ReservationTimeResponse create(final ReservationTimeCreateRequest request) {
        validateIsTimeUnique(request);
        ReservationTime newReservationTime = reservationTimeRepository.save(request.toReservationTime());
        return ReservationTimeResponse.from(newReservationTime);
    }

    void validateIsTimeUnique(final ReservationTimeCreateRequest request) {
        if (reservationTimeRepository.existsByStartAt(request.startAt())) {
            log.info("중복된 예약 시간으로 인한 예약 시간 생성 실패 startAt = {}", request.startAt());
            throw new ReservationTimeAlreadyExistsException("중복된 예약 시간을 생성할 수 없습니다.");
        }
    }

    public List<AvailableReservationTimeResponse> getAvailableReservationTimes(final LocalDate date,
                                                                               final Long themeId) {
        return reservationRepository.findBookedTimesByDateAndThemeId(date, themeId);
    }
}
