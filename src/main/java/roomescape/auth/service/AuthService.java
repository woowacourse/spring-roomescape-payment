package roomescape.auth.service;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Service;
import roomescape.auth.domain.Token;
import roomescape.auth.dto.LoginRequest;
import roomescape.auth.provider.model.TokenProvider;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.MemberExceptionCode;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;

@Tag(name = "Auth 서비스", description = "로그인 요청 정보로 사용자가 db에 존재하는지 확인한 뒤 존재하면 Token 객체를 반환한다.")
@Service
public class AuthService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public AuthService(MemberRepository memberRepository, TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
    }

    public Token login(LoginRequest loginRequest) {
        Member member = memberRepository.findMemberByEmailAndPassword(loginRequest.email(),
                        loginRequest.password())
                .orElseThrow(() -> new RoomEscapeException(MemberExceptionCode.MEMBER_NOT_EXIST_EXCEPTION));

        return tokenProvider.getAccessToken(member.getId());
    }
}
