package roomescape.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.exception.member.NotFoundMemberException;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getMemberById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundMemberException::new);
    }

    Optional<Member> findByEmail(MemberEmail email);

    boolean existsByEmail(MemberEmail email);
}
