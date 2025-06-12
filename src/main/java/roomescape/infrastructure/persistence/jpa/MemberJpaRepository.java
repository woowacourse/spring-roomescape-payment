package roomescape.infrastructure.persistence.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;

public interface MemberJpaRepository extends CrudRepository<Member, Long> {

    boolean existsByEmail(Email email);

    List<Member> findAll();

    Optional<Member> findByEmail(Email email);
}
