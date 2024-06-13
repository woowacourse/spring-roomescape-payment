package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.MemberService;
import roomescape.dto.response.member.MemberResponse;
import roomescape.dto.request.member.MemberSignUpRequest;

@Tag(name = "사용자 회원 API", description = "사용자 회원 관련 API 입니다.")
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "사용자 조회 API")
    @GetMapping
    public ResponseEntity<List<MemberResponse>> members() {
        List<MemberResponse> memberResponses = memberService.findAll();
        return ResponseEntity.ok(memberResponses);
    }

    @Operation(summary = "사용자 회원가입 API")
    @PostMapping
    public ResponseEntity<MemberResponse> save(@RequestBody @Valid MemberSignUpRequest memberSignUpRequest) {
        MemberResponse memberResponse = memberService.save(memberSignUpRequest);
        return ResponseEntity.created(URI.create("/members/" + memberResponse.id()))
                .body(memberResponse);
    }
}
