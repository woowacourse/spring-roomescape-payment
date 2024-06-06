package roomescape.service;

import static roomescape.exception.RoomescapeExceptionCode.MEMBER_NOT_FOUND;

import org.springframework.stereotype.Service;
import roomescape.auth.JwtTokenProvider;
import roomescape.domain.member.Member;
import roomescape.dto.member.MemberResponse;
import roomescape.dto.auth.TokenRequest;
import roomescape.dto.auth.TokenResponse;
import roomescape.exception.RoomescapeException;
import roomescape.repository.MemberRepository;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberService(final MemberRepository memberRepository,
                         final JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public TokenResponse createToken(final TokenRequest request) {
        final Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new RoomescapeException(MEMBER_NOT_FOUND));
        member.checkIncorrectPassword(request.password());
        final String accessToken = jwtTokenProvider.createToken(member);
        return new TokenResponse(accessToken);
    }

    public MemberResponse findById(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RoomescapeException(MEMBER_NOT_FOUND));
        return new MemberResponse(member.getId(), member.getNameString(), member.getEmail(), member.getRole());
    }

    public MemberResponse findMemberByToken(final String accessToken) {
        final Long memberId = jwtTokenProvider.getMemberIdByToken(accessToken);
        return findById(memberId);
    }

    public List<MemberResponse> findAll() {
        final List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }
}
