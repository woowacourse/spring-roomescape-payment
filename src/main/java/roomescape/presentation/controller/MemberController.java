package roomescape.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.MemberService;
import roomescape.dto.response.MemberResponseDto;

@Tag(name = "회원 정보 관련 API")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 전체 정보 조회")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MemberResponseDto> findAll() {
        return memberService.findAll();
    }
}
