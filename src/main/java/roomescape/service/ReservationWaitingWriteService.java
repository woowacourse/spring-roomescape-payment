package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.NotFoundException;
import roomescape.model.Reservation;
import roomescape.model.ReservationTime;
import roomescape.model.Theme;
import roomescape.model.Waiting;
import roomescape.repository.ReservationRepository;
import roomescape.repository.WaitingRepository;

import java.time.LocalDate;

@Transactional
@Service
public class ReservationWaitingWriteService {

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;

    public ReservationWaitingWriteService(ReservationRepository reservationRepository,
                                          WaitingRepository waitingRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
    }

    public void deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() ->
                new NotFoundException("해당 id:[%s] 값으로 예약된 내역이 존재하지 않습니다.".formatted(id)));
        reservationRepository.deleteById(id);

        Theme theme = reservation.getTheme();
        LocalDate date = reservation.getDate();
        ReservationTime time = reservation.getTime();
        if (waitingRepository.existsWaitingByThemeAndDateAndTime(theme, date, time)) {
            convertWaitingToReservation(theme, date, time);
        }
    }

    private void convertWaitingToReservation(Theme theme, LocalDate date, ReservationTime time) {
        Waiting waiting = waitingRepository.findFirstByThemeAndDateAndTime(theme, date, time).orElseThrow(() ->
                new NotFoundException("해당 테마:[%s], 날짜:[%s], 시간:[%s] 값으로 예약된 예약 대기 내역이 존재하지 않습니다.".formatted(theme.getName(), date, time.getStartAt())));

        reservationRepository.save(Reservation.paymentWaitingStatusOf(date, time, theme, waiting.getMember()));

        waitingRepository.deleteById(waiting.getId());
    }
}
