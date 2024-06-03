package roomescape.domain.member.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.member.service.MemberService;
import roomescape.domain.member.dto.MemberDto;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.member.dto.SaveMemberRequest;

import java.util.List;

@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/admin/members")
    public List<MemberResponse> getMembers() {
        return memberService.getMembers()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    @PostMapping("/members")
    public MemberResponse saveMember(@RequestBody final SaveMemberRequest request) {
        final MemberDto savedMember = memberService.saveMember(request);
        return MemberResponse.from(savedMember);
    }
}
