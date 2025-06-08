package roomescape.member.service;

import jakarta.transaction.Transactional;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.exception.ErrorCode;
import roomescape.exception.NotFoundException;
import roomescape.exception.UnauthorizedException;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginRequest;
import roomescape.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    public Member getMemberByEmailAndPassword(final LoginRequest loginRequest) {
        final Member member = memberRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.MEMBER_NOT_FOUND));
        if (!matches(loginRequest.password(), member.getPassword())) {
            throw new UnauthorizedException(ErrorCode.PASSWORD_MISMATCH);
        }
        return member;
    }

    @Transactional
    public void updateSessionId(final Member member, final String sessionId) {
        final Member foundMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        foundMember.updateSessionId(sessionId);
        memberRepository.save(foundMember);
    }

    private String encode(final String rawPassword) {
        return Base64.getEncoder().encodeToString(rawPassword.getBytes());
    }

    private boolean matches(final String rawPassword, final String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }

}
