package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.common.exception.ReservationException;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.theme.domain.Theme;

@Getter
@ToString
@NoArgsConstructor
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    private Theme theme;

    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;

    public Reservation(
            final Long id,
            final Member member,
            final Theme theme,
            final LocalDate date,
            final ReservationTime time,
            final Payment payment
    ) {
        validateMember(member);
        validateDate(date);
        validateTime(time);
        validateTheme(theme);
        validatePayment(payment);
        this.id = id;
        this.member = member;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.payment = payment;
    }

    public Reservation(
            final Member member,
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Payment payment
    ) {
        validateMember(member);
        validateDate(date);
        validateTime(time);
        validateTheme(theme);
        validatePayment(payment);
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.payment = payment;
    }

    private void validateMember(final Member member) {
        if (member == null) {
            throw new ReservationException("Member cannot be null");
        }
    }

    private void validateDate(final LocalDate date) {
        if (date == null) {
            throw new ReservationException("Date cannot be null");
        }
    }

    private void validateTime(final ReservationTime time) {
        if (time == null) {
            throw new ReservationException("Time cannot be null");
        }
    }

    private void validateTheme(final Theme theme) {
        if (theme == null) {
            throw new ReservationException("Theme cannot be null");
        }
    }

    private void validatePayment(final Payment payment) {
        if (payment == null) {
            throw new ReservationException("Payment cannot be null");
        }
    }
}
