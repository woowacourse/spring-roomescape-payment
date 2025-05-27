package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import roomescape.member.domain.Member;
import roomescape.schedule.domain.ReservationSchedule;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

@Entity
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "schedule_id")
    private ReservationSchedule schedule;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "payment_key")
    private String paymentKey;

    protected Reservation() {
    }

    public Reservation(
            final Long id,
            final Member member,
            final ReservationSchedule schedule,
            final String orderId,
            final Long amount
    ) {
        this.id = id;
        this.member = Objects.requireNonNull(member);
        this.schedule = Objects.requireNonNull(schedule);
        this.orderId = Objects.requireNonNull(orderId);
        this.amount = Objects.requireNonNull(amount);
    }

    public Reservation(
            final Long id,
            final Member member,
            final ReservationSchedule schedule
    ) {
        this.id = id;
        this.member = Objects.requireNonNull(member);
        this.schedule = Objects.requireNonNull(schedule);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return schedule.getDate();
    }

    public ReservationTime getReservationTime() {
        return schedule.getReservationTime();
    }

    public LocalTime getStartAt() {
        return schedule.getStartAt();
    }

    public Theme getTheme() {
        return schedule.getTheme();
    }

    public ReservationSchedule getSchedule() {
        return schedule;
    }

    public Member getMember() {
        return member;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}
