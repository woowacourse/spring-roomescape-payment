package roomescape.time.service;


import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BadRequestException;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.time.domain.ReservationTime;
import roomescape.time.repository.ReservationTimeRepository;

@Service
@Slf4j
public class ReservationTimeCommandService {
    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationQueryService reservationQueryService;

    public ReservationTimeCommandService(
            final ReservationTimeRepository reservationTimeRepository,
            final ReservationQueryService reservationQueryService
    ) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationQueryService = reservationQueryService;
    }

    @Transactional
    public ReservationTime createReservationTime(LocalTime time) {
        if (reservationTimeRepository.existsByStartAt(time)) {
            throw new BadRequestException("이미 존재하는 시간입니다.");
        }
        return reservationTimeRepository.save(new ReservationTime(null, time));
    }

    @Transactional
    public void deleteTimeById(final Long timeId) {
        if (reservationQueryService.existsReservationInTime(timeId)) {
            throw new BadRequestException("해당 시간에 예약이 존재하여 삭제할 수 없습니다.");
        }
        reservationTimeRepository.deleteById(timeId);
    }
}
