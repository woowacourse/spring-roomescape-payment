package roomescape.business.service;

import static roomescape.exception.SecurityErrorCode.INVALID_EMAIL;
import static roomescape.exception.SecurityErrorCode.INVALID_PASSWORD;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.AuthToken;
import roomescape.auth.jwt.JwtUtil;
import roomescape.business.model.entity.User;
import roomescape.exception.auth.AuthenticationException;
import roomescape.infrastructure.UserRepository;
import roomescape.presentation.dto.request.LoginRequest;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthToken authenticate(LoginRequest request) {
        final User user = userRepository.findByEmail_Value(request.email())
                .orElseThrow(() -> new AuthenticationException(INVALID_EMAIL));

        if (!user.isPasswordCorrect(request.password())) {
            throw new AuthenticationException(INVALID_PASSWORD);
        }

        return jwtUtil.createToken(user);
    }
}
