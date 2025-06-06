package roomescape.mvc.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.mvc.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findOneByEmail(String email);

    boolean existsByEmail(String email);
}
