package roomescape;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@Component
@Transactional
public class DBHelper {

    @PersistenceContext
    private EntityManager em;

    public Reservation insertReservation(Reservation reservation) {
        if(reservation.getMember().getId() == null) {
            em.persist(reservation.getMember());
        }
        if(reservation.getTime().getId() == null) {
            em.persist(reservation.getTime());
        }
        if(reservation.getTheme().getId() == null) {
            em.persist(reservation.getTheme());
        }
        if(reservation.getId() == null) {
            em.persist(reservation);
        }
        if(reservation.getId() == null) {
            em.persist(reservation);
        }
        em.flush();

        return reservation;
    }

    public WaitingReservation insertWaiting(WaitingReservation waiting) {
        if(waiting.getMember().getId() == null) {
            em.persist(waiting.getMember());
        }
        if(waiting.getTime().getId() == null) {
            em.persist(waiting.getTime());
        }
        if(waiting.getTheme().getId() == null) {
            em.persist(waiting.getTheme());
        }
        if(waiting.getId() == null) {
            em.persist(waiting);
        }
        if(waiting.getId() == null) {
            em.persist(waiting);
        }
        em.flush();

        return waiting;
    }

    public Member insertMember(Member member) {
        em.persist(member);
        em.flush();
        return member;
    }

    public ReservationTime insertTime(ReservationTime reservationTime) {
        em.persist(reservationTime);
        em.flush();
        return reservationTime;
    }

    public Theme insertTheme(Theme theme) {
        em.persist(theme);
        em.flush();

        return theme;
    }

    public Payment insertCompletedPayment(Reservation reservation) {
        Payment payment = Payment.builder()
                .status(PaymentStatus.COMPLETED)
                .reservation(reservation)
                .member(reservation.getMember())
                .paymentKey("test-payment-key" + UUID.randomUUID())
                .orderId("test-orderId" + UUID.randomUUID())
                .build();
        em.persist(payment);
        em.flush();

        return payment;
    }

    public Payment insertNotPaidPayment(Reservation reservation) {
        Payment payment = Payment.builder()
                .status(PaymentStatus.NOT_PAID)
                .reservation(reservation)
                .member(reservation.getMember())
                .paymentKey("test-payment-key" + UUID.randomUUID())
                .orderId("test-orderId" + UUID.randomUUID())
                .build();
        em.persist(payment);
        em.flush();

        return payment;
    }

    public Payment insertPaymentOfStatus(Reservation reservation, PaymentStatus paymentStatus) {
        Payment payment = Payment.builder()
                .status(paymentStatus)
                .reservation(reservation)
                .member(reservation.getMember())
                .paymentKey("test-payment-key" + UUID.randomUUID())
                .orderId("test-orderId" + UUID.randomUUID())
                .build();
        em.persist(payment);
        em.flush();

        return payment;
    }
}
