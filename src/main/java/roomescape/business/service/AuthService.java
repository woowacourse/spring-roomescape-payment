package roomescape.business.service;

import static roomescape.exception.SecurityErrorCode.INVALID_EMAIL;
import static roomescape.exception.SecurityErrorCode.INVALID_PASSWORD;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.AuthToken;
import roomescape.auth.jwt.JwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.exception.auth.AuthenticationException;
import roomescape.infrastructure.MemberRepository;
import roomescape.presentation.dto.request.LoginRequest;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public AuthToken authenticate(LoginRequest request) {
        final Member member = memberRepository.findByEmail_Value(request.email())
                .orElseThrow(() -> new AuthenticationException(INVALID_EMAIL));

        if (!member.isPasswordCorrect(request.password())) {
            throw new AuthenticationException(INVALID_PASSWORD);
        }

        return jwtUtil.createToken(member);
    }
}
