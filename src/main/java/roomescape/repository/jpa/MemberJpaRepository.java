package roomescape.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;

import java.util.Optional;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByName(String name);
}
