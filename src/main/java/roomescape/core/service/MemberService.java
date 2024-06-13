package roomescape.core.service;

import static roomescape.core.exception.ExceptionMessage.ALREADY_USED_EMAIL_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.INVALID_EMAIL_OR_PASSWORD_EXCEPTION;
import static roomescape.core.exception.ExceptionMessage.MEMBER_NOT_FOUND_EXCEPTION;

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
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    public MemberService(final TokenProvider tokenProvider, final MemberRepository memberRepository) {
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
    }

    public TokenResponse createToken(final TokenRequest request) {
        final Member member = memberRepository.findByEmailAndPassword(request.getEmail(), request.getPassword());
        if (member == null) {
            throw new IllegalArgumentException(INVALID_EMAIL_OR_PASSWORD_EXCEPTION.getMessage());
        }
        return new TokenResponse(tokenProvider.createToken(member.getEmail(), member.getRole().name()));
    }

    @Transactional(readOnly = true)
    public MemberResponse findMemberByToken(final String token) {
        final String email = tokenProvider.getPayload(token);
        final Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION.getMessage());
        }
        return new MemberResponse(member.getId(), member.getName());
    }

    @Transactional(readOnly = true)
    public LoginMember findLoginMemberByToken(final String token) {
        final String email = tokenProvider.getPayload(token);
        final Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException(MEMBER_NOT_FOUND_EXCEPTION.getMessage());
        }
        return LoginMember.from(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @Transactional
    public MemberResponse create(final MemberRequest request) {
        final Member member = new Member(request.getName(), request.getEmail(), request.getPassword(), Role.USER);
        try {
            final Member savedMember = memberRepository.save(member);
            return new MemberResponse(savedMember.getId(), savedMember.getName());
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(ALREADY_USED_EMAIL_EXCEPTION.getMessage());
        }
    }
}
