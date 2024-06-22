package roomescape.domain.reservation;

import static roomescape.exception.RoomescapeErrorCode.INVALID_DATETIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

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

import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.theme.Theme;
import roomescape.exception.RoomescapeException;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Theme theme;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    private Payment payment;

    protected Reservation() {
    }

    public Reservation(
            final Long id,
            final Member member,
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final ReservationStatus status,
            final Payment payment
    ) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.status = status;
        this.payment = payment;
    }

    public static ReservationBuilder builder() {
        return new ReservationBuilder();
    }

    public void validateDateTime() {
        if (date.isBefore(LocalDate.now()) && time.isBefore(LocalTime.now())) {
            throw new RoomescapeException(INVALID_DATETIME);
        }
    }

    public void pay(final Payment payment) {
        this.payment = payment;
        reserve();
    }

    public void reserve() {
        status = ReservationStatus.RESERVED;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public LocalTime getStartAt() {
        return time.getStartAt();
    }

    public Theme getTheme() {
        return theme;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Payment getPayment() {
        return payment;
    }

    public Optional<String> getPaymentKey() {
        return Optional.ofNullable(payment.getPaymentKey());
    }

    public Optional<String> getOrderId() {
        return Optional.ofNullable(payment.getOrderId());
    }

    public Optional<Long> getPaymentAmount() {
        return Optional.ofNullable(payment.getAmount());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof Reservation other
                && Objects.equals(getId(), other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", member=" + member +
                ", date=" + date +
                ", time=" + time +
                ", theme=" + theme +
                ", status=" + status +
                ", payment=" + payment +
                '}';
    }
}
