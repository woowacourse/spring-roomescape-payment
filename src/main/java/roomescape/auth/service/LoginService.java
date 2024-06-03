package roomescape.auth.service;

import static roomescape.exception.type.RoomescapeExceptionType.NOT_FOUND_MEMBER_BY_EMAIL;
import static roomescape.exception.type.RoomescapeExceptionType.REQUIRED_LOGIN;
import static roomescape.exception.type.RoomescapeExceptionType.WRONG_PASSWORD;

import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import roomescape.auth.config.JwtGenerator;
import roomescape.auth.domain.Role;
import roomescape.auth.dto.LoginRequest;
import roomescape.exception.RoomescapeException;
import roomescape.member.domain.LoginMember;
import roomescape.member.entity.Member;
import roomescape.member.repository.MemberRepository;

@Service
public class LoginService {

    private final MemberRepository memberRepository;
    private final JwtGenerator jwtGenerator;

    public LoginService(MemberRepository memberRepository, JwtGenerator jwtGenerator) {
        this.memberRepository = memberRepository;
        this.jwtGenerator = jwtGenerator;
    }

    public String getLoginToken(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER_BY_EMAIL, loginRequest.email()));
        if (!findMember.getPassword().equals(loginRequest.password())) {
            throw new RoomescapeException(WRONG_PASSWORD, loginRequest.password());
        }

        return jwtGenerator.generateWith(Map.of(
                "id", findMember.getId(),
                "name", findMember.getName(),
                "role", findMember.getRole().getTokenValue()
        ));
    }

    public LoginMember checkLogin(String token) {
        try {
            Claims claims = jwtGenerator.getClaims(token);
            return new LoginMember(
                    claims.get("id", Long.class),
                    claims.get("name", String.class),
                    Role.findByValue(claims.get("role", String.class))
            );
        } catch (ExpiredJwtException e) {
            throw new RoomescapeException(REQUIRED_LOGIN);
        }
    }
}
