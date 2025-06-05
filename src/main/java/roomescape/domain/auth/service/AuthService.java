package roomescape.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.auth.exception.AccessDeniedException;
import roomescape.domain.auth.provider.JwtTokenProvider;
import roomescape.domain.member.Role;
import roomescape.domain.member.entity.Member;
import roomescape.domain.member.exception.MemberNotFoundException;
import roomescape.domain.member.repository.MemberRepositoryInterface;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final MemberRepositoryInterface memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String createToken(final String email, final String password) {
        if (!checkInvalidLogin(email, password)) {
            throw new MemberNotFoundException("멤버가 존재하지 않아 토큰을 만들 수 없습니다.");
        }

        return jwtTokenProvider.createToken(email);
    }

    @Transactional(readOnly = true)
    public String findNameByToken(final String token) {
        final String payload = jwtTokenProvider.getPayload(token);

        return memberRepository.findNameByEmail(payload)
                .orElseThrow(() -> new MemberNotFoundException("해당 이메일을 가진 멤버를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public void validateAdminByToken(final String token) {
        final String email = jwtTokenProvider.getPayload(token);
        final Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("해당 이메일을 가진 멤버를 찾을 수 없습니다."));

        if (member.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("관리자만 접근 가능합니다.");
        }
    }

    private boolean checkInvalidLogin(final String email, final String password) {
        return memberRepository.existsByEmailAndPassword(email, password);
    }
}
