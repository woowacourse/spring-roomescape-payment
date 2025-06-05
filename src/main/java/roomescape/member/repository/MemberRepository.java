package roomescape.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.global.exception.NotFoundException;
import roomescape.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    default Member getById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundException("존재하지 않는 멤버입니다. id: " + id));
    }

    Optional<Member> findByEmailAndPassword(final String email, final String password);

    boolean existsByEmail(final String email);

    Optional<Member> findByEmail(final String email);
}
