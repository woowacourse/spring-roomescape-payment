package roomescape.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.Id;

public interface MemberRepository extends JpaRepository<Member, Id> {

    Optional<Member> findByEmail_Value(String email);

    boolean existsByEmail_Value(String email);
}
