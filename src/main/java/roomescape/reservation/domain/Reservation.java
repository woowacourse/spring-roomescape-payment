package roomescape.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import roomescape.member.domain.Member;
import roomescape.reservation.persistence.ReservationStatusPersistConverter;

import java.time.LocalDate;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Column(nullable = false, name = "date")
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "time_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ReservationTime time;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "theme_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Theme theme;

    @Column(nullable = false, name = "status")
    @Convert(converter = ReservationStatusPersistConverter.class)
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme, ReservationStatus status) {
        this(null, member, date, time, theme, status);
    }

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time,
                       Theme theme, ReservationStatus status) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.status = status;
    }

    public boolean isBeforeOrOnToday(LocalDate today) {
        return date.isBefore(today) || date.equals(today);
    }

    public boolean isBooking() {
        return status.isBooking();
    }

    public boolean isWaiting() {
        return status.isWaiting();
    }

    public boolean isModifiableBy(Member member) {
        Long memberId = member.getId();
        Long ownerId = this.member.getId();
        return ownerId.equals(memberId) || member.isAdmin();
    }

    public void changeToBooking() {
        this.status = ReservationStatus.BOOKING;
    }

    public void changeToWaiting() {
        this.status = ReservationStatus.WAITING;
    }

    public String getMemberName() {
        return member.getName();
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

    public Theme getTheme() {
        return theme;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public String getStatusDescription() {
        return status.getDescription();
    }
}
