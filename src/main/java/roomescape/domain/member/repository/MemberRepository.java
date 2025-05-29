package roomescape.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
