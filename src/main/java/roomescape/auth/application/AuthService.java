package roomescape.auth.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.auth.domain.AuthTokenProvider;
import roomescape.auth.ui.dto.LoginRequest;
import roomescape.exception.auth.AuthenticationException;
import roomescape.exception.resource.ResourceNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthTokenProvider authTokenProvider;
    private final MemberRepository memberRepository;

    public String createAccessToken(final LoginRequest request) {
        final String email = request.email();
        final Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("로그인 실패: 존재하지 않는 이메일 '{}'", email);
                    return new ResourceNotFoundException("해당 이메일을 가진 회원이 존재하지 않습니다.");
                });

        if (member.isWrongPassword(request.password())) {
            log.warn("로그인 실패: 이메일 '{}' 에 대해 비밀번호 불일치", email);
            throw new AuthenticationException("비밀번호가 올바르지 않습니다.");
        }

        final String token = authTokenProvider.createAccessToken(member.getId().toString(), member.getRole());
        log.info("로그인 성공: 이메일 '{}' -> id={}, role={}, token 생성 완료", email, member.getId(), member.getRole());

        return token;
    }

    public String getMemberNameById(final Long memberId) {
        final String name = memberRepository.getById(memberId).getName();
        log.debug("회원 조회: id={} -> name={}", memberId, name);
        return name;
    }
}
