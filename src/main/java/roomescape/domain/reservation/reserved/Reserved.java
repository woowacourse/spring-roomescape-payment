package roomescape.domain.reservation.reserved;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;

@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Entity
@DiscriminatorValue("RESERVED")
public class Reserved extends Reservation {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    private Reserved(final User user, final LocalDate date, final TimeSlot timeSlot, final Theme theme) {
        super(date, timeSlot, theme, user);
    }

    public static Reserved register(final User user, final LocalDate date, final TimeSlot timeSlot, final Theme theme) {

        Reserved reserved = new Reserved(user, date, timeSlot, theme);
        return reserved;
    }

    public static Reserved fromPendingPayment(final PendingPayment pendingPayment) {
        Reserved reserved = new Reserved(
                pendingPayment.getUser(), pendingPayment.getDate(),
                pendingPayment.getTimeSlot(), pendingPayment.getTheme()
        );
        return reserved;
    }

    public void registerPayment(Payment payment) {
        this.payment = payment;
    }
}
