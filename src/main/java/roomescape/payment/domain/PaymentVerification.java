package roomescape.payment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import roomescape.member.domain.Member;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private int amount;

    @ManyToOne
    private Member member;

    public PaymentVerification(final String orderId, final int amount, final Member member) {
        this.orderId = orderId;
        this.amount = amount;
        this.member = member;
    }

    public boolean isSamePayment(
            final String orderId,
            final int amount,
            final Long memberId
    ) {
        return Objects.equals(this.orderId, orderId)
               && this.amount == amount
               && Objects.equals(this.member.getId(), memberId);
    }
}
