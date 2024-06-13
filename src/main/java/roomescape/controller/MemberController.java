package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.MemberService;
import roomescape.service.dto.request.MemberJoinRequest;
import roomescape.service.dto.response.MemberResponse;

@Tag(name = "[MEMBER] 회원 API", description = "회원가입을 할 수 있습니다.")
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원가입 API")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "인증/인가에 필요한 사용자 정보를 반환합니다.")
    })
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> save(@RequestBody @Valid MemberJoinRequest memberRequest) {
        MemberResponse memberResponse = memberService.join(memberRequest);

        return ResponseEntity.created(URI.create("/members/" + memberResponse.id()))
                .body(memberResponse);
    }
}
