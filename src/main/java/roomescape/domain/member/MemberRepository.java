package roomescape.domain.member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findByEmail(Email email);

    boolean existsByEmail(Email email);

    Optional<Member> findById(long id);

    List<Member> findAll();

    Member save(Member member);
}
