package roomescape.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.service.request.AdminSearchedReservationAppRequest;
import roomescape.service.request.ReservationSaveAppRequest;
import roomescape.service.response.ReservationAppResponse;
import roomescape.web.controller.request.AdminReservationRequest;
import roomescape.web.controller.request.SearchCondition;
import roomescape.web.controller.response.AdminReservationResponse;
import roomescape.web.controller.response.MemberReservationResponse;

@Tag(name = "Admin-Reservation", description = "운영자 예약 API")
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

    @Operation(summary = "예약 생성", description = "예약을 생성합니다.")
    @PostMapping
    public ResponseEntity<AdminReservationResponse> reserve(
            @Valid @RequestBody AdminReservationRequest adminReservationRequest) {
        ReservationSaveAppRequest appRequest = ReservationSaveAppRequest.from(adminReservationRequest);

        ReservationAppResponse appResponse = reservationService.save(appRequest);
        AdminReservationResponse adminReservationResponse = AdminReservationResponse.from(appRequest);

        return ResponseEntity.created(URI.create("/reservations/" + appResponse.id()))
                .body(adminReservationResponse);
    }

    @Operation(summary = "조건으로 예약 조회", description = "회원id, 테마id, 시작일, 종료일로 예약을 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<MemberReservationResponse>> getSearchedReservations(SearchCondition searchCondition) {
        AdminSearchedReservationAppRequest appRequest = AdminSearchedReservationAppRequest.from(searchCondition);

        List<ReservationAppResponse> appResponses = reservationService.findAllSearched(appRequest);

        List<MemberReservationResponse> webResponse = appResponses.stream()
                .map(MemberReservationResponse::from)
                .toList();

        return ResponseEntity.ok().body(webResponse);
    }

    @Operation(summary = "예약 삭제", description = "예약 id로 예약을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBy(@PathVariable Long id) {
        reservationAndWaitingService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }
}
