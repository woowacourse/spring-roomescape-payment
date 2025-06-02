package roomescape.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class MemberHelper {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member getById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 사용자가 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 사용자가 존재하지 않습니다."));
    }
}
