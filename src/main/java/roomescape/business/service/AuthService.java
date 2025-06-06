package roomescape.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.AuthToken;
import roomescape.auth.jwt.JwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.exception.auth.EmailNotRegisteredException;
import roomescape.exception.auth.InvalidPasswordException;
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
                .orElseThrow(EmailNotRegisteredException::new);

        if (!member.isPasswordCorrect(request.password())) {
            throw new InvalidPasswordException();
        }

        return jwtUtil.createToken(member);
    }
}
