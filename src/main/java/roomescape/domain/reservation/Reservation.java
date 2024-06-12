package roomescape.domain.reservation;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.schedule.ReservationTime;
import roomescape.domain.theme.Theme;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@SQLDelete(sql = "UPDATE reservation SET status = 'CANCELED' WHERE id = ?")
@SQLRestriction("status <> 'CANCELED'")
@EntityListeners(AuditingEntityListener.class)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Member member;

    @ManyToOne
    private ReservationDetail detail;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToOne
    private Payment payment;

    protected Reservation() {
    }

    public Reservation(Member member, ReservationDetail detail, ReservationStatus status, Payment payment) {
        this.member = member;
        this.detail = detail;
        this.status = status;
        this.payment = payment;
    }

    public Reservation(Member member, ReservationDetail detail, ReservationStatus status) {
        this(member, detail, status, null);
    }

    public boolean isReservationOf(Member member) {
        return this.member.equals(member);
    }

    public boolean isPast() {
        return detail.getSchedule().isBeforeNow();
    }

    public boolean isReserved() {
        return status.isReserved();
    }

    public boolean isPendingPayment() {
        return status.isPendingPayment();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation other)) {
            return false;
        }
        return Objects.equals(id, other.id);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return detail.getDate();
    }

    public ReservationTime getReservationTime() {
        return detail.getReservationTime();
    }

    public Theme getTheme() {
        return detail.getTheme();
    }

    public LocalTime getTime() {
        return detail.getTime();
    }

    public ReservationDetail getDetail() {
        return detail;
    }

    public Payment getPayment() {
        return payment;
    }

    public void reserved() {
        this.status = ReservationStatus.RESERVED;
    }

    public void pendingPayment() {
        this.status = ReservationStatus.PENDING_PAYMENT;
    }

    public void paid(Payment payment) {
        this.payment = payment;
        this.status = ReservationStatus.RESERVED;
    }
}
