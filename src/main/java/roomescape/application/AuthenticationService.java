package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.domain.auth.AuthenticationTokenHandler;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRepository;
import roomescape.exception.AuthenticationException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationTokenHandler tokenHandler;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public String issueToken(final String email, final String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("이메일이 틀렸습니다."));

        if (!user.matchesPassword(password)) {
            throw new AuthenticationException("비밀번호가 틀렸습니다.");
        }

        AuthenticationInfo authenticationInfo = new AuthenticationInfo(user.getId(), user.getRole());
        return tokenHandler.createToken(authenticationInfo);
    }

    @Transactional(readOnly = true)
    public User getUserByToken(final String token) {
        boolean isValidToken = tokenHandler.isValidToken(token);

        if (!isValidToken) {
            throw new AuthenticationException("토큰이 만료되었거나 유효하지 않습니다.");
        }

        long id = tokenHandler.extractId(token);

        return userRepository.findById(id)
                .orElseThrow(() -> new AuthenticationException("사용자 정보가 없습니다. 다시 로그인 해주세요."));
    }
}
