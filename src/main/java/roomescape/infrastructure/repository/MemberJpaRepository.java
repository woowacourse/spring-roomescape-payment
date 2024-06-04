package roomescape.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Member save(Member user);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmailAndPassword(String email, String password);

    List<Member> findAll();

    boolean existsByEmail(String email);

    void delete(Member member);
}
