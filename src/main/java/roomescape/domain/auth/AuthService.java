package roomescape.domain.auth;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.domain.auth.dto.LoginRequest;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.exception.custom.reason.auth.AuthNotExistsEmailException;
import roomescape.exception.custom.reason.auth.AuthNotValidPasswordException;

@Service
@AllArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final AuthJwtProvider jwtProvider;
    private final AuthPasswordEncoder passwordEncoder;

    public String generateToken(final LoginRequest loginRequest) {
        final Member member = memberRepository.findByEmail(loginRequest.email())
                .orElseThrow(AuthNotExistsEmailException::new);
        validatePassword(loginRequest, member);

        return jwtProvider.provideToken(member.getEmail(), member.getRole(), member.getName());
    }

    private void validatePassword(final LoginRequest loginRequest, final Member member) {
        if (!passwordEncoder.matches(loginRequest.password(), member.getPassword())) {
            throw new AuthNotValidPasswordException();
        }
    }
}
