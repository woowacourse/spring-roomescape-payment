package roomescape.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.api.AdminRestControllerInterface;
import roomescape.domain.admin.dto.AdminReservationRequest;
import roomescape.domain.admin.dto.AdminReservationResponse;
import roomescape.domain.admin.dto.ReservationSearchRequest;
import roomescape.domain.admin.dto.ReservationWaitingResponse;
import roomescape.domain.admin.service.AdminServiceFacade;

@RequiredArgsConstructor
@RestController
public class AdminRestController implements AdminRestControllerInterface {

    private final AdminServiceFacade adminService;

    @Override
    public ResponseEntity<AdminReservationResponse> createReservation(
            @RequestBody final AdminReservationRequest adminReservationRequest
    ) {
        final AdminReservationResponse adminReservationResponse = adminService.saveByAdmin(adminReservationRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(adminReservationResponse);
    }

    @Override
    public ResponseEntity<Void> deleteReservation(@PathVariable final Long id) {
        adminService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<AdminReservationResponse>> getReservationsBySearch(
            @ModelAttribute final ReservationSearchRequest searchRequest
    ) {
        final List<AdminReservationResponse> searchedResponses = adminService.findByInFromTo(searchRequest);

        return ResponseEntity.ok(searchedResponses);
    }

    @Override
    public ResponseEntity<List<ReservationWaitingResponse>> waitingManagement(
    ) {
        final List<ReservationWaitingResponse> waitingResponses = adminService.findAllWaitingReservations();

        return ResponseEntity.ok(waitingResponses);
    }

    @Override
    public ResponseEntity<Void> deleteWaiting(
            @PathVariable final Long id
    ) {
        adminService.deleteWaitingById(id);

        return ResponseEntity.noContent().build();
    }
}
