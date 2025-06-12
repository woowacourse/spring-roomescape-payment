package roomescape.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.dto.MemberRequest;
import roomescape.member.dto.MemberResponses;
import roomescape.member.service.MemberService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/admin/members")
    public MemberResponses findAllMember() {
        return memberService.findAllMember();
    }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@RequestBody @Valid MemberRequest request) {
        memberService.save(request);
    }
}
