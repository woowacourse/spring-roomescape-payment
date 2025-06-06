package roomescape.mvc.waiting.domain;

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
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.payment.domain.Payment;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.audit.AuditedEntity;

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
    private Payment payment;

    protected Waiting() {

    }

    public Waiting(
            Long id, LocalDate date, Theme theme, ReservationTime time,
            Member member, Payment payment) {
        this.id = id;
        this.date = date;
        this.theme = theme;
        this.time = time;
        this.member = member;
        this.payment = payment;
    }

    public static Waiting createWithoutIdWithoutPayment(LocalDate date, Theme theme,
            ReservationTime time, Member member) {
        return new Waiting(null, date, theme, time, member, null);
    }

    public static Waiting createWithoutId(LocalDate date, Theme theme, ReservationTime time,
            Member member, Payment payment) {
        return new Waiting(null, date, theme, time, member, payment);
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

    public Payment getPayment() {
        return payment;
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
