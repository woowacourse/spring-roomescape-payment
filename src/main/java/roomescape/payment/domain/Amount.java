package roomescape.payment.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Amount {
    @Column(name = "amount", nullable = false, unique = true)
    private BigDecimal value;

    public Amount(BigDecimal value) {
        this.value = value;
    }
}
