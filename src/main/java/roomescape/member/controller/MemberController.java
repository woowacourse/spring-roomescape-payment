package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.dto.MemberRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;

@Tag(name = "Member", description = "회원 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 목록 조회", description = "모든 회원 목록을 조회합니다.")
    @GetMapping
    public List<MemberResponse> findAllMember() {
        return memberService.findAllMember();
    }

    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(
            @Parameter(description = "회원 가입 요청 정보") @RequestBody @Valid MemberRequest request
    ) {
        memberService.signup(request);
    }
}
