package roomescape.service;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.entity.Member;
import roomescape.global.Role;
import roomescape.jwt.JwtTokenProvider;

@Service
@Slf4j
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String createTokenByMember(Member member) {
        log.info("JWT 토큰 생성 시작 - memberId: {}, email: {}", member.getId(), member.getEmail());

        String token = jwtTokenProvider.createTokenByMember(member);

        log.info("JWT 토큰 생성 완료 - memberId: {}", member.getId());
        return token;
    }

    public LoginMemberRequest getLoginMemberByToken(String token) {
        log.info("토큰에서 로그인 멤버 정보 추출 시작");

        Claims claims = jwtTokenProvider.getClaimsFromToken(token);
        Long memberId = Long.valueOf(claims.getSubject());
        String name = claims.get("name", String.class);
        String role = claims.get("role", String.class);

        LoginMemberRequest loginMemberRequest = new LoginMemberRequest(memberId, name, Role.valueOf(role));

        log.info("토큰에서 로그인 멤버 정보 추출 완료 - memberId: {}, name: {}, role: {}",
                memberId, name, role);
        return loginMemberRequest;
    }
}
