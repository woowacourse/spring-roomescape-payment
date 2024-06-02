package roomescape.service.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.service.member.dto.MemberListResponse;
import roomescape.service.member.dto.MemberResponse;

import java.util.List;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public MemberListResponse findAllMember() {
        List<Member> members = memberRepository.findAll();
        return new MemberListResponse(members.stream()
                .map(MemberResponse::new)
                .toList());
    }
}
