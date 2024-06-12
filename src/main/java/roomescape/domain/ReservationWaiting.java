package roomescape.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;

@Entity
@SQLRestriction("deleted_at is NULL")
@SQLDelete(sql = "UPDATE reservation_waiting SET deleted_at = NOW() WHERE id = ?")
public class ReservationWaiting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Embedded
    @AttributeOverride(name = "date", column = @Column(nullable = false))
    private ReservationDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationTime time;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Theme theme;

    private LocalDateTime deniedAt;

    protected ReservationWaiting() {
    }

    public ReservationWaiting(LocalDateTime now,
                              Member member,
                              ReservationDate date,
                              ReservationTime time,
                              Theme theme) {
        this(null, now, member, date, time, theme, null);
    }

    public ReservationWaiting(LocalDateTime now,
                              Member member,
                              ReservationDate date,
                              ReservationTime time,
                              Theme theme,
                              LocalDateTime deniedAt) {
        this(null, now, member, date, time, theme, deniedAt);
    }

    public ReservationWaiting(Long id,
                              LocalDateTime now,
                              Member member,
                              ReservationDate date,
                              ReservationTime time,
                              Theme theme,
                              LocalDateTime deniedAt) {
        validateDateTime(now, date, time);
        this.member = member;
        this.id = id;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.deniedAt = deniedAt;
    }

    private void validateDateTime(LocalDateTime now, ReservationDate date, ReservationTime time) {
        LocalDateTime waitingDateTime = LocalDateTime.of(date.getDate(), time.getStartAt());
        if (waitingDateTime.isBefore(now)) {
            throw new RoomescapeException(RoomescapeErrorCode.PAST_REQUEST,
                    String.format("지나간 시간에 대한 에약 대기는 생성할 수 없습니다. (dateTime: %s)", waitingDateTime)
            );
        }
    }

    public boolean hasSameMemberWith(Reservation reservation) {
        Member reservationMember = reservation.getMember();
        return member.equals(reservationMember);
    }

    public boolean hasMemberId(Long memberId) {
        return memberId.equals(member.getId());
    }

    public boolean isAllowed() {
        return deniedAt == null;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public ReservationDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public String getDeniedAt() {
        if (deniedAt == null) {
            return null;
        }
        return deniedAt.toString();
    }

    public void setDeniedAt(LocalDateTime deniedAt) {
        this.deniedAt = deniedAt;
    }
}
