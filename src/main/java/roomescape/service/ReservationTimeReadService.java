package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.response.IsReservedTimeResponse;
import roomescape.exception.NotFoundException;
import roomescape.model.ReservationTime;
import roomescape.repository.ReservationTimeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Transactional(readOnly = true)
@Service
public class ReservationTimeReadService {

    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationTimeReadService(ReservationTimeRepository reservationTimeRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
    }

    public List<ReservationTime> findAllReservationTimes() {
        return reservationTimeRepository.findAll();
    }

    public ReservationTime getReservationTime(long id) {
        return getById(id);
    }

    public List<IsReservedTimeResponse> getIsReservedTime(LocalDate date, long themeId) {
        List<ReservationTime> allTimes = reservationTimeRepository.findAll();
        List<ReservationTime> bookedTimes = reservationTimeRepository.findAllReservedTimes(date, themeId);

        List<ReservationTime> notBookedTimes = filterNotBookedTimes(allTimes, bookedTimes);
        List<IsReservedTimeResponse> bookedResponse = mapToResponse(bookedTimes, true);
        List<IsReservedTimeResponse> notBookedResponse = mapToResponse(notBookedTimes, false);

        return concat(notBookedResponse, bookedResponse);
    }

    private List<ReservationTime> filterNotBookedTimes(List<ReservationTime> times, List<ReservationTime> bookedTimes) {
        return times.stream()
                .filter(time -> !bookedTimes.contains(time))
                .toList();
    }

    private List<IsReservedTimeResponse> mapToResponse(List<ReservationTime> times, boolean isBooked) {
        return times.stream()
                .map(time -> new IsReservedTimeResponse(time.getId(), time.getStartAt(), isBooked))
                .toList();
    }

    private List<IsReservedTimeResponse> concat(List<IsReservedTimeResponse> notBookedTimes,
                                                List<IsReservedTimeResponse> bookedTimes) {
        return Stream.concat(notBookedTimes.stream(), bookedTimes.stream()).toList();
    }

    private ReservationTime getById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(id)));
    }
}
