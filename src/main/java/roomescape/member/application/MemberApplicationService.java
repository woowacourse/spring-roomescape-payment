package roomescape.member.application;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.security.application.MyPasswordEncoder;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.exception.MemberDuplicatedException;
import roomescape.member.presentation.dto.request.SignupWebRequest;
import roomescape.member.presentation.dto.response.MemberWebResponse;
import roomescape.member.presentation.dto.response.SignUpWebResponse;

@Service
@Transactional
@Slf4j
public class MemberApplicationService {

    private final MemberDataService memberDataService;
    private final MyPasswordEncoder myPasswordEncoder;

    public MemberApplicationService(final MemberDataService memberDataService,
                                    final MyPasswordEncoder myPasswordEncoder) {
        this.memberDataService = memberDataService;
        this.myPasswordEncoder = myPasswordEncoder;
    }

    public SignUpWebResponse signup(final SignupWebRequest signupWebRequest) {
        log.info("회원가입 시도: email={}, name={}", signupWebRequest.email(), signupWebRequest.name());

        String encodedPassword = myPasswordEncoder.encode(signupWebRequest.password());
        Member member = new Member(signupWebRequest.name(), signupWebRequest.email(), encodedPassword,
                MemberRole.REGULAR);
        validateMemberExists(signupWebRequest);
        Member savedMember = memberDataService.create(member);

        log.info("회원가입 완료: memberId={}, email={}", savedMember.getId(), savedMember.getEmail());
        return SignUpWebResponse.from(savedMember);
    }

    public List<MemberWebResponse> findAllRegular() {
        return memberDataService.findByMemberRole(MemberRole.REGULAR).stream()
                .map(member -> new MemberWebResponse(member.getId(), member.getName()))
                .toList();
    }

    public Member getById(final Long id) {
        return memberDataService.getById(id);
    }

    private void validateMemberExists(final SignupWebRequest signupWebRequest) {
        if (memberDataService.existsByEmail(signupWebRequest.email())) {
            log.warn("회원가입 실패 - 이미 존재하는 이메일: email={}", signupWebRequest.email());
            throw new MemberDuplicatedException("이미 존재하는 회원입니다.");
        }
    }
}
