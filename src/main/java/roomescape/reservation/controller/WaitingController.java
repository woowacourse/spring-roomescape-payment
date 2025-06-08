package roomescape.reservation.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.controller.api.WaitingApi;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.WaitingCreateRequest;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.service.WaitingService;

@RequiredArgsConstructor
@RestController
public class WaitingController implements WaitingApi {

    private final WaitingService waitingService;

    @Override
    public ResponseEntity<WaitingResponse> create(
            @Valid @RequestBody final ReservationRequest request,
            final LoginMember loginMember
    ) {
        WaitingCreateRequest createRequest = WaitingCreateRequest.from(request, loginMember);
        WaitingResponse response = waitingService.createWaiting(createRequest);

        return ResponseEntity.created(URI.create("/waitings/" + response.id()))
                .body(response);
    }

    @Override
    public ResponseEntity<Void> delete(final Long waitingId) {
        waitingService.deleteWaiting(waitingId);

        return ResponseEntity.noContent()
                .build();
    }
}
