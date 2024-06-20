package roomescape.domain.reservation;

import java.time.LocalDate;
import java.util.Objects;

import roomescape.domain.member.Member;
import roomescape.domain.payment.Payment;
import roomescape.domain.theme.Theme;

public class ReservationBuilder {

    private Long id;
    private Member member;
    private LocalDate date;
    private ReservationTime time;
    private Theme theme;
    private ReservationStatus status;
    private Payment payment;

    ReservationBuilder() {
    }

    public ReservationBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public ReservationBuilder member(Member member) {
        this.member = member;
        return this;
    }

    public ReservationBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public ReservationBuilder time(ReservationTime time) {
        this.time = time;
        return this;
    }

    public ReservationBuilder theme(Theme theme) {
        this.theme = theme;
        return this;
    }

    public ReservationBuilder status(ReservationStatus status) {
        this.status = status;
        return this;
    }

    public ReservationBuilder payment(Payment payment) {
        this.payment = payment;
        return this;
    }

    public Reservation build() {
        Objects.requireNonNull(member, "member is required");
        Objects.requireNonNull(date, "date is required");
        Objects.requireNonNull(time, "time is required");
        Objects.requireNonNull(theme, "theme is required");
        Objects.requireNonNull(status, "status is required");
        return new Reservation(id, member, date, time, theme, status, payment);
    }
}
