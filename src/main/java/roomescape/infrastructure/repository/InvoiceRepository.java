package roomescape.infrastructure.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.Invoice;
import roomescape.domain.Member;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findAllByReservation_Member(Member member);
}
