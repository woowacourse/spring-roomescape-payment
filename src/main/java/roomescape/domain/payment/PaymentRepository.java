package roomescape.domain.payment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import roomescape.domain.BaseRepository;
import roomescape.exception.NotFoundException;

public interface PaymentRepository extends BaseRepository<Payment, Long> {

    @Override
    Payment save(Payment payment);

    @Override
    Optional<Payment> findById(Long id);

    @Override
    Payment getById(Long id) throws NotFoundException;

    @Override
    List<Payment> findAll(Specification<Payment> specification);

    @Override
    boolean exists(Specification<Payment> specification);

    @Override
    void delete(Payment payment);

    @Override
    void deleteByIdOrElseThrow(Long id) throws NotFoundException;
}
