package roomescape.member.service;

import org.springframework.stereotype.Service;
import roomescape.common.exception.NotFoundException;
import roomescape.member.domain.Email;
import roomescape.member.domain.Member;
import roomescape.member.domain.Password;
import roomescape.member.repository.MemberRepository;
import roomescape.member.service.dto.request.MemberCreateRequest;
import roomescape.member.service.dto.response.MemberResponse;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member findMemberByEmailAndPassword(final String email, final String password) {
        return memberRepository.findByEmailAndPassword(new Email(email), new Password(password))
                .orElseThrow(() -> new NotFoundException("이메일 또는 패스워드가 잘못 되었습니다."));
    }

    public List<MemberResponse> findAll() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::fromEntity)
                .toList();
    }

    public void create(MemberCreateRequest request) {
        memberRepository.save(request.toEntity());
    }
}
