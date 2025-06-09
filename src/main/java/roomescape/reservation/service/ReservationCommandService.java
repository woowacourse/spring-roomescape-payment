package roomescape.reservation.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.schedule.domain.ReservationSchedule;

@Service
@Slf4j
public class ReservationCommandService {
    private final ReservationRepository reservationRepository;

    public ReservationCommandService(final ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void deleteReservationById(final Long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public Reservation createReservation(
            final ReservationSchedule schedule,
            final Member member
    ) {
        if (reservationRepository.findByScheduleId(schedule.getId()).isPresent()) {
            log.warn("[DUPLICATE-RESERVATION] 이미 예약 존재 - scheduleId: {}, memberId: {}",
                    schedule.getId(), member.getId());
            throw new BadRequestException("이미 해당 일정에 예약이 존재합니다.");
        }

        return reservationRepository.save(new Reservation(member, schedule));
    }
}
