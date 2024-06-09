package roomescape.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.JwtTokenProvider;
import roomescape.auth.dto.*;
import roomescape.exception.BadRequestException;
import roomescape.exception.ResourceNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;

@Service
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public AuthService(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 멤버입니다."));
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Member member = getMemberByEmail(request.email());
        member.validatePassword(request.password());

        String accessToken = jwtTokenProvider.generate(member);
        return new LoginResponse(accessToken);
    }

    @Transactional(readOnly = true)
    public LoginCheckResponse checkLogin(LoginMember loginMember) {
        Member member = getMemberByEmail(loginMember.email());
        return LoginCheckResponse.from(member);
    }

    @Transactional(rollbackFor = Exception.class)
    public LoginCheckResponse signup(SignupRequest request) {
        Member member = request.toMember();

        validateDuplicatedEmail(member);

        Member savedMember = memberRepository.save(member);
        return LoginCheckResponse.from(savedMember);
    }

    private void validateDuplicatedEmail(Member member) {
        memberRepository.findByEmail(member.getEmail())
                .ifPresent((existsMember) -> {
                    throw new BadRequestException("이미 존재하는 이메일입니다.");
                });
    }
}
