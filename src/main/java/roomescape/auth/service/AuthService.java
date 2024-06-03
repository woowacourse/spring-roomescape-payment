package roomescape.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.controller.dto.LoginRequest;
import roomescape.auth.controller.dto.MemberResponse;
import roomescape.auth.controller.dto.TokenResponse;
import roomescape.auth.domain.AuthInfo;
import roomescape.auth.service.dto.SignUpCommand;
import roomescape.exception.AuthenticationException;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;

@Service
public class AuthService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
                       TokenProvider tokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public void authenticate(LoginRequest loginRequest) {
        Member member = memberRepository.findMemberByEmailAddress(loginRequest.email())
                .orElseThrow(() -> new AuthenticationException(ErrorType.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(loginRequest.password(), member.getPassword())) {
            throw new AuthenticationException(ErrorType.SECURITY_EXCEPTION);
        }
    }

    public TokenResponse createToken(LoginRequest loginRequest) {
        return new TokenResponse(tokenProvider.createAccessToken(loginRequest.email()));
    }

    public AuthInfo fetchByToken(String token) {
        Member member = memberRepository.findMemberByEmailAddress(tokenProvider.getPayload(token).getValue())
                .orElseThrow(() -> new AuthenticationException(ErrorType.TOKEN_PAYLOAD_EXTRACTION_FAILURE));
        return AuthInfo.from(member);
    }

    @Transactional
    public MemberResponse signUp(SignUpCommand signUpCommand) {
        if (memberRepository.existsByEmailAddress(signUpCommand.email())) {
            throw new BadRequestException(ErrorType.DUPLICATED_EMAIL_ERROR);
        }
        return MemberResponse.from(
                memberRepository.save(new Member(
                        signUpCommand.name(),
                        signUpCommand.email(),
                        passwordEncoder.encode(signUpCommand.password()),
                        Role.USER)
                )
        );
    }
}
