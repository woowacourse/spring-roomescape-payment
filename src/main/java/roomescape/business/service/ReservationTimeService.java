package roomescape.business.service;

import static roomescape.exception.ErrorCode.RESERVATION_NOT_EXIST;
import static roomescape.exception.ErrorCode.RESERVATION_TIME_ALREADY_EXIST;
import static roomescape.exception.ErrorCode.RESERVATION_TIME_INTERVAL_INVALID;
import static roomescape.exception.ErrorCode.RESERVED_RESERVATION_TIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.ReservationTime;
import roomescape.business.model.vo.Id;
import roomescape.exception.business.DuplicatedException;
import roomescape.exception.business.InvalidCreateArgumentException;
import roomescape.exception.business.NotFoundException;
import roomescape.exception.business.RelatedEntityExistException;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.presentation.dto.response.ReservationTimeResponse;
import roomescape.presentation.dto.response.ReservationTimeResponseWithBooked;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeResponse addAndGet(final LocalTime time) {
        ReservationTime reservationTime = ReservationTime.create(time);
        validateNoDuplication(reservationTime);
        validateTimeInterval(reservationTime);

        reservationTimeRepository.save(reservationTime);
        return ReservationTimeResponse.from(reservationTime);
    }


    private void validateNoDuplication(final ReservationTime reservationTime) {
        boolean isExist = reservationTimeRepository.existsByStartTime_Value(reservationTime.startTimeValue());
        if (isExist) {
            throw new DuplicatedException(RESERVATION_TIME_ALREADY_EXIST);
        }
    }

    private void validateTimeInterval(final ReservationTime reservationTime) {
        boolean existInInterval = reservationTimeRepository.existsByStartTime_ValueBetween(
                reservationTime.startInterval(),
                reservationTime.endInterval());
        if (existInInterval) {
            throw new InvalidCreateArgumentException(RESERVATION_TIME_INTERVAL_INVALID);
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationTimeResponse> getAll() {
        return reservationTimeRepository.findAll()
                .stream()
                .map(ReservationTimeResponse::from)
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
        if (reservationRepository.existsByTimeId(timeId)) {
            throw new RelatedEntityExistException(RESERVED_RESERVATION_TIME);
        }
        if (!reservationTimeRepository.existsById(timeId)) {
            throw new NotFoundException(RESERVATION_NOT_EXIST);
        }
        reservationTimeRepository.deleteById(timeId);
    }
}
