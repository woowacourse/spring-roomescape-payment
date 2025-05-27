package roomescape.member.controller;

import static roomescape.member.controller.response.MemberSuccessCode.GET_MEMBERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.response.ApiResponse;
import roomescape.member.controller.response.MemberResponse;
import roomescape.member.service.MemberService;


@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class MemberAdminApiController {

    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MemberResponse>>> getMembers() {
        List<MemberResponse> responses = memberService.getMembers();

        return ResponseEntity.ok(
                ApiResponse.success(GET_MEMBERS, responses));
    }
}
