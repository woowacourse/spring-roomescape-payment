package roomescape.domain.member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    List<Member> findAll();

    boolean existsByEmail(Email email);

    Member getById(long id);

    Optional<Member> findByEmail(Email email);
}
