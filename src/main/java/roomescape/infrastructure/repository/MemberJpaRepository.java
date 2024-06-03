package roomescape.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import roomescape.domain.member.Member;

public interface MemberJpaRepository extends Repository<Member, Long> {

    Member save(Member user);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmailAndPassword(String email, String password);

    List<Member> findAll();

    boolean existsByEmail(String email);

    void delete(Member member);
}
