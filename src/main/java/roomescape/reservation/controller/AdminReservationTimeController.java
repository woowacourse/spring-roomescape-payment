package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.member.entity.RoleType;
import roomescape.reservation.dto.request.ReservationTimeCreateRequest;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.service.ReservationTimeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/times")
@Tag(name = "어드민 예약 시간", description = "어드민 예약 시간 관련 API")
public class AdminReservationTimeController {

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "예약 시간 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
    })
    @PostMapping
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<ReservationTimeResponse> createTimeByAdmin(
            @RequestBody @Valid ReservationTimeCreateRequest request
    ) {
        ReservationTimeResponse response = reservationTimeService.createTime(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약 시간 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", useReturnTypeSchema = true),
    })
    @DeleteMapping("/{id}")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> deleteTimeByAdmin(
            @PathVariable("id") long id
    ) {
        reservationTimeService.deleteTime(id);
        return ResponseEntity.noContent().build();
    }
}
