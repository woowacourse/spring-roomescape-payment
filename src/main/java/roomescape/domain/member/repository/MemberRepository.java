package roomescape.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.member.model.Member;
import roomescape.domain.member.model.MemberEmail;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(MemberEmail email);

    boolean existsByEmail(MemberEmail email);
}
