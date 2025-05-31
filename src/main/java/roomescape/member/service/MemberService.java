package roomescape.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.MemberResponses;
import roomescape.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void save(final MemberRequest memberRequest) {
        if (memberRepository.existsByEmail(memberRequest.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다. email=" + memberRequest.email());
        }
        memberRepository.save(
                Member.withDefaultRole(memberRequest.name(), memberRequest.email(), memberRequest.password()));
    }

    public MemberResponses findAllMember() {
        List<MemberResponse> members = memberRepository.findAll()
                .stream()
                .map(MemberResponse::new)
                .toList();

        return new MemberResponses(members);
    }
}
