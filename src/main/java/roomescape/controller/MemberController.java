package roomescape.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.configuration.annotation.Authority;
import roomescape.domain.Role;
import roomescape.dto.response.MemberProfileResponse;
import roomescape.service.query.MemberQueryService;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberQueryService memberQueryService;

    public MemberController(MemberQueryService memberQueryService) {
        this.memberQueryService = memberQueryService;
    }

    @GetMapping
    @Authority(Role.ADMIN)
    public List<MemberProfileResponse> findAllMember() {
        return memberQueryService.findAllMemberProfile();
    }
}
