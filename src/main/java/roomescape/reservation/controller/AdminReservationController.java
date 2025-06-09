package roomescape.reservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.member.entity.RoleType;
import roomescape.reservation.dto.request.ReservationAdminCreateRequest;
import roomescape.reservation.dto.response.ReservationAdminCreateResponse;
import roomescape.reservation.service.ReservationService;

@Tag(name = "Admin Reservation", description = "ADMIN 권한 예약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/reservations")
public class AdminReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "관리자 예약 생성", description = "ADMIN 권한으로 예약을 생성합니다.")
    @PostMapping
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<ReservationAdminCreateResponse> createReservationByAdmin(
            @RequestBody @Valid ReservationAdminCreateRequest request
    ) {
        ReservationAdminCreateResponse response = reservationService.createReservationByAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
