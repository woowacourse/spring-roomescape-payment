package roomescape.payment.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ReservationTime reservationTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Theme theme;

    private LocalDate date;

    @Embedded
    private PaymentInfo paymentInfo;

    @Enumerated(value = EnumType.STRING)
    private PaymentGateway paymentGateway;

    protected Payment() {
    }

    public Payment(final Long id, final Member member, final ReservationTime reservationTime,
        final Theme theme, final LocalDate date, final PaymentInfo paymentInfo,
        final PaymentGateway paymentGateway) {
        this.id = id;
        this.member = member;
        this.reservationTime = reservationTime;
        this.theme = theme;
        this.date = date;
        this.paymentInfo = paymentInfo;
        this.paymentGateway = paymentGateway;
    }

    public Payment(final Member member, final ReservationTime reservationTime,
        final Theme theme, final LocalDate date, final PaymentInfo paymentInfo,
        final PaymentGateway paymentGateway) {
        this(null, member, reservationTime, theme, date, paymentInfo, paymentGateway);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment waiting = (Payment) o;
        if (id == null || waiting.id == null) {
            return false;
        }
        return Objects.equals(id, waiting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
