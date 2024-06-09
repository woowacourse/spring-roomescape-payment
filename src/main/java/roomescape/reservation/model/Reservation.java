package roomescape.reservation.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;
import roomescape.member.model.Member;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ReservationTime time;

    @ManyToOne
    private Theme theme;

    @ManyToOne
    private Member member;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Embedded
    private ReservationDate date;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    protected Reservation() {
    }

    public Reservation(
            final ReservationStatus status,
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member,
            final PaymentStatus paymentStatus
    ) {
        this(
                null,
                status,
                new ReservationDate(date),
                time,
                theme,
                member,
                paymentStatus
        );
    }

    public Reservation(
            final Long id,
            final ReservationStatus status,
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member,
            final PaymentStatus paymentStatus
    ) {
        this(
                id,
                status,
                new ReservationDate(date),
                time,
                theme,
                member,
                paymentStatus
        );
    }

    protected Reservation(
            final Long id,
            final ReservationStatus status,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member,
            final PaymentStatus paymentStatus
    ) {
        checkRequiredData(status, time, theme, member);
        this.id = id;
        this.status = status;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
        this.paymentStatus = paymentStatus;
    }

    private static void checkRequiredData(
            final ReservationStatus status,
            final ReservationTime reservationTime,
            final Theme theme,
            final Member member
    ) {
        if (status == null || reservationTime == null || theme == null || member == null) {
            throw new IllegalArgumentException("예약 상태, 시간, 테마, 회원 정보는 Null을 입력할 수 없습니다.");
        }
    }

    public boolean isPaid() {
        return paymentStatus.isPaid();
    }

    public Long getId() {
        return id;
    }

    public ReservationDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public Member getMember() {
        return member;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Reservation other = (Reservation) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
