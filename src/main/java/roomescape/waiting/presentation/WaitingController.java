package roomescape.waiting.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.common.argumentResolver.Login;
import roomescape.member.adaptor.MemberApiAdaptor;
import roomescape.member.docs.LoginMemberDocs;
import roomescape.member.dto.request.LoginMember;
import roomescape.waiting.adaptor.WaitingApiAdaptor;
import roomescape.waiting.docs.WaitingRequestDocs;
import roomescape.waiting.docs.WaitingResponseDocs;
import roomescape.waiting.dto.request.WaitingRequest;
import roomescape.waiting.dto.response.WaitingResponse;
import roomescape.waiting.service.WaitingService;

import java.net.URI;
import java.util.List;

@RestController
public class WaitingController {

    private final WaitingService waitingService;
    private final WaitingApiAdaptor waitingApiAdaptor;
    private final MemberApiAdaptor memberApiAdaptor;

    public WaitingController(WaitingService waitingService, WaitingApiAdaptor waitingApiAdaptor, MemberApiAdaptor memberApiAdaptor) {
        this.waitingService = waitingService;
        this.waitingApiAdaptor = waitingApiAdaptor;
        this.memberApiAdaptor = memberApiAdaptor;
    }

    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponseDocs> createWaiting(
            @RequestBody final WaitingRequestDocs requestDocs,
            @Login final LoginMemberDocs loginMemberDocs
    ) {
        WaitingRequest request = waitingApiAdaptor.toWaitingRequest(requestDocs);
        LoginMember loginMember = memberApiAdaptor.toLoginMember(loginMemberDocs);

        WaitingResponse response = waitingService.createWaiting(request, loginMember);
        WaitingResponseDocs responseDocs = waitingApiAdaptor.toWaitingResponseDocs(response);
        URI location = URI.create("/waitings/" + response.id());
        return ResponseEntity.created(location).body(responseDocs);
    }

    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponseDocs>> getWaitings() {
        List<WaitingResponse> waitings = waitingService.getAllWaitings();
        List<WaitingResponseDocs> waitingDocs = waitings.stream()
                .map(waitingApiAdaptor::toWaitingResponseDocs)
                .toList();
        return ResponseEntity.ok(waitingDocs);
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> cancelWaiting(@PathVariable Long id) {
        waitingService.cancelWaiting(id);
        return ResponseEntity.noContent().build();
    }
}
