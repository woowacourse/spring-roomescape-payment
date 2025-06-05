package roomescape.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import org.hibernate.annotations.CreationTimestamp;
import roomescape.exception.custom.InvalidReservationException;
import roomescape.global.ReservationStatus;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime reservationTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @OneToOne(mappedBy = "reservation")
    private Payment payment;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createAt;

    protected Reservation() {
    }

    public Reservation(Long id,
                       Member member,
                       LocalDate date,
                       ReservationTime reservationTime,
                       Theme theme,
                       ReservationStatus status,
                       Payment payment) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.reservationTime = reservationTime;
        this.theme = theme;
        this.status = status;
        this.payment = payment;
    }

    public Reservation(Member member,
                       LocalDate date,
                       ReservationTime reservationTime,
                       Theme theme,
                       ReservationStatus status) {
        this(null, member, date, reservationTime, theme, status, null);
    }

    public long calculateWaitRank(List<Reservation> allReservations) {
        return allReservations.stream()
                .filter(this::isSameReservationCondition)
                .filter(reservation -> reservation.isCreateAtBeforeOrEqual(this))
                .count();
    }

    private boolean isSameReservationCondition(Reservation target) {
        return this.theme.equals(target.theme)
                && this.date.equals(target.date)
                && this.reservationTime.equals(target.reservationTime)
                && this.status.equals(target.status);
    }

    private boolean isCreateAtBeforeOrEqual(Reservation reservation) {
        return !this.createAt.isAfter(reservation.createAt);
    }

    public boolean isBefore(LocalDateTime compareDateTime) {
        LocalDateTime reservationDateTime = LocalDateTime.of(date, getStartAt());
        return reservationDateTime.isBefore(compareDateTime);
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELED;
    }

    public void waitToPending() {
        if (this.status != ReservationStatus.WAIT) {
            throw new InvalidReservationException("대기 중인 예약이 아닙니다.");
        }

        if (member == null) {
            throw new InvalidReservationException("Member 가 없는 대기 reservation은 결제 대기로 변경할 수 없습니다.");
        }

        this.status = ReservationStatus.PENDING;
    }

    public void payForReservation(Payment payment) {
        this.payment = payment;
        this.status = ReservationStatus.RESERVED;
        payment.setReservation(this);
    }

    public void pendingToReserve() {
        if (this.status != ReservationStatus.PENDING) {
            throw new InvalidReservationException("결제 대기중인 예약이 아닙니다.");
        }
        this.status = ReservationStatus.RESERVED;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getName() {
        return member.getName();
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getReservationTime() {
        return reservationTime;
    }

    public LocalTime getStartAt() {
        return reservationTime.getStartAt();
    }

    public Theme getTheme() {
        return theme;
    }

    public String getThemeName() {
        return theme.getName();
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Payment getPayment() {
        return payment;
    }

    protected void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Reservation that)) {
            return false;
        }
        return this.getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
