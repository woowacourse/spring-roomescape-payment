package roomescape.application.member.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.member.query.dto.MemberResult;
import roomescape.domain.member.repository.MemberRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberQueryService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public List<MemberResult> findAll() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResult::from)
                .toList();
    }
}
