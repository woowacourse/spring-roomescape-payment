package roomescape.domain.member;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.member.dto.MemberRequest;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.exception.custom.reason.member.MemberEmailConflictException;

@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberPasswordEncoder passwordEncoder;

    public void createMember(final MemberRequest request) {
        validateDuplicationEmail(request);

        final String encodedPassword = passwordEncoder.encode(request.password());

        final Member notSavedMember = new Member(request.email(), encodedPassword, request.name(), MemberRole.MEMBER);
        memberRepository.save(notSavedMember);
    }

    public List<MemberResponse> readAllMember() {
        return memberRepository.findAll().stream()
                .map(MemberResponse::from)
                .toList();
    }

    private void validateDuplicationEmail(final MemberRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new MemberEmailConflictException();
        }
    }
}
