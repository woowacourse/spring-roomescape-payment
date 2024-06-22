package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.controller.dto.MemberResponse;
import roomescape.member.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin API", description = "어드민 관련 API")
public class AdminController {

    private final MemberService memberService;

    public AdminController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    @Operation(summary = "모든 멤버를 조회한다.")
    public List<MemberResponse> findAll() {
        return memberService.findAll();
    }
}
