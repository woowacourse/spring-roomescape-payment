package roomescape.reservation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import roomescape.reservation.controller.api.AdminWaitingApi;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.service.WaitingService;

@RequiredArgsConstructor
@RestController
public class AdminWaitingController implements AdminWaitingApi {

    private final WaitingService waitingService;

    @Override
    public ResponseEntity<List<WaitingResponse>> readAllWaiting() {
        List<WaitingResponse> responses = waitingService.getAll();

        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<Void> deny(final Long waitingId) {
        waitingService.deleteWaiting(waitingId);

        return ResponseEntity.noContent()
                .build();
    }
}
