package roomescape.reservation.domain;

import static roomescape.reservation.domain.Status.PAYMENT_PENDING;
import static roomescape.reservation.domain.Status.SUCCESS;
import static roomescape.reservation.domain.Status.WAIT;

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
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.member.domain.Member;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_id", nullable = false)
    private ReservationTime reservationTime;

    // TODO: nullable 로 DDL이 생성되는 지 확인하기
    private String paymentKey;

    private Long amount;

    protected Reservation() {
    }

    public Reservation(
            Long id, Member member, LocalDate date,
            Theme theme, ReservationTime reservationTime, Status status,
            String paymentKey, long amount
    ) {
        this.id = id;
        this.member = member;
        validateLastDate(date);
        this.date = date;
        this.theme = theme;
        this.reservationTime = reservationTime;
        this.status = status;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    private void validateLastDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("지난 날짜는 예약할 수 없습니다.");
        }
    }

    public Reservation(
            Member member, LocalDate date, Theme theme,
            ReservationTime reservationTime, Status status,
            String paymentKey, Long amount
    ) {
        this(null, member, date, theme, reservationTime, status, paymentKey, amount);
    }

    public void updatePaymentPending() {
        if (status == SUCCESS) {
            throw new IllegalArgumentException("이미 확정된 예약입니다.");
        }
        if (status == PAYMENT_PENDING) {
            throw new IllegalArgumentException("이미 결제 대기된 예약입니다.");
        }
        this.status = PAYMENT_PENDING;
    }

    public boolean isSuccessReservation() {
        return status == SUCCESS;
    }

    public boolean isWaitingReservation() {
        return status == WAIT;
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

    public String getThemeName() {
        return theme.getName();
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return reservationTime;
    }

    public LocalTime getStartAt() {
        return reservationTime.getStartAt();
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusDisplayName() {
        return status.getDisplayName();
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public long getAmount() {
        return amount;
    }
}
