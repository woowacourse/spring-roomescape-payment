package roomescape.repository;

import org.springframework.data.repository.CrudRepository;
import roomescape.model.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member, Long> {

    Optional<Member> findByEmailAndPassword(final String email, final String password);

    Optional<Member> findById(final Long id);

    List<Member> findAll();
}
