package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import roomescape.common.exception.InvalidReservationException;
import roomescape.member.domain.Member;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

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

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme, ReservationStatus status) {
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.status = status;
    }

    public static Reservation createWithoutId(
            final LocalDateTime now,
            final Member member,
            final LocalDate reservationDate,
            final ReservationTime time,
            final Theme theme,
            final ReservationStatus status
    ) {
        validateReservationDateTime(now, reservationDate, time);
        return new Reservation(member, reservationDate, time, theme, status);
    }

    private static void validateReservationDateTime(final LocalDateTime now, final LocalDate reservationDate,
                                                    final ReservationTime time) {
        LocalDate nowDate = now.toLocalDate();
        if (reservationDate.isBefore(nowDate)) {
            throw new InvalidReservationException("예약할 수 없는 날짜와 시간입니다.");
        }

        LocalTime nowTime = now.toLocalTime();
        if (nowDate.isEqual(reservationDate) && time.isBeforeTime(nowTime)) {
            throw new InvalidReservationException("예약할 수 없는 날짜와 시간입니다.");
        }
    }

    public boolean isSameTime(final ReservationTime time) {
        return this.time.isSameTime(time);
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

    public ReservationStatus getStatus() {
        return status;
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

    public void confirm() {
        this.status = ReservationStatus.DONE;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }
}
