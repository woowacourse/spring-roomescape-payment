package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.member.entity.RoleType;
import roomescape.reservation.dto.request.ReservationAdminCreateRequest;
import roomescape.reservation.dto.request.ReservationFindFilteredRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")
@Tag(name = "어드민 예약", description = "어드민 예약 관련 API")
public class AdminReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "예약 생성 (어드민 권한)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    @PostMapping
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<ReservationResponse> createReservationByAdmin(
            @RequestBody @Valid ReservationAdminCreateRequest request
    ) {
        ReservationResponse response = reservationService.createReservationByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "모든 예약 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @GetMapping
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> responses = reservationService.getAllReservations();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "필터링된 예약 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
    })
    @GetMapping("/filtered")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<List<ReservationResponse>> getFilteredReservationsByAdmin(
            @ModelAttribute @Valid ReservationFindFilteredRequest request
    ) {
        List<ReservationResponse> responses = reservationService.getFilteredReservations(request);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    @DeleteMapping("/{id}")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> deleteReservationByAdmin(
            @PathVariable("id") long id
    ) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
