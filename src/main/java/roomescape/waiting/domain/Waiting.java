package roomescape.waiting.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.user.domain.User;

import java.time.LocalDate;
import java.util.Objects;

@Getter
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

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Waiting waiting = (Waiting) object;
        return Objects.equals(id, waiting.id) && Objects.equals(date, waiting.date) && Objects.equals(time, waiting.time) && Objects.equals(theme, waiting.theme) && Objects.equals(member, waiting.member);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, time, theme, member);
    }
}
