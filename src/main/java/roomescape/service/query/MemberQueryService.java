package roomescape.service.query;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.dto.response.MemberProfileResponse;
import roomescape.exception.LoginFailException;
import roomescape.exception.NotFoundException;
import roomescape.repository.MemberRepository;

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

    public List<MemberProfileResponse> findAllMemberProfile() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberProfileResponse::new)
                .toList();
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findOneByEmail(email)
                .orElseThrow(() -> new LoginFailException("이메일에 해당하는 회원이 존재하지 않습니다."));
    }

    public boolean existsMemberInEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}
