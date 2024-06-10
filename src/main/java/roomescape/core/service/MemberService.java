package roomescape.core.service;

import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.core.domain.Member;
import roomescape.core.domain.Role;
import roomescape.core.dto.auth.TokenRequest;
import roomescape.core.dto.auth.TokenResponse;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.member.MemberRequest;
import roomescape.core.dto.member.MemberResponse;
import roomescape.core.repository.MemberRepository;
import roomescape.infrastructure.TokenProvider;

@Service
public class MemberService {
    protected static final String MEMBER_NOT_FOUND_EXCEPTION_MESSAGE = "올바르지 않은 이메일 또는 비밀번호입니다.";
    protected static final String MEMBER_NOT_EXISTS_EXCEPTION_MESSAGE = "존재하지 않는 사용자입니다.";
    protected static final String ALREADY_USED_EMAIL_EXCEPTION_MESSAGE = "이미 사용 중인 이메일입니다.";

    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public MemberService(final TokenProvider tokenProvider,
                         final MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
    }

    public TokenResponse createToken(final TokenRequest request) {
        final Member member = memberRepository
                .findByEmailAndPassword(request.getEmail(), request.getPassword());
        if (member == null) {
            throw new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION_MESSAGE);
        }
        return new TokenResponse(
                tokenProvider.createToken(member.getEmail(), member.getRole().name()));
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberByToken(final String token) {
        final String email = tokenProvider.getPayload(token);
        final Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException(MEMBER_NOT_EXISTS_EXCEPTION_MESSAGE);
        }
        return new MemberResponse(member);
    }

    @Transactional(readOnly = true)
    public LoginMember findLoginMemberByToken(final String token) {
        final String email = tokenProvider.getPayload(token);
        final Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException(MEMBER_NOT_EXISTS_EXCEPTION_MESSAGE);
        }
        return new LoginMember(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::new)
                .toList();
    }

    @Transactional
    public MemberResponse create(final MemberRequest request) {
        final Member member = new Member(request.getName(), request.getEmail(),
                request.getPassword(), Role.USER);
        try {
            final Member savedMember = memberRepository.save(member);
            return new MemberResponse(savedMember);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(ALREADY_USED_EMAIL_EXCEPTION_MESSAGE);
        }
    }
}
