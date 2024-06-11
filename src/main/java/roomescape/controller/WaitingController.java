package roomescape.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.request.WaitingRequest;
import roomescape.annotation.AuthenticationPrincipal;
import roomescape.model.Member;
import roomescape.model.Waiting;
import roomescape.response.WaitingResponse;
import roomescape.service.WaitingService;

import java.net.URI;
import java.util.List;

@RestController
public class WaitingController {

    private final WaitingService waitingService;

    public WaitingController(WaitingService waitingService) {
        this.waitingService = waitingService;
    }

    @PostMapping("/waiting")
    public ResponseEntity<WaitingResponse> createWaiting(@RequestBody WaitingRequest request,
                                                         @AuthenticationPrincipal Member member) {
        Waiting waiting = waitingService.addWaiting(request, member);
        WaitingResponse response = new WaitingResponse(waiting);
        return ResponseEntity.created(URI.create("/waiting/" + waiting.getId())).body(response);
    }

    @DeleteMapping("/waiting/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") long id) {
        waitingService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/waitings")
    public ResponseEntity<List<WaitingResponse>> getWaiting() {
        List<Waiting> waiting = waitingService.findAllWaiting();
        List<WaitingResponse> waitingResponses = waiting.stream()
                .map(WaitingResponse::new)
                .toList();
        return ResponseEntity.ok(waitingResponses);
    }
}
