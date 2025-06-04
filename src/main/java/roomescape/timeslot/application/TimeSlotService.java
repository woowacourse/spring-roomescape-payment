package roomescape.timeslot.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.timeslot.domain.TimeSlot;
import roomescape.timeslot.domain.TimeSlotRepository;
import roomescape.timeslot.dto.request.TimeSlotRequest;
import roomescape.timeslot.dto.response.TimeSlotResponse;
import roomescape.timeslot.dto.request.TimeSlotConditionRequest;
import roomescape.timeslot.dto.response.TimeSlotConditionResponse;
import roomescape.timeslot.exception.TimeSlotException;

@Service
@Transactional(readOnly = true)
public class TimeSlotService {

    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository reservationTimeRepository;

    public TimeSlotService(final ReservationRepository reservationRepository,
                                  final TimeSlotRepository reservationTimeRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    @Transactional
    public TimeSlotResponse createTimeSlot(final TimeSlotRequest request) {
        TimeSlot timeSlot = reservationTimeRepository.save(
            TimeSlot.createWithoutId(request.startAt()));

        return TimeSlotResponse.from(timeSlot);
    }

    @Transactional
    public void deleteTimeSlotById(final Long id) {
        validateExistIdToDelete(id);

        reservationTimeRepository.findById(id).orElseThrow(() -> new TimeSlotException("존재하지 않는 예약 시간입니다."));
        reservationTimeRepository.deleteById(id);
    }

    private void validateExistIdToDelete(final Long id) {
        if (reservationRepository.existsByTimeId(id)) {
            throw new TimeSlotException("해당 시간에 예약이 존재해서 삭제할 수 없습니다.");
        }
    }

    public List<TimeSlotResponse> getTimeSlots() {
        return reservationTimeRepository.findAll().stream().map(TimeSlotResponse::from).toList();
    }

    public List<TimeSlotConditionResponse> getTimesWithCondition(final TimeSlotConditionRequest request) {
        List<Reservation> reservations = reservationRepository.findAllByDateAndThemeId(request.date(), request.themeId());
        List<TimeSlot> times = reservationTimeRepository.findAll();

        return times.stream().map(time -> {
            boolean hasTime = reservations.stream()
                .anyMatch(reservation -> reservation.isSameTime(time));
            return new TimeSlotConditionResponse(time.getId(), time.getStartAt(), hasTime);
        }).toList();
    }
}
