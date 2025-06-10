package roomescape.domain.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(of = {"paymentKey"})
@Getter
@Accessors(fluent = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity(name = "PAYMENT")
public class Payment {

    @Id
    private String paymentKey;
    private int amount;

    public Payment(final String paymentKey, final int amount) {
        this.paymentKey = paymentKey;
        this.amount = amount;
    }
}
