package roomescape.member.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BadRequestException;
import roomescape.global.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberPassword;
import roomescape.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
@Slf4j
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
            log.warn("[AUTH-FAIL] 로그인 실패 - 비밀번호 불일치");
            throw new BadRequestException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member getById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("[NOT-FOUND] 회원 조회 실패 - ID: {}", memberId);
                    return new NotFoundException("존재하지 않는 사용자입니다.");
                });
    }

    private Member getByEmail(MemberEmail email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("[NOT-FOUND] 회원 조회 실패 - 이메일");
                    return new NotFoundException("존재하지 않는 사용자입니다.");
                });
    }
}
