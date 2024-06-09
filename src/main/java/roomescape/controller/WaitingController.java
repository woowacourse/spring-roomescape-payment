package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.annotation.AuthenticationPrincipal;
import roomescape.controller.request.WaitingRequest;
import roomescape.controller.response.WaitingResponse;
import roomescape.model.Member;
import roomescape.model.Waiting;
import roomescape.service.WaitingReadService;
import roomescape.service.WaitingWriteService;

import java.net.URI;
import java.util.List;

@RestController
public class WaitingController {

    private final WaitingReadService waitingReadService;
    private final WaitingWriteService waitingWriteService;

    public WaitingController(WaitingReadService waitingReadService,
                             WaitingWriteService waitingWriteService) {
        this.waitingReadService = waitingReadService;
        this.waitingWriteService = waitingWriteService;
    }

    @PostMapping("/waiting")
    public ResponseEntity<WaitingResponse> createWaiting(@RequestBody WaitingRequest request,
                                                         @AuthenticationPrincipal Member member) {
        Waiting waiting = waitingWriteService.addWaiting(request, member);
        WaitingResponse response = new WaitingResponse(waiting);
        return ResponseEntity.created(URI.create("/waiting/" + waiting.getId())).body(response);
    }

    @DeleteMapping("/waiting/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") long id) {
        waitingWriteService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponse>> getWaiting() {
        List<Waiting> waiting = waitingReadService.findAllWaiting();
        List<WaitingResponse> waitingResponses = waiting.stream()
                .map(WaitingResponse::new)
                .toList();
        return ResponseEntity.ok(waitingResponses);
    }
}
