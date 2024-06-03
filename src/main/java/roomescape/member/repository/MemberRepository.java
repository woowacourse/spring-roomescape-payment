package roomescape.member.repository;

import org.springframework.data.repository.Repository;
import roomescape.member.domain.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findMemberByEmailAndPassword(String email, String password);

    Optional<Member> findMemberById(long id);

    List<Member> findAll();
}
