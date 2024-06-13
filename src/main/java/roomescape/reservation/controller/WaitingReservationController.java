package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.common.dto.ResourcesResponse;
import roomescape.reservation.controller.dto.request.WaitingReservationSaveRequest;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.controller.dto.response.WaitingResponse;
import roomescape.reservation.service.component.WaitingComponentService;
import roomescape.reservation.service.dto.request.WaitingReservationRequest;

@RestController
public class WaitingReservationController {

    private final WaitingComponentService waitingComponentService;

    public WaitingReservationController(WaitingComponentService waitingComponentService) {
        this.waitingComponentService = waitingComponentService;
    }

    @GetMapping("/reservations/wait")
    public ResponseEntity<ResourcesResponse<WaitingResponse>> findWaitings() {
        List<WaitingResponse> reservations = waitingComponentService.findWaitings();
        ResourcesResponse<WaitingResponse> response = new ResourcesResponse<>(reservations);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservations/wait")
    public ResponseEntity<ReservationResponse> save(
            @Valid @RequestBody WaitingReservationSaveRequest saveRequest,
            LoginMember loginMember
    ) {
        WaitingReservationRequest request = WaitingReservationRequest.of(saveRequest, loginMember.id());
        ReservationResponse response = waitingComponentService.save(request);

        return ResponseEntity.created(URI.create("/reservations/wait/" + response.id()))
                .body(response);
    }

    @PatchMapping("/reservations/wait/{id}")
    public ResponseEntity<Void> approveReservation(@PathVariable("id") Long id) {
        waitingComponentService.approveReservation(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reservations/wait/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        waitingComponentService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
