package roomescape.paymenthistory.repository;

import org.springframework.data.repository.ListCrudRepository;
import roomescape.paymenthistory.domain.PaymentHistory;

// TODO: Repository<T, R> 로 바꾸기
public interface PaymentHistoryRepository extends ListCrudRepository<PaymentHistory, Long> {
}
