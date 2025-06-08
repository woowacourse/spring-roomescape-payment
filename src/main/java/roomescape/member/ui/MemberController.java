package roomescape.member.ui;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.response.ApiResponse;
import roomescape.member.application.MemberService;
import roomescape.member.application.dto.MemberRequest;
import roomescape.member.application.dto.MemberResponse;

@RestController
@RequestMapping("members")
@AllArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @Operation(
            summary = "회원 가입",
            description = "회원 정보를 받아 새로운 회원을 등록합니다. 중복된 이메일은 등록할 수 없습니다."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<MemberResponse>> create(@Valid @RequestBody MemberRequest request) {
        log.info("회원가입 요청: email={}", request.email());
        MemberResponse response = memberService.create(request);
        ApiResponse<MemberResponse> apiResponse = ApiResponse.createSuccess(response);
        return ResponseEntity.ok(apiResponse);
    }
}
