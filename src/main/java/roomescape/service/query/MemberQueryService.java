package roomescape.service.query;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.dto.member.MemberResponseDto;
import roomescape.exception.common.NotFoundException;
import roomescape.repository.JpaMemberRepository;

@Service
@Transactional(readOnly = true)
public class MemberQueryService {

    private final JpaMemberRepository memberRepository;

    public MemberQueryService(JpaMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member findMemberById(long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("유저", id));
    }

    public List<MemberResponseDto> findAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponseDto::new)
                .toList();
    }
}
