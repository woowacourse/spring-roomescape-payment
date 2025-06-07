package roomescape.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.slot.ReservationTime;
import roomescape.dto.time.ReservationTimeCreateRequestDto;
import roomescape.dto.time.ReservationTimeResponseDto;
import roomescape.exception.DuplicateContentException;
import roomescape.exception.common.ForbiddenException;
import roomescape.exception.common.NotFoundException;
import roomescape.repository.JpaReservationRepository;
import roomescape.repository.JpaReservationTimeRepository;

@Service
@Transactional
public class ReservationTimeCommandService {

    private final JpaReservationTimeRepository reservationTimeRepository;
    private final JpaReservationRepository reservationRepository;

    public ReservationTimeCommandService(JpaReservationTimeRepository reservationTimeRepository,
                                         JpaReservationRepository reservationRepository) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationRepository = reservationRepository;
    }

    public ReservationTimeResponseDto createReservationTime(final ReservationTimeCreateRequestDto requestDto) {
        if (reservationTimeRepository.existsByStartAt(requestDto.startAt())) {
            throw new DuplicateContentException("해당 시간이 이미 존재합니다.", requestDto.startAt().toString());
        }
        ReservationTime requestTime = requestDto.createWithoutId();
        ReservationTime savedTime = reservationTimeRepository.save(requestTime);
        return new ReservationTimeResponseDto(savedTime);
    }

    public void deleteReservationTimeById(final Long id) {
        if (!reservationTimeRepository.existsById(id)) {
            throw new NotFoundException("예약 시간", id);
        }
        if (reservationRepository.existsByTimeId(id)) {
            throw new ForbiddenException("이 시간의 예약이 이미 존재합니다. 예약 id : " + id);
        }

        reservationTimeRepository.deleteById(id);
    }
}
