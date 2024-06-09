package roomescape.reservation.domain.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.BadRequestException;
import roomescape.exception.ForbiddenException;
import roomescape.member.domain.Member;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class MemberReservation {

    private static final long CAN_CONFIRM_RANK = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
    @JoinColumn(name = "reservation_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;
    @Enumerated(value = EnumType.STRING)
    @ColumnDefault(value = "'CONFIRMATION'")
    private ReservationStatus status;
    @CreatedDate
    private LocalDateTime createdAt;

    protected MemberReservation() {
    }

    public MemberReservation(Long id, Member member, Reservation reservation, ReservationStatus status) {
        this.id = id;
        this.member = member;
        this.reservation = reservation;
        this.status = status;
    }

    public MemberReservation(Long id, Member member, Reservation reservation) {
        this(id, member, reservation, ReservationStatus.CONFIRMATION);
    }

    public MemberReservation(Member member, Reservation reservation, ReservationStatus status) {
        this(null, member, reservation, status);
    }

    public void validateDuplicated(MemberReservation other) {
        if (reservation.equals(other.reservation)) {
            validateDuplicatedType(other);
        }
    }

    private void validateDuplicatedType(MemberReservation other) {
        if (member.equals(other.member)) {
            throw new BadRequestException("이미 예약한 테마입니다.");
        }

        if (status.isNotWaiting()) {
            throw new BadRequestException("다른 사용자가 이미 예약한 테마입니다.");
        }
    }

    public void validateWaitingStatus() {
        if (isNotWaitingStatus()) {
            throw new BadRequestException("해당 예약은 대기 상태가 아닙니다.");
        }
    }

    public boolean isNotWaitingStatus() {
        return status.isNotWaiting();
    }

    public void validatePendingStatus() {
        if (!status.isPending()) {
            throw new BadRequestException("해당 예약은 결제 대기 상태가 아닙니다.");
        }
    }

    public boolean isConfirmationStatus() {
        return status.isConfirmation();
    }

    public void validateRankConfirm(Long waitingRank) {
        if (!waitingRank.equals(CAN_CONFIRM_RANK)) {
            throw new BadRequestException("예약 대기는 순서대로 승인할 수 있습니다.");
        }
    }

    public void validateIsOwner(LoginMember loginMember) {
        if (!member.isSameMember(loginMember)) {
            throw new ForbiddenException("본인의 예약 대기만 삭제할 수 있습니다.");
        }
    }

    public void validateIsBeforeNow() {
        reservation.validateIsBeforeNow();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
