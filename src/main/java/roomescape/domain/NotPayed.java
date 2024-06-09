package roomescape.domain;

import jakarta.persistence.Entity;

@Entity
public class NotPayed extends Payment {

    public NotPayed() {
        super(State.READY);
    }

    @Override
    public String getPaymentKey() {
        return null;
    }

    @Override
    public String getOrderId() {
        return null;
    }

    @Override
    public Long getAmount() {
        return null;
    }
}
