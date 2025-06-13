package roomescape.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.ReservationSlots;
import roomescape.dto.request.AvailableTimeRequest;
import roomescape.dto.request.CreateReservationTimeRequest;
import roomescape.entity.Reservation;
import roomescape.entity.ReservationTime;
import roomescape.exception.custom.InvalidReservationTimeException;
import roomescape.global.ReservationStatus;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

@Service
@Transactional
@Slf4j
public class ReservationTimeService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;

    public ReservationTimeService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
    }

    public ReservationTime addReservationTime(CreateReservationTimeRequest request) {
        log.info("예약 시간 생성 시작 - startAt: {}", request.startAt());

        ReservationTime reservationTime = request.toReservationTime();
        if (reservationTimeRepository.existsByStartAt(reservationTime.getStartAt())) {
            log.error("중복된 예약 시간 - startAt: {}", request.startAt());
            throw new InvalidReservationTimeException("중복된 예약시간입니다");
        }

        ReservationTime savedTime = reservationTimeRepository.save(reservationTime);
        log.info("예약 시간 생성 완료 - timeId: {}, startAt: {}", savedTime.getId(), savedTime.getStartAt());
        return savedTime;
    }

    public List<ReservationTime> findAll() {
        log.info("예약 시간 목록 조회 시작");
        List<ReservationTime> times = reservationTimeRepository.findAll();
        log.info("예약 시간 목록 조회 완료 - 총 {}개", times.size());
        return times;
    }

    public void deleteReservationTime(Long id) {
        log.info("예약 시간 삭제 시작 - timeId: {}", id);

        if (reservationRepository.existsByReservationTimeId(id)) {
            log.error("예약이 존재하는 시간 삭제 시도 - timeId: {}", id);
            throw new InvalidReservationTimeException("예약이 되어있는 시간은 삭제할 수 없습니다.");
        }

        reservationTimeRepository.deleteById(id);
        log.info("예약 시간 삭제 완료 - timeId: {}", id);
    }

    public ReservationSlots getReservationSlots(AvailableTimeRequest request) {
        log.info("예약 슬롯 조회 시작 - themeId: {}, date: {}", request.themeId(), request.date());

        List<ReservationTime> times = reservationTimeRepository.findAll();

        List<Reservation> alreadyReservedReservations = reservationRepository.findAllByDateAndThemeIdAndStatus(
                request.date(), request.themeId(), ReservationStatus.RESERVED);

        ReservationSlots slots = new ReservationSlots(times, alreadyReservedReservations);

        log.info("예약 슬롯 조회 완료 - themeId: {}, date: {}, 총 {}개",
                request.themeId(), request.date(), slots.getReservationSlots().size());
        return slots;
    }
}
