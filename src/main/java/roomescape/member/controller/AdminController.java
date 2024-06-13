package roomescape.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.controller.dto.MemberResponse;
import roomescape.member.service.MemberService;

@Tag(name = "Admin", description = "Admin API")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;

    public AdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public List<MemberResponse> findAll() {
        return memberService.findAll();
    }
}
