package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.dto.MultipleResponses;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.MemberSignUpRequest;
import roomescape.member.service.MemberService;

@Tag(name = "회원 API", description = "방탈출 회원용 API 입니다.")
@RestController
public class MemberApiController {

    private final MemberService memberService;

    public MemberApiController(MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원 가입 API", description = "회원을 추가 합니다.")
    @PostMapping("/members")
    public ResponseEntity<Void> signup(@Valid @RequestBody MemberSignUpRequest memberSignUpRequest) {
        MemberResponse memberResponse = memberService.save(memberSignUpRequest);

        return ResponseEntity.created(URI.create("/members/" + memberResponse.id())).build();
    }

    @Operation(summary = "회원 조회 API", description = "회원을 조회 합니다.")
    @GetMapping("/members")
    public ResponseEntity<MultipleResponses<MemberResponse>> findAll() {
        List<MemberResponse> memberResponses = memberService.findAll();

        return ResponseEntity.ok(new MultipleResponses<>(memberResponses));
    }
}
