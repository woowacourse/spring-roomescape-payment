package roomescape.mvc.member.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.BadRequestException;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.member.dto.MemberCreationContent;
import roomescape.mvc.member.repository.MemberRepository;
import roomescape.mvc.member.response.AddMemberResponse;

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
