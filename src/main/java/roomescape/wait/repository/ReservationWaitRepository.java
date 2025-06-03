package roomescape.wait.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.wait.domain.ReservationWait;

public interface ReservationWaitRepository extends JpaRepository<ReservationWait, Long> {

    List<ReservationWait> findAllByMember_id(Long memberId);
}
