package roomescape.member.service;

import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberPassword;
import roomescape.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class MemberQueryService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public MemberQueryService(final PasswordEncoder passwordEncoder, final MemberRepository memberRepository) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    public Member login(final MemberEmail email, final MemberPassword password) {
        Member member = getByEmail(email);
        if (!member.isMatchPassword(password, passwordEncoder)) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 멤버입니다."));
    }

    private Member getByEmail(MemberEmail email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자입니다."));
    }
}
