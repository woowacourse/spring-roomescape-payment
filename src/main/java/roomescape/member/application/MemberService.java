package roomescape.member.application;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import roomescape.global.exception.ViolationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;

import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member create(Member member) {
        validateDuplicatedEmail(member);
        try {
            return memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            throw new ViolationException("중복된 이메일입니다.");
        }
    }

    private void validateDuplicatedEmail(Member member) {
        boolean isDuplicatedEmail = memberRepository.findByEmail(member.getEmail())
                .isPresent();
        if (isDuplicatedEmail) {
            throw new ViolationException("중복된 이메일입니다.");
        }
    }

    public List<Member> findAll() {
        return memberRepository.findAll();
    }
}
