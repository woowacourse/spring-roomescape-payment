package roomescape.domain.member.repository;

import java.util.List;
import java.util.Optional;
import roomescape.domain.member.entity.Member;

public interface MemberRepositoryInterface {

    Member save(final Member member);

    void deleteById(final Long id);

    boolean existsByEmailAndPassword(final String email, final String password);

    Optional<Member> findByEmail(final String email);

    Optional<Member> findById(final Long id);

    List<Member> findAll();

    Optional<String> findNameByEmail(final String email);
}
