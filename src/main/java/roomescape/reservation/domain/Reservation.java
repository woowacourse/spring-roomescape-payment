package roomescape.reservation.domain;

import jakarta.persistence.Column;
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
import java.time.LocalTime;
import roomescape.common.domain.AuditedEntity;
import roomescape.member.domain.Member;

@Entity
public class Reservation extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime time;

    protected Reservation() {
    }

    public Reservation(Member member, LocalDate date, Theme theme, ReservationTime time, ReservationStatus status) {
        this(null, member, date, theme, time, status);
    }

    public Reservation(Long id, Member member, LocalDate date, Theme theme, ReservationTime time, ReservationStatus status) {
        validateLastDate(date);
        this.id = id;
        this.member = member;
        this.date = date;
        this.theme = theme;
        this.time = time;
        this.status = status;
    }

    private void validateLastDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("지난 날짜는 예약할 수 없습니다.");
        }
    }

    public boolean sameDate(LocalDate otherDate) {
        return date.equals(otherDate);
    }

    public boolean sameThemeId(Long otherThemeId) {
        return theme.sameThemeId(otherThemeId);
    }

    public boolean sameTimeId(Long otherTimeId) {
        return time.sameTimeId(otherTimeId);
    }

    public void updateStatus(ReservationStatus updateStatus) {
        this.status = updateStatus;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Theme getTheme() {
        return theme;
    }

    public String getThemeName() {
        return theme.getName();
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public LocalTime getStartAt() {
        return time.getStartAt();
    }

    public ReservationStatus getStatus() {
        return status;
    }
}
