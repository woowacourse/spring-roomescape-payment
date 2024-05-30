package roomescape.infrastructure.member;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;

import java.util.Optional;

public interface MemberJpaRepository extends MemberRepository, JpaRepository<Member, Long> {

    Optional<Member> findByEmail(Email email);

    boolean existsByEmail(Email email);
}
