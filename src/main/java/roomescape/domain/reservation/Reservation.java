package roomescape.domain.reservation;

import jakarta.persistence.*;
import roomescape.domain.member.Member;
import roomescape.domain.theme.Theme;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

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

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(final Member member, final LocalDate date, final ReservationTime time,
                       final Theme theme, final ReservationStatus status) {
        this(null, member, date, time, theme, status);
    }

    public Reservation(final Long id, final Member member, final LocalDate date,
                       final ReservationTime time, final Theme theme, final ReservationStatus status) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.status = status;
    }

    public void toReserved() {
        this.status = ReservationStatus.RESERVED;
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

    public String getThemeName() {
        return theme.getName();
    }

    public ReservationStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return object instanceof Reservation other
                && Objects.equals(getId(), other.getId())
                && Objects.equals(getMember(), other.getMember())
                && Objects.equals(getDate(), other.getDate())
                && Objects.equals(getTime(), other.getTime())
                && Objects.equals(getTheme(), other.getTheme())
                && Objects.equals(getStatus(), other.getStatus());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
