package roomescape.member.service.usecase;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.ConflictException;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.Account;
import roomescape.member.domain.Member;
import roomescape.member.log.MemberProbe;
import roomescape.member.repository.AccountRepository;
import roomescape.member.repository.MemberRepository;
import roomescape.member.service.MemberConverter;

@Service
@RequiredArgsConstructor
public class MemberCommandUseCase {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final MemberProbe memberProbe;

    @Transactional
    public MemberInfo create(Account account) {
        if (memberRepository.existsByEmail(account.getMember().getEmail())) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }

        Member newMember = memberRepository.save(account.getMember());
        final MemberInfo dto = MemberConverter.toDto(newMember);
        accountRepository.save(account);
        memberProbe.signup(newMember);
        return dto;
    }
}
