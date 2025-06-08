package roomescape.time.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.global.exception.custom.BadRequestException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.dto.CreateReservationTimeRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.repository.ReservationTimeRepository;

@Service
@Slf4j
public class ReservationTimeService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeService(ReservationTimeRepository reservationTimeRepository,
                                  ReservationRepository reservationRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTimeResponse createReservationTime(final CreateReservationTimeRequest request) {
        log.info("예약 시간 생성 요청 - startAt: {}", request.startAt());
        if (reservationTimeRepository.existsByStartAt(request.startAt())) {
            throw new BadRequestException("이미 존재하는 시간입니다.");
        }
        final ReservationTime reservationTime = request.convertToReservationTime();
        final ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);
        log.info("예약 시간 생성 완료 - timeId: {}, startAt: {}", savedReservationTime.getId(), savedReservationTime.getStartAt());
        return new ReservationTimeResponse(savedReservationTime);
    }

    public List<ReservationTimeResponse> getReservationTimes() {
        return reservationTimeRepository.findAll().stream()
                .map(ReservationTimeResponse::new)
                .toList();
    }

    public void deleteReservationTimeById(final long id) {
        log.info("예약 시간 삭제 요청 - timeId: {}", id);
        if (reservationRepository.existsByTimeId(id)) {
            throw new BadRequestException("예약이 존재하는 시간은 삭제할 수 없습니다.");
        }
        reservationTimeRepository.deleteById(id);
        log.info("예약 시간 삭제 완료 - timeId: {}", id);
    }
}
