package roomescape;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@Component
@Transactional
public class DBHelper {

    @PersistenceContext
    private EntityManager em;

    public Long insertReservation(Reservation reservation) {
        if(reservation.getMember().getId() == null) {
            em.persist(reservation.getMember());
        }
        if(reservation.getRoomEscapeInformation().getTime().getId() == null) {
            em.persist(reservation.getRoomEscapeInformation().getTime());
        }
        if(reservation.getRoomEscapeInformation().getTheme().getId() == null) {
            em.persist(reservation.getRoomEscapeInformation().getTheme());
        }
        if(reservation.getRoomEscapeInformation().getId() == null) {
            em.persist(reservation.getRoomEscapeInformation());
        }
        if(reservation.getId() == null) {
            em.persist(reservation);
        }
        em.flush();

        return reservation.getId();
    }

    public WaitingReservation insertWaiting(WaitingReservation waiting) {
        if(waiting.getMember().getId() == null) {
            em.persist(waiting.getMember());
        }
        if(waiting.getRoomEscapeInformation().getTime().getId() == null) {
            em.persist(waiting.getRoomEscapeInformation().getTime());
        }
        if(waiting.getRoomEscapeInformation().getTheme().getId() == null) {
            em.persist(waiting.getRoomEscapeInformation().getTheme());
        }
        if(waiting.getRoomEscapeInformation().getId() == null) {
            em.persist(waiting.getRoomEscapeInformation());
        }
        if(waiting.getId() == null) {
            em.persist(waiting);
        }
        em.flush();

        return waiting;
    }

    public void insertMember(Member member) {
        em.persist(member);
        em.flush();
    }

    public void insertTime(ReservationTime reservationTime) {
        em.persist(reservationTime);
        em.flush();
    }

    public void insertTheme(Theme theme) {
        em.persist(theme);
        em.flush();
    }

    public void prepareForBooking(Member member, ReservationTime time, Theme theme) {
        insertMember(member);
        insertTime(time);
        insertTheme(theme);
        em.flush();
    }
}
