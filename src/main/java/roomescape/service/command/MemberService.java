package roomescape.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.dto.business.MemberCreationContent;
import roomescape.dto.response.MemberProfileResponse;
import roomescape.exception.BadRequestException;
import roomescape.repository.MemberRepository;
import roomescape.service.query.MemberQueryService;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberQueryService memberQueryService;

    public MemberService(
            MemberRepository memberRepository,
            MemberQueryService memberQueryService
    ) {
        this.memberRepository = memberRepository;
        this.memberQueryService = memberQueryService;
    }

    public MemberProfileResponse addMember(MemberCreationContent request) {
        validateDuplicatedEmail(request.email());
        Member member = Member.createWithoutId(request.role(), request.name(), request.email(), request.password());
        Member savedMember = memberRepository.save(member);
        return new MemberProfileResponse(savedMember);
    }

    private void validateDuplicatedEmail(String email) {
        boolean isDuplicatedEmail = memberQueryService.existsMemberInEmail(email);
        if (isDuplicatedEmail) {
            throw new BadRequestException("이미 존재하는 계정입니다.");
        }
    }
}
