package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PaymentProduct {
    @Column(nullable = false, name = "product_id")
    private Long productId;

    protected PaymentProduct() {
    }

    public PaymentProduct(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
