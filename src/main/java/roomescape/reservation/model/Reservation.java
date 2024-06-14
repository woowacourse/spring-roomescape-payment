package roomescape.reservation.model;

import java.time.LocalDate;

import jakarta.persistence.*;

import roomescape.member.model.Member;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ReservationDate date;

    @ManyToOne
    private ReservationTime time;

    @ManyToOne
    private Theme theme;

    @ManyToOne
    private Member member;

    @OneToOne
    private Payment payment;

    public static Reservation of(
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        checkRequiredData(time, theme, member);

        final ReservationDate reservationDate = new ReservationDate(date);
        return new Reservation(
                null,
                reservationDate,
                time,
                theme,
                member
        );
    }

    private static void checkRequiredData(
            final ReservationTime reservationTime,
            final Theme theme,
            final Member member
    ) {
        if (reservationTime == null || theme == null || member == null) {
            throw new IllegalArgumentException("시간, 테마, 회원 정보는 Null을 입력할 수 없습니다.");
        }
    }

    public static Reservation of(
            final Long id,
            final LocalDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        checkRequiredData(time, theme, member);

        return new Reservation(
                id,
                new ReservationDate(date),
                time,
                theme,
                member
        );
    }

    protected Reservation() {
    }

    private Reservation(
            final Long id,
            final ReservationDate date,
            final ReservationTime time,
            final Theme theme,
            final Member member
    ) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.theme = theme;
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public ReservationDate getDate() {
        return date;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Theme getTheme() {
        return theme;
    }

    public Member getMember() {
        return member;
    }

    public Payment getPayment() {
        return payment;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public void updatePayment(Payment payment) {
        this.payment = payment;
    }
}
