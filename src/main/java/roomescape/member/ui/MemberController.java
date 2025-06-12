package roomescape.member.ui;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.member.application.MemberService;
import roomescape.member.application.dto.MemberRequest;
import roomescape.member.application.dto.MemberResponse;

@Tag(name = "멤버 API", description = "멤버 관련 API입니다.")
@RestController
@RequestMapping("members")
@AllArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원가입", description = "멤버를 생성하는 회원가입 API입니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> create(@Valid @RequestBody MemberRequest request) {
        MemberResponse response = memberService.create(request);
        ApiResponse<MemberResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }
}
