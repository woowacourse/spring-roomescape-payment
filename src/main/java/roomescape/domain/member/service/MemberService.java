package roomescape.domain.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.entity.Member;
import roomescape.domain.member.exception.MemberNotFoundException;
import roomescape.domain.member.repository.MemberRepositoryInterface;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepositoryInterface memberRepository;

    @Transactional(readOnly = true)
    public Member findMemberByEmail(final String email) {

        return validateMember(email);
    }

    @Transactional(readOnly = true)
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    private Member validateMember(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원 데이터가 존재하지 않습니다. email = " + email));
    }
}
