package roomescape.web.controller.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationAndWaitingService;
import roomescape.service.ReservationService;
import roomescape.service.request.AdminSearchedReservationDto;
import roomescape.service.request.ReservationSaveDto;
import roomescape.service.response.ReservationDto;
import roomescape.web.controller.request.AdminReservationRequest;
import roomescape.web.controller.request.SearchCondition;
import roomescape.web.controller.response.AdminReservationResponse;
import roomescape.web.controller.response.MemberReservationResponse;

@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;
    private final ReservationAndWaitingService reservationAndWaitingService;

    public AdminReservationController(ReservationService reservationService,
                                      ReservationAndWaitingService reservationAndWaitingService) {
        this.reservationService = reservationService;
        this.reservationAndWaitingService = reservationAndWaitingService;
    }

    @PostMapping
    public ResponseEntity<AdminReservationResponse> reserve(
            @Valid @RequestBody AdminReservationRequest request) {
        ReservationSaveDto appRequest = ReservationSaveDto.from(request);

        ReservationDto appResponse = reservationService.save(appRequest);
        AdminReservationResponse adminReservationResponse = AdminReservationResponse.from(appRequest);

        return ResponseEntity.created(URI.create("/reservations/" + appResponse.id()))
                .body(adminReservationResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MemberReservationResponse>> getSearchedReservations(SearchCondition searchCondition) {
        AdminSearchedReservationDto appRequest = AdminSearchedReservationDto.from(searchCondition);

        List<ReservationDto> appResponses = reservationService.findAllSearched(appRequest);

        List<MemberReservationResponse> webResponse = appResponses.stream()
                .map(MemberReservationResponse::from)
                .toList();

        return ResponseEntity.ok().body(webResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBy(@PathVariable Long id) {
        reservationAndWaitingService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }
}
