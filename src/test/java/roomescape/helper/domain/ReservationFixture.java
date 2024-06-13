package roomescape.helper.domain;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;

@Component
public class ReservationFixture {
    @Autowired
    private ReservationRepository reservationRepository;

    public Reservation createPastReservation(ReservationTime reservationTime, Theme theme, Member member) {
        Reservation reservation = new Reservation(LocalDate.of(2000, 4, 1), reservationTime, theme, member);
        return reservationRepository.save(reservation);
    }

    public Reservation createFutureReservation(ReservationTime reservationTime, Theme theme, Member member) {
        Reservation reservation = new Reservation(LocalDate.of(2000, 4, 8), reservationTime, theme, member);
        return reservationRepository.save(reservation);
    }

    public Reservation createReservationWithDate(
            LocalDate date, ReservationTime reservationTime, Theme theme, Member member) {
        Reservation reservation = new Reservation(date, reservationTime, theme, member);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> findAllReservation() {
        return reservationRepository.findAll();
    }
}
