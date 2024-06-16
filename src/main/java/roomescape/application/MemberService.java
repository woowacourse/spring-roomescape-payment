package roomescape.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.dto.request.member.LoginRequest;
import roomescape.application.dto.request.member.SignupRequest;
import roomescape.application.dto.response.member.MemberResponse;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Member;
import roomescape.exception.member.AuthenticationFailureException;
import roomescape.exception.member.DuplicatedEmailException;
import roomescape.infrastructure.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public List<MemberResponse> findAllMember() {
        return memberRepository.findAll()
                .stream()
                .map(member -> new MemberResponse(member.getId(), member.getName()))
                .toList();
    }

    public String login(LoginRequest loginRequest) {
        Member findMember = memberRepository.findByEmailAndPassword(loginRequest.email(), loginRequest.password())
                .orElseThrow(AuthenticationFailureException::new);
        return "token=" + jwtProvider.encode(findMember);
    }

    @Transactional
    public long signup(SignupRequest request) {
        validateDuplicatedEmial(request.email());
        Member savedMember = memberRepository.save(request.toMember());
        return savedMember.getId();
    }

    private void validateDuplicatedEmial(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException();
        }
    }

    @Transactional
    public void withdrawal(Long memberId) {
        Member findMember = memberRepository.findById(memberId).orElseThrow(AuthenticationFailureException::new);
        memberRepository.delete(findMember);
    }
}
