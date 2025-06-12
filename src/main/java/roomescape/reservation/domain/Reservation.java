package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import roomescape.common.exception.BadRequestException;
import roomescape.common.utils.Validator;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.reservation.domain.utils.PaymentMethodConverter;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldNameConstants(level = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Embedded
    private ReservationDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Theme theme;

    @Convert(converter = PaymentMethodConverter.class)
    @Column(nullable = false)
    private PaymentMethod status;

    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;

    private static Reservation of(
            final Long id,
            final Member member,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final PaymentMethod status,
            final Payment payment
    ) {
        validate(member, date, time, theme, status);
        return new Reservation(id, member, date, time, theme, status, payment);
    }

    public static Reservation withId(
            final Long id,
            final Member member,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final PaymentMethod status,
            final Payment payment
    ) {
        return of(id, member, date, time, theme, status, payment);
    }

    public static Reservation withId(
            final Long id,
            final Member member,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final PaymentMethod status
    ) {
        return of(id, member, date, time, theme, status, null);
    }

    public static Reservation withoutId(
            final Member member,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final PaymentMethod status
    ) {
        validatePast(date, time);
        return of(null, member, date, time, theme, status, null);
    }

    public static Reservation withoutId(
            final Member member,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final PaymentMethod status,
            final Payment payment
    ) {
        validatePast(date, time);
        return of(null, member, date, time, theme, status, payment);
    }

    private static void validate(
            final Member member,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final PaymentMethod status
    ) {

        Validator.of(Reservation.class)
                .notNullField(Fields.member, member)
                .notNullField(Fields.date, date)
                .notNullField(Fields.time, time)
                .notNullField(Fields.theme, theme)
                .notNullField(Fields.status, status);
    }

    public static void validatePast(final ReservationDate date, final ReservationTime time) {
        final LocalDateTime now = LocalDateTime.now();
        if (date.isAfter(now.toLocalDate())) {
            return;
        }
        if (date.isBefore(now.toLocalDate())) {
            throw new BadRequestException("지난 날짜는 예약할 수 없습니다.");
        }
        if (time.isBefore(now.toLocalTime())) {
            throw new BadRequestException("이미 지난 시간에는 예약할 수 없습니다.");
        }
    }

    public void confirmPayment(PaymentMethod paymentMethod, Payment payment) {
        this.status = paymentMethod;
        this.payment = payment;
    }
}
