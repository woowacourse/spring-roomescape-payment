package roomescape.domain.reservation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import roomescape.domain.member.Member;
import roomescape.domain.theme.Theme;

@Table(name = "reservation")
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @Column(name = "reserved_date")
    private LocalDate date;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "time_id")
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status")
    private ReservationStatus status;

    public Reservation(Member Member, LocalDate date, LocalDateTime createdAt, ReservationTime time, Theme theme,
                       ReservationStatus status) {
        this(null, Member, date, createdAt, time, theme, null, status);
    }

    public Reservation(Member Member, LocalDate date, LocalDateTime createdAt, ReservationTime time,
                       Theme theme, Payment payment, ReservationStatus status) {
        this(null, Member, date, createdAt, time, theme, payment, status);
    }

    public Reservation(Long id, Member Member, LocalDate date, LocalDateTime createdAt,
                       ReservationTime time, Theme theme, Payment payment, ReservationStatus status) {

        this.id = id;
        this.member = Member;
        this.date = date;
        this.createdAt = createdAt;
        this.time = time;
        this.theme = theme;
        this.payment = payment;
        this.status = status;
    }

    protected Reservation() {
    }

    public boolean isNotReservedBy(Member member) {
        return !this.member.getId().equals(member.getId());
    }

    public void reserve() {
        this.status = ReservationStatus.RESERVED;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return member.getName();
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public Member getMember() {
        return member;
    }

    public Optional<Payment> getPayment() {
        if(payment == null) {
            return Optional.empty();
        }
        return Optional.of(payment);
    }

    public ReservationStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        if (id == null || that.id == null) {
            return Objects.equals(member, that.member) && Objects.equals(date, that.date) && Objects.equals(time, that.time) && Objects.equals(theme, that.theme);
        }
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return Objects.hash(member, date, time, theme);
        }
        return Objects.hash(id);
    }
}
