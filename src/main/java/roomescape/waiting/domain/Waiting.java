package roomescape.waiting.domain;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;

@Tag(name = "예약 대기 엔티티", description = "예약 대기에 필요한 정보를 관리한다. 예약 대기는 예약 id와 대기를 신청한 멤버 id를 갖고 있다.")
@Entity
public class Waiting {

    private static final int NULL_ID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "reservation_id", referencedColumnName = "id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public Waiting() {
    }

    public Waiting(Reservation reservation, Member member, LocalDateTime createdAt) {
        this.id = NULL_ID;
        this.reservation = reservation;
        this.member = member;
        this.createdAt = createdAt;
    }

    public Waiting(long id, Reservation reservation, Member member, LocalDateTime createdAt) {
        this.id = id;
        this.reservation = reservation;
        this.member = member;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }
    
    public Member getMember() {
        return member;
    }
}
