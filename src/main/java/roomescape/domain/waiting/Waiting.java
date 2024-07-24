package roomescape.domain.waiting;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import roomescape.domain.reservation.Reservation;
import roomescape.exception.custom.RoomEscapeException;

@Entity
public class Waiting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Reservation reservation;

    @Embedded
    private WaitingOrder waitingOrder;

    protected Waiting() {
    }

    public Waiting(Reservation reservation, int waitingOrder) {
        validateAvailableWaiting(reservation);
        this.reservation = reservation;
        this.waitingOrder = new WaitingOrder(waitingOrder);
    }

    public void decreaseWaitingOrderByOne() {
        waitingOrder.decreaseWaitingOrderByOne();
    }

    public boolean isFirstOrder() {
        return waitingOrder.isFirstOrder();
    }

    public boolean isWaitingOrderGreaterThan(int waitingOrderToCompare) {
        return waitingOrder.isWaitingOrderGreaterThan(waitingOrderToCompare);
    }

    private void validateAvailableWaiting(Reservation reservation) {
        if (reservation.isReserved()) {
            throw new RoomEscapeException(
                    "확정된 예약은 대기가 불가능합니다.",
                    "reservation_id : " + reservation.getId()
            );
        }
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public int getWaitingOrderValue() {
        return waitingOrder.getWaitingOrder();
    }
}
