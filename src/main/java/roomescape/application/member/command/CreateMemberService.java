package roomescape.application.member.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.member.command.dto.RegisterCommand;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.infrastructure.error.exception.MemberException;

@Service
@Transactional
public class CreateMemberService {

    private final MemberRepository memberRepository;

    public CreateMemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long register(final RegisterCommand registerCommand) {
        final Email email = new Email(registerCommand.email());
        validateDuplicateEmail(email);
        final Member member = new Member(registerCommand.name(), email, registerCommand.password());
        final Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    private void validateDuplicateEmail(final Email email) {
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException("이미 같은 이메일을 가진 사용자가 존재합니다.");
        }
    }
}
