package roomescape.reservation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import roomescape.member.domain.Member;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;
import roomescape.theme.domain.Theme;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime reservationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(value = EnumType.STRING)
    private ReservationStatus reservationStatus;

    protected Reservation() {
    }

    public Reservation(
            LocalDate date,
            ReservationTime reservationTime,
            Theme theme,
            Member member,
            ReservationStatus status
    ) {
        this(null, date, reservationTime, theme, member, status);
    }

    public Reservation(
            Long id,
            LocalDate date,
            ReservationTime reservationTime,
            Theme theme,
            Member member,
            ReservationStatus status
    ) {
        validateIsNull(date, reservationTime, theme, member, status);
        this.id = id;
        this.date = date;
        this.reservationTime = reservationTime;
        this.theme = theme;
        this.member = member;
        this.reservationStatus = status;
    }

    private void validateIsNull(LocalDate date, ReservationTime reservationTime, Theme theme, Member member,
                                ReservationStatus reservationStatus) {
        if (date == null || reservationTime == null || theme == null || member == null || reservationStatus == null) {
            throw new RoomEscapeException(ErrorType.REQUEST_DATA_BLANK, String.format("[values: %s]", this),
                    HttpStatus.BAD_REQUEST);
        }
    }

    public Long getMemberId() {
        return member.getId();
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

    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    @JsonIgnore
    public boolean isSameDateAndTime(LocalDate date, ReservationTime time) {
        return this.date.equals(date) && time.getStartAt().equals(this.reservationTime.getStartAt());
    }

    @JsonIgnore
    public boolean isWaiting() {
        return reservationStatus == ReservationStatus.WAITING;
    }

    @JsonIgnore
    public boolean isSameMember(Long memberId) {
        return getMemberId().equals(memberId);
    }
}
