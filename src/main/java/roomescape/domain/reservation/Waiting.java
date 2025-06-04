package roomescape.domain.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.domain.BaseEntity;
import roomescape.domain.member.Member;
import roomescape.infrastructure.error.exception.WaitingException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Waiting extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_time_id")
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    public Waiting(final Member member, final LocalDate date, final ReservationTime time, final Theme theme) {
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    public void validateWaitable(final LocalDateTime currentDateTime) {
        final LocalDateTime waitingDateTime = LocalDateTime.of(date, time.getStartAt());
        if (waitingDateTime.isBefore(currentDateTime)) {
            throw new WaitingException("예약일이 지나 대기 신청을 할 수 없습니다.");
        }
    }

    public boolean canBeApprovedBy(final Member member) {
        return member.isAdmin();
    }

    public boolean canBeCanceledBy(final Member member) {
        return isOwner(member.getId()) || member.isAdmin();
    }

    private boolean isOwner(final Long memberId) {
        return Objects.equals(this.member.getId(), memberId);
    }
}
