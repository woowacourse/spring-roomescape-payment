package roomescape.controller.api;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import roomescape.controller.api.docs.AdminReservationApiDocs;
import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.request.SearchReservationFilterRequest;
import roomescape.controller.dto.response.ReservationResponse;
import roomescape.service.AdminReservationService;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController implements AdminReservationApiDocs {
    private final AdminReservationService adminReservationService;

    public AdminReservationController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        List<ReservationResponse> response = adminReservationService.findAllReserved();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponse>> find(SearchReservationFilterRequest request) {
        List<ReservationResponse> response = adminReservationService.findAllByFilter(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/standby")
    public ResponseEntity<List<ReservationResponse>> findAllStandby() {
        List<ReservationResponse> response = adminReservationService.findAllStandby();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> save(@Valid @RequestBody CreateReservationRequest request) {
        ReservationResponse response = adminReservationService.reserve(request);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminReservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/standby/{id}")
    public ResponseEntity<Void> deleteStandby(@PathVariable Long id) {
        adminReservationService.deleteStandby(id);
        return ResponseEntity.noContent().build();
    }
}
