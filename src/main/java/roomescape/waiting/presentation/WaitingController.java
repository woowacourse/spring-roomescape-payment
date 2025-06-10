package roomescape.waiting.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.common.argumentResolver.Login;
import roomescape.member.dto.request.LoginMember;
import roomescape.waiting.dto.request.WaitingRequest;
import roomescape.waiting.dto.response.WaitingResponse;
import roomescape.waiting.service.WaitingService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class WaitingController implements WaitingControllerDocs {

    private final WaitingService waitingService;

    @Override
    public ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody final WaitingRequest request,
            @Login final LoginMember loginMember
    ) {
        WaitingResponse response = waitingService.createWaiting(request, loginMember);
        URI location = URI.create("/waitings/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<List<WaitingResponse>> getWaitings() {
        List<WaitingResponse> waitings = waitingService.getAllWaitings();
        return ResponseEntity.ok(waitings);
    }

    @Override
    public ResponseEntity<Void> cancelWaiting(@PathVariable Long id) {
        waitingService.cancelWaiting(id);
        return ResponseEntity.noContent().build();
    }
}
