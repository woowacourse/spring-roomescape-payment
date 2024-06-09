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

import roomescape.controller.dto.request.CreateReservationRequest;
import roomescape.controller.dto.response.CreateReservationResponse;
import roomescape.controller.dto.response.FindReservationResponse;
import roomescape.controller.dto.response.FindReservationStandbyResponse;
import roomescape.controller.dto.request.SearchReservationFilterRequest;
import roomescape.service.AdminReservationService;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    private final AdminReservationService adminReservationService;

    public AdminReservationController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }

    @GetMapping
    public ResponseEntity<List<FindReservationResponse>> findAll() {
        List<FindReservationResponse> response = adminReservationService.findAllReserved();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FindReservationResponse>> find(SearchReservationFilterRequest request) {
        List<FindReservationResponse> response = adminReservationService.findAllByFilter(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/standby")
    public ResponseEntity<List<FindReservationStandbyResponse>> findAllStandby() {
        List<FindReservationStandbyResponse> response = adminReservationService.findAllStandby();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateReservationResponse> save(@Valid @RequestBody CreateReservationRequest request) {
        CreateReservationResponse response = adminReservationService.reserve(request);

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
