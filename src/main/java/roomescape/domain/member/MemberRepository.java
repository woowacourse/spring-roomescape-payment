package roomescape.domain.member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    Member save(Member user);

    Optional<Member> findMember(Long id);

    Optional<Member> findMember(String email, String password);

    List<Member> getAll();

    boolean existsMember(String email);

    void deleteMember(Member member);
}
