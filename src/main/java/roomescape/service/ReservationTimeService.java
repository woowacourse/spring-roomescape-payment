package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.exception.BadRequestException;
import roomescape.exception.DuplicatedException;
import roomescape.exception.NotFoundException;
import roomescape.model.ReservationTime;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.request.ReservationTimeRequest;
import roomescape.response.IsReservedTimeResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;

    private final ReservationRepository reservationRepository;

    public ReservationTimeService(final ReservationTimeRepository reservationTimeRepository,
                                  final ReservationRepository reservationRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }


    public List<ReservationTime> findAllReservationTimes() {
        return reservationTimeRepository.findAll();
    }

    public ReservationTime addReservationTime(final ReservationTimeRequest request) {
        LocalTime startAt = request.startAt();

        validateExistTime(startAt);

        ReservationTime reservationTime = new ReservationTime(startAt);
        return reservationTimeRepository.save(reservationTime);
    }

    private void validateExistTime(final LocalTime startAt) {
        boolean exists = reservationTimeRepository.existsByStartAt(startAt);
        if (exists) {
            throw new DuplicatedException("이미 존재하는 시간입니다.");
        }
    }

    public ReservationTime findReservationTime(final Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(id)));
    }

    public List<IsReservedTimeResponse> getIsReservedTime(final LocalDate date, final Long themeId) {
        List<ReservationTime> allTimes = reservationTimeRepository.findAll();
        List<ReservationTime> bookedTimes = reservationTimeRepository.findAllReservedTimes(date, themeId);

        List<ReservationTime> notBookedTimes = filterNotBookedTimes(allTimes, bookedTimes);
        List<IsReservedTimeResponse> bookedResponse = mapToResponse(bookedTimes, true);
        List<IsReservedTimeResponse> notBookedResponse = mapToResponse(notBookedTimes, false);

        return concat(notBookedResponse, bookedResponse);
    }

    public void deleteReservationTime(final Long id) {
        validateNotExistReservationTime(id);
        validateReservedTime(id);

        reservationTimeRepository.deleteById(id);
    }

    private void validateReservedTime(final Long id) {
        ReservationTime time = reservationTimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(id)));

        boolean exists = reservationRepository.existsByTime(time);
        if (exists) {
            throw new BadRequestException("해당 시간에 예약이 존재하여 삭제할 수 없습니다.");
        }
    }

    private void validateNotExistReservationTime(final Long id) {
        boolean exists = reservationTimeRepository.existsById(id);
        if (!exists) {
            throw new NotFoundException("id(%s)에 해당하는 예약 시간이 존재하지 않습니다.".formatted(id));
        }
    }

    private List<ReservationTime> filterNotBookedTimes(final List<ReservationTime> times, final List<ReservationTime> bookedTimes) {
        return times.stream()
                .filter(time -> !bookedTimes.contains(time))
                .toList();
    }

    private List<IsReservedTimeResponse> mapToResponse(final List<ReservationTime> times, final Boolean isBooked) {
        return times.stream()
                .map(time -> new IsReservedTimeResponse(time.getId(), time.getStartAt(), isBooked))
                .toList();
    }

    private List<IsReservedTimeResponse> concat(List<IsReservedTimeResponse> notBookedTimes,
                                                List<IsReservedTimeResponse> bookedTimes) {
        return Stream.concat(notBookedTimes.stream(), bookedTimes.stream()).toList();
    }
}
