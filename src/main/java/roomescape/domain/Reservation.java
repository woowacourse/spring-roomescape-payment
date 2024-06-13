package roomescape.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private TimeSlot time;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Theme theme;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Payment payment;

    @Enumerated(value = EnumType.STRING)
    private ReservationStatus status;

    protected Reservation() {
    }

    public Reservation(Long id, Member member, LocalDate date, TimeSlot time, Theme theme, Payment payment, ReservationStatus status) {
        this.id = id;
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.payment = payment;
        this.status = status;
    }

    public Reservation(Member member, LocalDate date, TimeSlot time, Theme theme, ReservationStatus status) {
        this.member = member;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.payment = null;
        this.status = status;
    }

    public static Reservation createNewBooking(Member member, LocalDate date, TimeSlot time, Theme theme, Payment payment) {
        return new Reservation(null, member, date, time, theme, payment, ReservationStatus.BOOKING);
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

    public TimeSlot getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public Payment getPayment() {
        return payment;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}
