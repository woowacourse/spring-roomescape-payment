package roomescape.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.dto.timeslot.request.TimeSlotConditionRequest;
import roomescape.dto.timeslot.request.TimeSlotRequest;
import roomescape.dto.timeslot.response.TimeSlotConditionResponse;
import roomescape.dto.timeslot.response.TimeSlotResponse;
import roomescape.exception.timeslot.TimeSlotException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TimeSlotService {

    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;
    
    @Transactional
    public TimeSlotResponse createTimeSlot(final TimeSlotRequest request) {
        TimeSlot timeSlot = timeSlotRepository.save(TimeSlot.createWithoutId(request.startAt()));

        return TimeSlotResponse.from(timeSlot);
    }

    @Transactional
    public void deleteTimeSlotById(final Long id) {
        validateExistIdToDelete(id);

        timeSlotRepository.findById(id)
                .orElseThrow(() -> new TimeSlotException("존재하지 않는 예약 시간입니다."));
        timeSlotRepository.deleteById(id);
    }

    private void validateExistIdToDelete(final Long id) {
        if (reservationRepository.existsByTimeId(id)) {
            throw new TimeSlotException("해당 시간에 예약이 존재해서 삭제할 수 없습니다.");
        }
    }

    public List<TimeSlotResponse> getTimeSlots() {
        return timeSlotRepository.findAll().stream().map(TimeSlotResponse::from).toList();
    }

    public List<TimeSlotConditionResponse> getTimesWithCondition(final TimeSlotConditionRequest request) {
        List<Reservation> reservations = reservationRepository.findAllByDateAndThemeId(request.date(), request.themeId());
        List<TimeSlot> times = timeSlotRepository.findAll();

        return times.stream().map(time -> {
            boolean hasTime = reservations.stream()
                    .anyMatch(reservation -> reservation.isSameTime(time));
            return new TimeSlotConditionResponse(time.id(), time.startAt(), hasTime);
        }).toList();
    }
}
