package roomescape.member.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import roomescape.member.controller.api.MemberApi;
import roomescape.member.dto.MemberCreateRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;

@RequiredArgsConstructor
@RestController
public class MemberController implements MemberApi {

    private final MemberService memberService;

    @Override
    public ResponseEntity<Void> create(
            @RequestBody @Valid final MemberCreateRequest request
    ) {
        memberService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public ResponseEntity<List<MemberResponse>> findAll() {
        final List<MemberResponse> responses = memberService.findAll();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responses);
    }
}
