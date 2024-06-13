package roomescape.domain.reservation;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.util.Objects;
import roomescape.domain.member.Member;

@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Embedded
    private ReservationSlot reservationSlot;

    @OneToOne(mappedBy = "reservation")
    private Payment payment;

    public Reservation(Member member, ReservationSlot reservationSlot) {
        this.member = member;
        this.reservationSlot = reservationSlot;
    }

    protected Reservation() {
    }

    public void acceptWaiting(Waiting waiting){
        this.member = waiting.getMember();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getDate() {
        return reservationSlot.getDate();
    }

    public ReservationTime getTime() {
        return reservationSlot.getTime();
    }

    public Theme getTheme() {
        return reservationSlot.getTheme();
    }

    public ReservationSlot getReservationSlot() {
        return reservationSlot;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Reservation)) {
            return false;
        }
        Reservation that = (Reservation) o;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
