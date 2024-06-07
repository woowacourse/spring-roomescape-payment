package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.MemberService;
import roomescape.service.dto.request.MemberJoinRequest;
import roomescape.service.dto.response.MemberResponse;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원가입 API", description = "회원가입후 사용자 정보를 반환한다.")
    @PostMapping("/members")
    public ResponseEntity<MemberResponse> save(@RequestBody @Valid MemberJoinRequest memberRequest) {
        MemberResponse memberResponse = memberService.join(memberRequest);

        return ResponseEntity.created(URI.create("/members/" + memberResponse.id()))
                .body(memberResponse);
    }
}
