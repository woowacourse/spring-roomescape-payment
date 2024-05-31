package roomescape.service;

import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.domain.MemberEmail;
import roomescape.domain.MemberName;
import roomescape.domain.MemberPassword;
import roomescape.domain.repository.MemberRepository;
import roomescape.exception.RoomescapeErrorCode;
import roomescape.exception.RoomescapeException;
import roomescape.service.request.MemberSignUpDto;
import roomescape.service.response.MemberDto;
import roomescape.web.auth.CookieHandler;

@Service
@Transactional(readOnly = true)
public class MemberAuthService {

    private final MemberRepository memberRepository;
    private final CookieHandler cookieHandler;

    public MemberAuthService(MemberRepository memberRepository, CookieHandler cookieHandler) {
        this.memberRepository = memberRepository;
        this.cookieHandler = cookieHandler;
    }

    @Transactional
    public MemberDto signUp(MemberSignUpDto request) {
        if (memberRepository.existsByEmail(new MemberEmail(request.email()))) {
            throw new RoomescapeException(RoomescapeErrorCode.DUPLICATED_MEMBER, "해당 이메일의 회원이 이미 존재합니다.");
        }

        Member newMember = Member.createUser(
                new MemberName(request.name()),
                new MemberEmail(request.email()),
                new MemberPassword(request.password())
        );

        Member savedMember = memberRepository.save(newMember);
        return new MemberDto(savedMember.getId(), savedMember.getName(), savedMember.getRole().name());
    }

    public MemberDto findMemberByEmail(String email) {
        return memberRepository.findByEmail(new MemberEmail(email))
                .map(MemberDto::from)
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 찾지 못했습니다. 다시 로그인 해주세요."));
    }

    public List<MemberDto> findAll() {
        return memberRepository.findAll().stream()
                .map(member -> new MemberDto(member.getId(), member.getName(), member.getRole().name()))
                .toList();
    }

    public boolean isExistsMemberByEmailAndPassword(String email, String password) {
        if (memberRepository.existsByEmailAndPassword(new MemberEmail(email), new MemberPassword(password))) {
            return true;
        }
        throw new RoomescapeException(RoomescapeErrorCode.UNAUTHORIZED, "이메일 또는 비밀번호가 잘못되었습니다.");
    }

    public Cookie createCookieByToken(String token) {
        return cookieHandler.createCookieByToken(token);
    }

    public String extractTokenFromCookies(Cookie[] cookies) {
        return cookieHandler.extractTokenFromCookies(cookies);
    }
}
