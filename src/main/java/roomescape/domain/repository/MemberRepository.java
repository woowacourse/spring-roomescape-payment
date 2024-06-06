package roomescape.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.Member;
import roomescape.domain.MemberEmail;
import roomescape.domain.MemberPassword;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(MemberEmail email);

    Optional<Member> findByEmail(MemberEmail email);

    boolean existsByEmailAndPassword(MemberEmail email, MemberPassword password);
}