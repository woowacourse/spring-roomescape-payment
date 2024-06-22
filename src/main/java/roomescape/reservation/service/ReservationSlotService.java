package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.repository.ReservationSlotRepository;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class ReservationSlotService {

    private final ReservationSlotRepository reservationSlotRepository;

    public ReservationSlotService(ReservationSlotRepository reservationSlotRepository) {
        this.reservationSlotRepository = reservationSlotRepository;
    }

    public ReservationSlot findReservationSlot(LocalDate date, ReservationTime reservationTime, Theme theme) {
        return reservationSlotRepository.findByDateAndTimeAndTheme(date, reservationTime, theme)
                .orElseGet(() -> reservationSlotRepository.save(new ReservationSlot(date, reservationTime, theme)));
    }

    @Transactional
    public void deleteById(Long id) {
        reservationSlotRepository.deleteById(id);
    }
}
