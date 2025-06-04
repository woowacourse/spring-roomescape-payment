package roomescape.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.MemberRole;
import roomescape.dto.request.MemberRegisterRequest;
import roomescape.dto.response.MemberRegisterResponse;
import roomescape.dto.response.MemberResponse;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberRegisterResponse register(final MemberRegisterRequest request) {
        validateDuplicateEmail(request.email());
        validateDuplicateName(request.name());
        final Member newMember = Member.builder()
                .email(request.email())
                .name(request.name())
                .password(request.password())
                .role(MemberRole.USER)
                .build();
        return MemberRegisterResponse.from(memberRepository.save(newMember));
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getAll() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    private void validateDuplicateEmail(final String email) {
        if (memberRepository.existByEmail(email)) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 이메일 입니다.");
        }
    }

    private void validateDuplicateName(final String name) {
        if (memberRepository.existByName(name)) {
            throw new IllegalArgumentException("[ERROR] 이미 존재하는 이름 입니다.");
        }
    }
}
