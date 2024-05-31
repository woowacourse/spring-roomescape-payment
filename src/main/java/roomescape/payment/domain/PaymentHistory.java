package roomescape.payment.domain;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import roomescape.global.entity.BaseEntity;
import roomescape.global.entity.Price;
import roomescape.member.domain.Member;

@Entity
public class PaymentHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Embedded
    private Price price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    protected PaymentHistory() {
    }

    private PaymentHistory(Long id, String paymentKey, PaymentType paymentType, PaymentStatus paymentStatus,
                           Price price,
                           Member member) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.paymentType = paymentType;
        this.paymentStatus = paymentStatus;
        this.price = price;
        this.member = member;
    }

    public PaymentHistory(String paymentKey, PaymentType paymentType, PaymentStatus paymentStatus, Price price,
                          Member member) {
        this(null, paymentKey, paymentType, paymentStatus, price, member);
    }

    public void cancel() {
        this.paymentStatus = PaymentStatus.CANCELLED;
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public Price getPrice() {
        return price;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PaymentHistory that = (PaymentHistory) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
