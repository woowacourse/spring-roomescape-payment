package roomescape.waiting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.User;

import java.time.LocalDate;

@Entity
@Table(name = "waiting")
public class Waiting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime time;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    protected Waiting() {
    }

    public Waiting(LocalDate date, ReservationTime time, Theme theme, User member) {
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
    }

    public static Waiting of(LocalDate date, ReservationTime reservationTime, Theme theme, User user) {
        return new Waiting(date, reservationTime, theme, user);
    }

    public boolean isSameMember(User compare) {
        return this.member.equals(compare);
    }

    public Long getId() {
        return id;
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

    public User getMember() {
        return member;
    }
}
