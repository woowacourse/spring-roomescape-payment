package roomescape.domain.member.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.domain.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.member.response.FindAllMemberResponse;
import roomescape.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public MemberQueryService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member getMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("ID에 해당하는 회원을 찾을 수 없습니다."));
    }

    public List<FindAllMemberResponse> findAllMemberProfile() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(FindAllMemberResponse::new)
                .toList();
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findOneByEmail(email)
                .orElseThrow(() -> new NotFoundException("이메일에 해당하는 회원을 찾을 수 없습니다."));
    }

    public boolean existsMemberInEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}
