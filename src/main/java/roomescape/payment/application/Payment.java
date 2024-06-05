package roomescape.payment.application;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private Long totalAmount;

    @Column(name = "payment_method")
    private String method; // TODO Enum

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;
}
