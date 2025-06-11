package roomescape.business.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.vo.Id;
import roomescape.exception.reservation.InvalidTimeSlotIntervalException;
import roomescape.exception.reservation.ReservationExistsException;
import roomescape.exception.reservation.TimeSlotExistsException;
import roomescape.exception.reservation.TimeSlotNotFoundException;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.presentation.dto.request.ReservationTimeRequest;
import roomescape.presentation.dto.response.ReservationTimeResponseWithBooked;
import roomescape.presentation.dto.response.TimeSlotResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public TimeSlotResponse addAndGet(final ReservationTimeRequest request) {
        TimeSlot timeSlot = TimeSlot.create(request.startAtToLocalTime());
        validateNoDuplication(timeSlot);
        validateTimeInterval(timeSlot);

        reservationTimeRepository.save(timeSlot);
        return TimeSlotResponse.from(timeSlot);
    }


    private void validateNoDuplication(final TimeSlot timeSlot) {
        boolean isExist = reservationTimeRepository.existsByStartAt(timeSlot.getStartAt());
        if (isExist) {
            throw new TimeSlotExistsException();
        }
    }

    private void validateTimeInterval(final TimeSlot timeSlot) {
        boolean existInInterval = reservationTimeRepository.existsByStartAtBetween(
                timeSlot.startInterval(),
                timeSlot.endInterval());
        if (existInInterval) {
            throw new InvalidTimeSlotIntervalException();
        }
    }

    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getAll() {
        return reservationTimeRepository.findAll()
                .stream()
                .map(TimeSlotResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponseWithBooked> getAllByDateAndThemeId(final LocalDate date,
                                                                          final String themeIdValue) {
        Id themeId = Id.create(themeIdValue);
        return reservationTimeRepository.findByDateAndThemeIdWithAlreadyBooked(date, themeId);
    }

    public void delete(final String themeIdValue) {
        Id timeId = Id.create(themeIdValue);
        if (reservationRepository.existsByTimeSlotId(timeId)) {
            throw new ReservationExistsException();
        }
        if (!reservationTimeRepository.existsById(timeId)) {
            throw new TimeSlotNotFoundException();
        }
        reservationTimeRepository.deleteById(timeId);
    }
}
