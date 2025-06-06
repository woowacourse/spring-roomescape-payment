package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.common.exception.WaitingException;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.theme.domain.Theme;

@Getter
@Table(name = "waiting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Waiting {
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

    public Waiting(
            final Long id,
            final Member member,
            final ReservationTime time,
            final Theme theme,
            final LocalDate date,
            final Payment payment
    ) {
        validateMember(member);
        validateDate(date);
        validateTime(time);
        validateTheme(theme);
        validatePayment(payment);
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.payment = payment;
    }

    public Waiting(
            final Member member,
            final ReservationTime time,
            final Theme theme,
            final LocalDate date,
            final Payment payment) {
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
            throw new WaitingException("Member cannot be null");
        }
    }

    private void validateDate(final LocalDate date) {
        if (date == null) {
            throw new WaitingException("Date cannot be null");
        }
    }

    private void validateTime(final ReservationTime time) {
        if (time == null) {
            throw new WaitingException("Time cannot be null");
        }
    }

    private void validateTheme(final Theme theme) {
        if (theme == null) {
            throw new WaitingException("Theme cannot be null");
        }
    }

    private void validatePayment(final Payment payment) {
        if (payment == null) {
            throw new WaitingException("Payment cannot be null");
        }
    }
}
