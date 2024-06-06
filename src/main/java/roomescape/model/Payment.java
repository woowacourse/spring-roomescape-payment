package roomescape.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.ColumnDefault;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class Payment {
    @ColumnDefault("''")
    private String paymentKey;

    @ColumnDefault("0")
    private Long totalAmount;

    public Payment() {
    }

    public Payment(final Long totalAmount, final String paymentKey) {
        this.totalAmount = totalAmount;
        this.paymentKey = paymentKey;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentKey() {
        return paymentKey;
    }
}