package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.approval.domain.Approval;
import roomescape.member.domain.Member;
import roomescape.reservation.exception.AlreadyApprovedException;
import roomescape.reservation.exception.InvalidApprovalException;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded
    private ReservationSpec spec;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationState reservationState = ReservationState.PENDING;

    public Reservation(Member member, ReservationSpec spec) {
        this.member = member;
        this.spec = spec;
    }

    public void approveWith(Approval approval) {
        validateApproval(approval);
        validateNotApproved();
        this.reservationState = ReservationState.APPROVED;
    }

    private void validateApproval(Approval approval) {
        if (!approval.isApproved()) {
            throw new InvalidApprovalException();
        }
    }

    private void validateNotApproved() {
        if (this.reservationState == ReservationState.APPROVED) {
            throw new AlreadyApprovedException();
        }
    }

    public LocalDate getDate() {
        return spec.getDate().getValue();
    }

    public ReservationTime getTime() {
        return spec.getTime();
    }

    public Theme getTheme() {
        return spec.getTheme();
    }
}
