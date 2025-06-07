package roomescape.reservationtime.presentation;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.config.SwaggerConfig;
import roomescape.common.exception.handler.ErrorResponse;
import roomescape.common.security.annotation.RequireRole;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.presentation.dto.response.ConfirmedReservationWebResponse;
import roomescape.reservationtime.application.ReservationTimeApplicationService;
import roomescape.reservationtime.presentation.dto.request.ReservationTimeCreateWebRequest;
import roomescape.reservationtime.presentation.dto.response.AvailableReservationTimeWebResponse;
import roomescape.reservationtime.presentation.dto.response.ReservationTimeWebResponse;

@RestController
@Tag(name = "예약 시간", description = "예약 시간 관련 API")
public class ReservationTimeController {

    private final ReservationTimeApplicationService reservationTimeApplicationService;

    public ReservationTimeController(final ReservationTimeApplicationService reservationTimeApplicationService) {
        this.reservationTimeApplicationService = reservationTimeApplicationService;
    }

    @Operation(summary = "예약 시간 추가",
            description = "예약 시간을 추가합니다.",
            responses = {
                    @ApiResponse(description = "예약 시간 추가 성공", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationTimeWebResponse.class))),
                    @ApiResponse(description = "중복된 시간이 이미 존재하는 경우", responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @PostMapping("/admin/times")
    public ResponseEntity<ReservationTimeWebResponse> create(
            @RequestBody ReservationTimeCreateWebRequest request
    ) {
        ReservationTimeWebResponse dto = reservationTimeApplicationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "모든 예약 시간 조회",
            description = "등록된 모든 예약 시간을 조회합니다.",
            responses = {
                    @ApiResponse(description = "조회 성공", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservationTimeWebResponse.class))))
            }
    )
    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeWebResponse>> findAll() {
        return ResponseEntity.ok(reservationTimeApplicationService.findAll());
    }

    @Operation(summary = "모든 예약 시간 조회 with 예약 가능 여부",
            description = "등록된 모든 예약 시간을 현재 예약 가능한지 여부와 함께 조회합니다.",
            responses = {
                    @ApiResponse(description = "조회 성공", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AvailableReservationTimeWebResponse.class))))
            }
    )
    @GetMapping("/times/available")
    public ResponseEntity<List<AvailableReservationTimeWebResponse>> findAvailable(
            @RequestParam("date") LocalDate date,
            @RequestParam("themeId") Long themeId
    ) {
        return ResponseEntity.ok(reservationTimeApplicationService.findAvailable(date, themeId));
    }

    @Operation(summary = "예약 시간 삭제",
            description = "등록되어 있는 예약 시간 하나를 삭제합니다.",
            responses = {
                    @ApiResponse(description = "삭제 성공", responseCode = "204")
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @DeleteMapping("/admin/times/{id}")
    public ResponseEntity<Void> remove(
            @PathVariable("id") Long id
    ) {
        reservationTimeApplicationService.removeById(id);
        return ResponseEntity.noContent().build();
    }
}
