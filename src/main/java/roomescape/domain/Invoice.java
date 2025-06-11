package roomescape.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Reservation reservation;

    @OneToOne
    private Payment payment;

    private LocalDateTime createdAt;

    private Invoice(Reservation reservation, Payment payment) {
        this.reservation = reservation;
        this.payment = payment;
        this.createdAt = LocalDateTime.now();
    }

    public static Invoice create(Reservation reservation, Payment payment) {
        return new Invoice(reservation, payment);
    }
}
