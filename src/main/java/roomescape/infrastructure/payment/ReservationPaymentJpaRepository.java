package roomescape.infrastructure.payment;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

import jakarta.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.repository.ListCrudRepository;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;

public interface ReservationPaymentJpaRepository extends
        ReservationPaymentRepository,
        ListCrudRepository<ReservationPayment, String> {

    @Override
    default ReservationPayment getById(String id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("존재하지 않는 결제 정보입니다."));
    }

    Optional<ReservationPayment> findByReservationId(long id);

    @Override
    default ReservationPayment getByReservationId(long id){
        return findByReservationId(id).orElse(ReservationPayment.getEmptyInstance());
    }
}
