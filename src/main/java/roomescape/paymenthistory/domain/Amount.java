package roomescape.paymenthistory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Amount {
    public static final int ROOM_ESCAPTE_AMOUNT = 128000;
    @Column(name = "amount", nullable = false)
    private long amount;

    protected Amount() {
    }

    public Amount(long amount) {
        validation(amount);
        this.amount = amount;
    }

    public void validation(long amount) {
        if (amount != ROOM_ESCAPTE_AMOUNT) {
            throw new IllegalArgumentException("방탈출 가격이 일치하지 않습니다.");
        }
    }

    public long getAmount() {
        return amount;
    }
}
