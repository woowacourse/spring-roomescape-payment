package roomescape.domain.waiting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.member.entity.Member;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.time.entity.ReservationTime;
import roomescape.domain.waiting.exception.WaitingException;

@Getter
@Table(name = "waiting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Waiting {
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

    public Waiting(final Long id, final Member member, final ReservationTime time, final Theme theme,
                   final LocalDate date) {
        validateMember(member);
        validateDate(date);
        validateTime(time);
        validateTheme(theme);
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public Waiting(final Member member, final ReservationTime time, final Theme theme, final LocalDate date) {
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
            throw new WaitingException("멤버는 NULL이 허용되지 않습니다.");
        }
    }

    private void validateDate(final LocalDate date) {
        if (date == null) {
            throw new WaitingException("날짜는 NULL이 허용되지 않습니다.");
        }
    }

    private void validateTime(final ReservationTime time) {
        if (time == null) {
            throw new WaitingException("시간은 NULL이 허용되지 않습니다.");
        }
    }

    private void validateTheme(final Theme theme) {
        if (theme == null) {
            throw new WaitingException("테마는 NULL이 허용되지 않습니다.");
        }
    }
}
