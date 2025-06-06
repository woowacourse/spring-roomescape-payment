package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import org.springframework.lang.Nullable;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;
import roomescape.payment.domain.Payment;
import roomescape.theme.domain.Theme;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Nullable  // NOTE. 어떻게 생각하시는지 여쭤보기
    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;

    protected Reservation() {
    }

    public Reservation(
        final Long id,
        final LocalDate date,
        final ReservationTime time,
        final Theme theme,
        final Member member,
        final ReservationStatus status,
        final Payment payment
    ) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
        this.status = status;
        this.payment = payment;
    }

    public Reservation(final LocalDate date, final ReservationTime time, final Theme theme, final Member member) {
        this(null, date, time, theme, member, ReservationStatus.CONFIRMED, null);
    }

    public Reservation(final LocalDate date, final ReservationTime time, final Theme theme, final Member member, final Payment payment) {
        this(null, date, time, theme, member, ReservationStatus.CONFIRMED, payment);
    }

    public boolean hasConflictWith(final ReservationTime reservationTime, final Theme theme) {
        final LocalTime startAt = time.getStartAt();
        return reservationTime.hasConflict(theme.getDuration(), startAt);
    }

    public Long getId() {
        return id;
    }

    public MemberName getName() {
        return member.getName();
    }

    public LocalDate getDate() {
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

    public String getThemeName() {
        return theme.getName();
    }

    public LocalTime getStartAt() {
        return time.getStartAt();
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void cancel() {
        if (status == ReservationStatus.CANCELED) {
            throw new IllegalStateException("이미 취소된 예약입니다.");
        }
        status = ReservationStatus.CANCELED;
    }
}
