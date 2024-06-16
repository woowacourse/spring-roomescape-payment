package roomescape.application;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.time.ReservationTimeRequest;
import roomescape.application.dto.response.time.AvailableReservationTimeResponse;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.exception.time.DuplicatedTimeException;
import roomescape.exception.time.ReservationReferencedTimeException;
import roomescape.infrastructure.repository.ReservationRepository;
import roomescape.infrastructure.repository.ReservationTimeRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationTimeService {
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public List<ReservationTimeResponse> findAllReservationTime() {
        return reservationTimeRepository.findAll()
                .stream()
                .map(ReservationTimeResponse::from)
                .toList();
    }

    public List<AvailableReservationTimeResponse> findAllAvailableReservationTime(LocalDate date, Long themeId) {
        List<ReservationTime> totalTimes = reservationTimeRepository.findAll();
        List<ReservationTime> reservedTimes = reservationTimeRepository.findAllReservedTimeByDateAndThemeId(date,
                themeId);

        return totalTimes.stream()
                .map(time -> AvailableReservationTimeResponse.of(time, reservedTimes))
                .toList();
    }

    @Transactional
    public ReservationTimeResponse saveReservationTime(ReservationTimeRequest request) {
        validateDuplicatedTime(request);
        ReservationTime reservationTime = request.toReservationTime();
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponse.from(savedReservationTime);
    }

    private void validateDuplicatedTime(ReservationTimeRequest request) {
        if (reservationTimeRepository.existsByStartAt(request.startAt())) {
            throw new DuplicatedTimeException();
        }
    }

    public void deleteReservationTime(Long id) {
        if (reservationRepository.existsByTimeId(id)) {
            throw new ReservationReferencedTimeException();
        }
        reservationTimeRepository.deleteById(id);
    }
}
