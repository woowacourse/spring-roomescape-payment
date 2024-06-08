package roomescape.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private long totalAmount;

    @Enumerated(EnumType.STRING)
    private State state;

    //얘가 안에 있는 것이 자연스러운가? 밖에서도 객체를 만들기 위해서 사용하기도 하는데?
    public enum State {
        READY, DONE
    }

    public Payment(String paymentKey, String orderId, long totalAmount, State state) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.state = state;
    }

    protected Payment() {
    }
}
