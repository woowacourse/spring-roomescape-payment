package roomescape.domain.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.domain.Member;
import roomescape.domain.member.dto.MemberCreationContent;
import roomescape.domain.member.repository.MemberRepository;
import roomescape.domain.member.response.AddMemberResponse;
import roomescape.exception.BadRequestException;

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

    public AddMemberResponse addMember(MemberCreationContent request) {
        validateDuplicatedEmail(request.email());
        Member member = Member.createWithoutId(request.role(), request.name(), request.email(), request.password());
        Member savedMember = memberRepository.save(member);
        return new AddMemberResponse(savedMember);
    }

    private void validateDuplicatedEmail(String email) {
        boolean isDuplicatedEmail = memberQueryService.existsMemberInEmail(email);
        if (isDuplicatedEmail) {
            throw new BadRequestException("이미 존재하는 계정입니다.");
        }
    }
}
