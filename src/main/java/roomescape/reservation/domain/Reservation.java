package roomescape.reservation.domain;

import jakarta.persistence.*;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    private Theme theme;

    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    protected Reservation() {
    }

    public Reservation(final Member member, final LocalDate date, final ReservationTime time, final Theme theme, final Payment payment, final ReservationStatus reservationStatus) {
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.payment = payment;
        this.reservationStatus = reservationStatus;
    }

    public static Reservation createPendingWithoutId(final LocalDateTime now, final Member member,
                                                     final LocalDate reservationDate,
                                                     final ReservationTime time, final Theme theme, final Payment payment
    ) {
        validateReservationDateTime(now, reservationDate, time);
        return new Reservation(member, reservationDate, time, theme, payment, ReservationStatus.PENDING);
    }

    private static void validateReservationDateTime(final LocalDateTime now, final LocalDate reservationDate,
                                                    final ReservationTime time) {
        LocalDate nowDate = now.toLocalDate();
        if (reservationDate.isBefore(nowDate)) {
            throw new IllegalArgumentException("예약할 수 없는 날짜와 시간입니다.");
        }

        LocalTime nowTime = now.toLocalTime();
        if (nowDate.isEqual(reservationDate) && time.isBeforeTime(nowTime)) {
            throw new IllegalArgumentException("예약할 수 없는 날짜와 시간입니다.");
        }
    }

    public boolean isSameTime(final ReservationTime time) {
        return this.time.isSameTime(time);
    }

    public void changePendingToPaid() {
        if (this.reservationStatus != ReservationStatus.PENDING) {
            throw new IllegalStateException("현재 예약의 상태를 변경할 수 없습니다. 현재 상태 : " + this.reservationStatus);
        }
        this.reservationStatus = ReservationStatus.PAID;
    }

    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Reservation that)) {
            return false;
        }

        if (getId() == null && that.getId() == null) {
            return false;
        }

        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return member.getName();
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getTimeId() {
        return time.getId();
    }

    public Long getMemberId() {
        return member.getId();
    }

    public LocalTime getReservationTime() {
        return time.getStartAt();
    }

    public Long getThemeId() {
        return theme.getId();
    }

    public String getThemeDescription() {
        return theme.getDescription();
    }

    public String getThemeName() {
        return theme.getName();
    }

    public String getThemeThumbnail() {
        return theme.getThumbnail();
    }

    public Payment getPayment() {
        return payment;
    }

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }
}
