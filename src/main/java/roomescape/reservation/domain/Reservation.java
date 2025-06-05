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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.theme.domain.ReservationTheme;
import roomescape.time.domain.ReservationTime;

@Entity
@Table(name = "reservation")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "date")
    @Temporal(TemporalType.DATE)
    @NotNull
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "time_id")
    @NotNull
    private ReservationTime time;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    @NotNull
    private ReservationTheme theme;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    @Setter
    private Payment payment;

    public Reservation(Long id, Member member, LocalDate date, ReservationTime time, ReservationTheme theme) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    @Builder
    public Reservation(Member member, LocalDate date, ReservationTime time, ReservationTheme theme) {
        validateLocalDate(date, time);
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
    }

    private void validateLocalDate(final LocalDate date, final ReservationTime time) {
        final LocalDateTime now = LocalDateTime.now();
        final LocalDateTime dateTime = LocalDateTime.of(date, time.getStartAt());
        if (dateTime.isBefore(now) || dateTime.isEqual(now)) {
            throw new IllegalArgumentException("[ERROR] 예약시간은 과거일 수 없습니다.");
        }
    }
}
