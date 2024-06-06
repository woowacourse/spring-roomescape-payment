package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PaymentProduct {
    @Column(nullable = false, name = "reservation_id")
    private Long productId;

    protected PaymentProduct() {
    }

    public PaymentProduct(Long productId) {
        this.productId = productId;
    }
}
