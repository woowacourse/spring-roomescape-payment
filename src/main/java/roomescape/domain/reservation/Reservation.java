package roomescape.domain.reservation;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import roomescape.common.exception.BusinessException;
import roomescape.domain.member.Member;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.infrastructure.converter.ReservationStatusConverter;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Accessors(fluent = true)
@EqualsAndHashCode(of = "id")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    private TimeSlot time;

    @ManyToOne
    private Theme theme;

    @ManyToOne
    private Member member;

    @Convert(converter = ReservationStatusConverter.class)
    private Status status;

    private Reservation(final Long id,
                        final LocalDate date,
                        final TimeSlot time,
                        final Theme theme,
                        final Member member,
                        final Status status
    ) {
        validateIsNonNull(date);
        validateIsNonNull(time);
        validateIsNonNull(theme);
        validateIsNonNull(member);
        validateIsNonNull(status);

        this.id = id;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.status = status;
        this.member = member;
    }

    public static Reservation createWithoutId(final LocalDate date,
                                              final TimeSlot time,
                                              final Theme theme,
                                              final Member member,
                                              final Status status
    ) {
        return new Reservation(null, date, time, theme, member, status);
    }

    private void validateIsNonNull(final Object object) {
        if (object == null) {
            throw new BusinessException("예약 정보는 필수입니다.");
        }
    }

    public boolean isCannotReserveDateTime(final LocalDateTime dateTime) {
        if (date.isBefore(dateTime.toLocalDate())) {
            return true;
        }

        return date.isEqual(dateTime.toLocalDate()) && time.isBefore(dateTime.toLocalTime());
    }

    public boolean isSameTime(final TimeSlot time) {
        return this.time.isSame(time.startAt());
    }

    public boolean isSameMember(final Member member) {
        return this.member.equals(member);
    }

    public void updateMember(final Member member) {
        this.member = member;
    }
}
