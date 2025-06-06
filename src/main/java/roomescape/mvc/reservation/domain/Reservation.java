package roomescape.mvc.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.audit.AuditedEntity;
import roomescape.mvc.payment.domain.Payment;

@Entity
public class Reservation extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationTime reservationTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Theme theme;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;
    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;

    protected Reservation() {
    }

    public Reservation(
            Long id, LocalDate date, ReservationTime time, Theme theme, Member member
    ) {
        this.id = id;
        this.date = date;
        this.reservationTime = time;
        this.theme = theme;
        this.member = member;
        this.payment = null;
    }

    public Reservation(
            Long id, LocalDate date, ReservationTime time, Theme theme, Member member, Payment payment
    ) {
        this.id = id;
        this.date = date;
        this.reservationTime = time;
        this.theme = theme;
        this.member = member;
        this.payment = payment;
    }

    public static Reservation createWithoutIdAndPaymentHistory(
            LocalDate date, ReservationTime time, Theme theme, Member member
    ) {
        return new Reservation(null, date, time, theme, member, null);
    }

    public static Reservation createWithoutId(
            LocalDate date, ReservationTime time,
            Theme theme, Member member, Payment paymentHistory
    ) {
        return new Reservation(null, date, time, theme, member, paymentHistory);
    }

    public boolean isPastDateTime() {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, reservationTime.getStartAt());
        return reservationDateTime.isBefore(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getReservationTime() {
        return reservationTime;
    }

    public Theme getTheme() {
        return theme;
    }

    public Member getMember() {
        return member;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reservation reservation = (Reservation) o;
        if (getId() == null || reservation.getId() == null) {
            return false;
        }
        return Objects.equals(id, reservation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
