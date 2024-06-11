package roomescape.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Embeddable;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class Payment {
    @ColumnDefault("''")
    private String paymentKey;

    @ColumnDefault("0")
    private BigDecimal amount;

    public Payment() {
    }

    public Payment(String paymentKey, Long amount) {
        this.paymentKey = paymentKey;
        this.amount = BigDecimal.valueOf(amount);
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}