package roomescape.member.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.Repository;
import roomescape.member.domain.Member;

@Tag(name = "멤버 레포지토리", description = "멤버 DB에서 가져온 데이터를 조작해 반환한다.")
public interface MemberRepository extends Repository<Member, Long> {

    Member save(Member member);

    Optional<Member> findMemberByEmailAndPassword(String email, String password);

    Optional<Member> findMemberById(long id);

    List<Member> findAll();
}
