package roomescape.infra.repository;

import org.springframework.data.repository.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;

public interface MemberJpaRepository extends MemberRepository, Repository<Member, Long> {
}
