package roomescape.member.controller;

import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.member.controller.dto.LoginRequest;
import roomescape.member.controller.dto.MemberResponse;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberPassword;
import roomescape.member.service.MemberQueryService;

@Service
public class MemberService {
    private final MemberQueryService memberQueryService;

    public MemberService(final MemberQueryService memberQueryService) {
        this.memberQueryService = memberQueryService;
    }

    public List<MemberResponse> findAllMembers() {
        return MemberResponse.from(memberQueryService.findAllMembers());
    }

    public Member login(final LoginRequest request) {
        return memberQueryService.login(
                new MemberEmail(request.email()),
                new MemberPassword(request.password())
        );
    }
}
