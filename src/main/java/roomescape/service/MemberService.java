package roomescape.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.dto.response.member.MemberResponse;
import roomescape.dto.request.member.MemberSignUpRequest;
import roomescape.exception.RoomescapeException;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public MemberResponse save(MemberSignUpRequest memberSignUpRequest) {
        if (memberRepository.existsByEmail(new Email(memberSignUpRequest.email()))) {
            throw new RoomescapeException(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다.");
        }
        Member member = memberRepository.save(memberSignUpRequest.toEntity());
        return MemberResponse.from(member);
    }
}
