package roomescape.payment.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class PrePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    String orderId;

    @Column(nullable = false)
    BigDecimal amount;

    public PrePayment(String orderId, BigDecimal amount) {
        this.orderId = orderId;
        this.amount = amount;
    }

    //TODO: 네이밍 고민  (2025-05-28, 수, 11:55)
    public boolean isSameAmount(BigDecimal amount) {
        System.out.println("임시: " + this.amount);
        System.out.println("진짜:" + amount);
        return this.amount.compareTo(amount) == 0;
    }
}
