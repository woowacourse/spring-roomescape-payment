package roomescape.domain.waiting;

public class Waiting {


    private Long id;


    private WaitingOrder waitingOrder;

    private Long reservationId;

    protected Waiting() {
    }

    public Waiting(int waitingOrder, Long reservationId) {
        this.waitingOrder = new WaitingOrder(waitingOrder);
        this.reservationId = reservationId;
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

    public Long getId() {
        return id;
    }

    public int getWaitingOrderValue() {
        return waitingOrder.getValue();
    }

    public Long getReservationId() {
        return reservationId;
    }
}
