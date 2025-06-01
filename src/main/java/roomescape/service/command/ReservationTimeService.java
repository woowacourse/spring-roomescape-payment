package roomescape.service.command;

import java.time.LocalTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationTime;
import roomescape.dto.business.ReservationTimeCreationContent;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.exception.BadRequestException;
import roomescape.repository.ReservationTimeRepository;
import roomescape.service.query.ReservationQueryService;
import roomescape.service.query.ReservationTimeQueryService;
import roomescape.service.query.WaitingQueryService;

@Service
@Transactional
public class ReservationTimeService {

    private final ReservationTimeRepository timeRepository;
    private final ReservationTimeQueryService timeQueryService;
    private final ReservationQueryService reservationQueryService;
    private final WaitingQueryService waitingQueryService;

    public ReservationTimeService(
            ReservationTimeRepository timeRepository,
            ReservationTimeQueryService timeQueryService,
            ReservationQueryService reservationQueryService,
            WaitingQueryService waitingQueryService
    ) {
        this.timeRepository = timeRepository;
        this.timeQueryService = timeQueryService;
        this.reservationQueryService = reservationQueryService;
        this.waitingQueryService = waitingQueryService;
    }

    public ReservationTimeResponse addReservationTime(ReservationTimeCreationContent request) {
        validateDuplicateTime(request.startAt());
        ReservationTime reservationTime = ReservationTime.createWithoutId(request.startAt());
        ReservationTime savedReservationTime = timeRepository.save(reservationTime);
        return new ReservationTimeResponse(savedReservationTime);
    }

    public void deleteReservationTimeById(Long timeId) {
        ReservationTime reservationTime = timeQueryService.getReservationTimeById(timeId);
        validateReservationInTime(reservationTime);
        validateWaitingInTime(reservationTime);
        timeRepository.deleteById(timeId);
    }

    private void validateDuplicateTime(LocalTime startAt) {
        boolean alreadyExistTime = timeRepository.existsByStartAt(startAt);
        if (alreadyExistTime) {
            throw new BadRequestException("중복된 예약시간입니다.");
        }
    }

    private void validateReservationInTime(ReservationTime reservationTime) {
        if (reservationQueryService.existsReservationInTime(reservationTime)) {
            throw new BadRequestException("이미 예약이 존재하는 예약 시간입니다.");
        }
    }

    private void validateWaitingInTime(ReservationTime reservationTime) {
        if (waitingQueryService.existsWaitingInTime(reservationTime)) {
            throw new BadRequestException("이미 예약 대기가 존재하는 예약 시간입니다.");
        }
    }
}
