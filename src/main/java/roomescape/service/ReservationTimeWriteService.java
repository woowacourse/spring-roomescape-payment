package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.ReservationTimeRequest;
import roomescape.exception.BadRequestException;
import roomescape.exception.DuplicatedException;
import roomescape.exception.NotFoundException;
import roomescape.model.ReservationTime;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

import java.time.LocalTime;

@Transactional
@Service
public class ReservationTimeWriteService {

    private final ReservationTimeRepository reservationTimeRepository;
    private final ReservationRepository reservationRepository;

    public ReservationTimeWriteService(ReservationTimeRepository reservationTimeRepository,
                                       ReservationRepository reservationRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTime addReservationTime(ReservationTimeRequest request) {
        LocalTime startAt = request.startAt();

        validateExistTime(startAt);

        ReservationTime reservationTime = new ReservationTime(startAt);
        return reservationTimeRepository.save(reservationTime);
    }

    private void validateExistTime(LocalTime startAt) {
        boolean exists = reservationTimeRepository.existsByStartAt(startAt);
        if (exists) {
            throw new DuplicatedException("이미 존재하는 시간입니다.");
        }
    }

    public void deleteReservationTime(long id) {
        validateNotExistReservationTime(id);
        validateReservedTime(id);

        reservationTimeRepository.deleteById(id);
    }

    private void validateReservedTime(long id) {
        ReservationTime time = getById(id);

        boolean exists = reservationRepository.existsByTime(time);
        if (exists) {
            throw new BadRequestException("해당 시간에 예약이 존재하여 삭제할 수 없습니다.");
        }
    }

    private void validateNotExistReservationTime(long id) {
        boolean exists = reservationTimeRepository.existsById(id);
        if (!exists) {
            throw new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(id));
        }
    }

    private ReservationTime getById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("아이디가 %s인 예약 시간이 존재하지 않습니다.".formatted(id)));
    }
}
