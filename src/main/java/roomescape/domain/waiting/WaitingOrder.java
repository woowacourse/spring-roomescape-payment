package roomescape.domain.waiting;

public class WaitingOrder {

    private static final int MIN_ORDER = 1;

    private int value;

    protected WaitingOrder() {
    }

    public WaitingOrder(int value) {
        validateWaitingOrder(value);
        this.value = value;
    }

    public void decreaseWaitingOrderByOne() {
        if (value > MIN_ORDER) {
            value--;
        }
    }

    public boolean isFirstOrder() {
        return value == MIN_ORDER;
    }

    public boolean isWaitingOrderGreaterThan(int waitingOrderToCompare) {
        return value > waitingOrderToCompare;
    }

    private void validateWaitingOrder(int order) {
        if (order < MIN_ORDER) {
            throw new IllegalArgumentException(
                    "[ERROR] 잘못된 대기 순서입니다. 관리자에게 문의해주세요.",
                    new Throwable("order : " + order));
        }
    }

    public int getValue() {
        return value;
    }
}
