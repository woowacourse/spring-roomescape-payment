package roomescape.service.member;

import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.ALREADY_EXIST_EMAIL;
import static roomescape.global.exception.roomescape.RoomEscapeErrorStatus.NON_EXIST_MEMBER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.MemberRole;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.dto.response.MemberResponse;
import roomescape.global.PasswordEncoder;
import roomescape.global.exception.roomescape.RoomEscapeException;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberRegisterResponse addMember(final MemberRegisterRequest request) {
        validateDuplicateEmail(request.email());
        final Member newMember = Member.builder()
                .email(request.email())
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .role(MemberRole.USER)
                .build();
        return MemberRegisterResponse.from(memberRepository.save(newMember));
    }

    public List<MemberResponse> getAllMembers() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    public Member getMemberById(final long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RoomEscapeException(NON_EXIST_MEMBER));
    }

    public Member getMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RoomEscapeException(NON_EXIST_MEMBER));
    }

    private void validateDuplicateEmail(final String email) {
        if (memberRepository.existByEmail(email)) {
            throw new RoomEscapeException(ALREADY_EXIST_EMAIL);
        }
    }
}
