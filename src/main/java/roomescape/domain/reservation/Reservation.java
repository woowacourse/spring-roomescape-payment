package roomescape.domain.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import roomescape.domain.member.Member;
import roomescape.domain.theme.Theme;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ReservationTime time;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Theme theme;

    @Column
    private String paymentKey;

    @Column
    private Integer amount;

    protected Reservation() {
    }

    public Reservation(final Member member, final LocalDate date, final ReservationTime time, final Theme theme,
                       final String paymentKey, final Integer amount) {
        this.id = null;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }

    public boolean hasSameDateTime(final LocalDate date, final ReservationTime time) {
        return this.time.equals(time) && this.date.equals(date);
    }

    public boolean isAvailable() {
        LocalDate now = LocalDate.now();
        return now.isBefore(date) || (now.equals(date) && time.isAvailable());
    }

    public boolean isNotReservedBy(Long id) {
        return !this.member.getId().equals(id);
    }

    public void updatePaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public Long getReservationTimeId() {
        return time.getId();
    }

    public Long getThemeId() {
        return theme.getId();
    }

    public String getThemeName() {
        return theme.getName();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getMemberName() {
        return member.getNameString();
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

    public String getPaymentKey() {
        return paymentKey;
    }

    public Integer getAmount() {
        return amount;
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
