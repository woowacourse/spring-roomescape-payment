package roomescape.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.config.annotation.Authority;
import roomescape.domain.Role;
import roomescape.dto.response.MemberProfileResponse;
import roomescape.service.MemberService;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService, ReservationService reservationService) {
        this.memberService = memberService;
    }

    @GetMapping
    @Authority(Role.ADMIN)
    public List<MemberProfileResponse> findAllMember() {
        return memberService.findAllMemberProfile();
    }
}
