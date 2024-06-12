package roomescape.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import roomescape.domain.Email;
import roomescape.domain.Member;
import roomescape.domain.Name;
import roomescape.domain.Role;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.request.LoginRequest;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;

import java.util.Map;

import static roomescape.exception.ExceptionType.*;

@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final JwtGenerator jwtGenerator;
    private final Logger logger = LogManager.getLogger(LoginService.class);

    public LoginService(MemberRepository memberRepository, JwtGenerator jwtGenerator) {
        this.memberRepository = memberRepository;
        this.jwtGenerator = jwtGenerator;
    }

    public String getLoginToken(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmail(new Email(loginRequest.email()))
                .orElseThrow(() -> new RoomescapeException(NOT_FOUND_MEMBER));
        if (!findMember.getPassword().getValue().equals(loginRequest.password())) {
            throw new RoomescapeException(WRONG_PASSWORD);
        }

        return jwtGenerator.generateWith(Map.of(
                "id", findMember.getId(),
                "name", findMember.getName().getValue(),
                "role", findMember.getRole().getTokenValue()
        ));
    }

    public LoginMemberRequest checkLogin(String token) {
        try {
            Claims claims = jwtGenerator.getClaims(token);
            return new LoginMemberRequest(
                    claims.get("id", Long.class),
                    new Name(claims.get("name", String.class)),
                    Role.findByValue(claims.get("role", String.class))
            );
        } catch (ExpiredJwtException e) {
            logger.debug(e.getMessage(), e);
            throw new RoomescapeException(REQUIRED_LOGIN);
        }
    }
}
