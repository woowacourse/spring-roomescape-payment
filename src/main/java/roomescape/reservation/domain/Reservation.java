package roomescape.reservation.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
import jakarta.persistence.Table;

import org.hibernate.proxy.HibernateProxy;

import roomescape.member.domain.Member;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "date", nullable = false)
    private LocalDate date;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Enumerated(value = EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", referencedColumnName = "id", nullable = false)
    private Theme theme;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_time_id", referencedColumnName = "id", nullable = false)
    private ReservationTime reservationTime;

    protected Reservation() {
    }

    public Reservation(Long id, LocalDate date, Status status, Member member, Theme theme,
                       ReservationTime reservationTime, LocalDateTime createdAt) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.member = member;
        this.theme = theme;
        this.reservationTime = reservationTime;
        this.createdAt = createdAt;
    }

    public Reservation(Member member, LocalDate date, Theme theme, ReservationTime reservationTime, Status status) {
        this(null, date, status, member, theme, reservationTime, LocalDateTime.now());
        validateLastDate(date);
    }

    public Reservation(Long id, LocalDate date, Status status, Member member, Theme theme,
                       ReservationTime reservationTime) {
        this(id, date, status, member, theme, reservationTime, LocalDateTime.now());
    }

    private void validateLastDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("지난 날짜는 예약할 수 없습니다.");
        }
    }

    public void changeSuccess() {
        status = Status.SUCCESS;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Theme getTheme() {
        return theme;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return reservationTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        Reservation that = (Reservation) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
                .getPersistentClass()
                .hashCode() : getClass().hashCode();
    }
}
