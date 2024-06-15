package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.theme.Theme;
import roomescape.exception.RoomescapeException;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Member member;
    @Column(nullable = false)
    private LocalDate date;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ReservationTime time;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Theme theme;
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(Member member, LocalDate date, ReservationTime time, Theme theme, ReservationStatus status) {
        this(null, member, date, time, theme, status);
    }

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time, Theme theme, ReservationStatus status) {
        if (date == null) {
            throw new RoomescapeException(HttpStatus.BAD_REQUEST, "예약 날짜는 필수입니다.");
        }
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.status = status;
    }

    public void cancel() {
        if (status.isNotCanceled()) {
            status = ReservationStatus.CANCELED;
        }
    }

    public boolean isPaid() {
        return status.isReserved();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Reservation that = (Reservation) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
