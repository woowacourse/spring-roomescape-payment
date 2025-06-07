package roomescape.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.application.AdminMemberService;
import roomescape.member.application.dto.response.MemberServiceResponse;
import roomescape.member.controller.dto.MemberResponse;

@Tag(name = "admin", description = "관리자 도메인 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<MemberResponse> getAll() {
        List<MemberServiceResponse> responses = adminMemberService.getAll();
        return responses.stream()
                .map(MemberResponse::from)
                .toList();
    }
}
