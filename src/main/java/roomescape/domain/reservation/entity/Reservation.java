package roomescape.domain.reservation.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import roomescape.domain.member.entity.Member;
import roomescape.domain.reservation.exception.ReservationException;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.time.entity.ReservationTime;

@Getter
@ToString
@NoArgsConstructor
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    private Theme theme;

    public Reservation(final Long id, final Member member, final Theme theme, final LocalDate date,
                       final ReservationTime time) {
        validateMember(member);
        validateDate(date);
        validateTime(time);
        validateTheme(theme);
        this.id = id;
        this.member = member;
        this.theme = theme;
        this.date = date;
        this.time = time;
    }

    public Reservation(final Member member, final LocalDate date, final ReservationTime time, final Theme theme) {
        validateMember(member);
        validateDate(date);
        validateTime(time);
        validateTheme(theme);
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    private void validateMember(final Member member) {
        if (member == null) {
            throw new ReservationException("멤버는 NULL이 허용되지 않습니다.");
        }
    }

    private void validateDate(final LocalDate date) {
        if (date == null) {
            throw new ReservationException("날짜는 NULL이 허용되지 않습니다.");
        }
    }

    private void validateTime(final ReservationTime time) {
        if (time == null) {
            throw new ReservationException("시간은 NULL이 허용되지 않습니다.");
        }
    }

    private void validateTheme(final Theme theme) {
        if (theme == null) {
            throw new ReservationException("테마는 NULL이 허용되지 않습니다.");
        }
    }
}
