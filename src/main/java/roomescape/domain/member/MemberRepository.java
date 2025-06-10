package roomescape.domain.member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    boolean existsByEmail(Email email);

    Member save(Member member);

    List<Member> findAll();

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(Email email);
}
