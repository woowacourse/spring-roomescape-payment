package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Waiting extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Theme theme;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ReservationTime time;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;
    @OneToOne(fetch = FetchType.LAZY)
    private Payment paymentHistory;

    protected Waiting() {

    }

    public Waiting(
            Long id, LocalDate date, Theme theme, ReservationTime time,
            Member member, Payment paymentHistory) {
        this.id = id;
        this.date = date;
        this.theme = theme;
        this.time = time;
        this.member = member;
        this.paymentHistory = paymentHistory;
    }

    public static Waiting createWithoutIdWithoutPayment(LocalDate date, Theme theme,
            ReservationTime time, Member member) {
        return new Waiting(null, date, theme, time, member, null);
    }

    public static Waiting createWithoutId(LocalDate date, Theme theme, ReservationTime time,
            Member member, Payment paymentHistory) {
        return new Waiting(null, date, theme, time, member, paymentHistory);
    }

    public boolean isPastWaiting() {
        LocalDateTime dateTime = LocalDateTime.of(date, time.getStartAt());
        return dateTime.isBefore(LocalDateTime.now());
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Theme getTheme() {
        return theme;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Waiting waiting = (Waiting) o;
        if (getId() == null || waiting.getId() == null) {
            return false;
        }
        return Objects.equals(id, waiting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
