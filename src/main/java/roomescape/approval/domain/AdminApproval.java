package roomescape.approval.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;

@Entity
@Getter
@NoArgsConstructor
@DiscriminatorValue("ADMIN_APPROVAL")
public class AdminApproval extends Approval {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public AdminApproval(Reservation reservation, Member member) {
        super(reservation);
        this.member = member;
    }

    @Override
    public ApprovalType getType() {
        return ApprovalType.ADMIN_APPROVAL;
    }
}
