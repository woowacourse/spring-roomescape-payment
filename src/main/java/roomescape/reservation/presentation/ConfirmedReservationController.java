package roomescape.reservation.presentation;

import java.time.LocalDate;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import roomescape.common.security.dto.request.MemberInfo;
import roomescape.member.domain.MemberRole;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.reservation.application.ConfirmedReservationApplicationService;
import roomescape.reservation.application.dto.request.ConfirmedReservationByCriteriaWebRequest;
import roomescape.reservation.application.dto.request.ConfirmedReservationCreateRequest;
import roomescape.reservation.presentation.dto.request.AdminReservationSlotCreateWebRequest;
import roomescape.reservation.presentation.dto.request.ConfirmedReservationCreateWebRequest;
import roomescape.reservation.presentation.dto.response.ConfirmedReservationWebResponse;
import roomescape.reservationslot.presentation.dto.response.MyReservationResponse;

@RestController
@Tag(name = "예약(확정)", description = "확정된 예약 관련 API")
public class ConfirmedReservationController {

    private final ConfirmedReservationApplicationService confirmedReservationApplicationService;

    public ConfirmedReservationController(
            final ConfirmedReservationApplicationService confirmedReservationApplicationService) {
        this.confirmedReservationApplicationService = confirmedReservationApplicationService;
    }

    @Operation(summary = "예약 필터링 조회",
            description = "테마, 예약한 멤버, 기간으로 필터링해서 예약을 조회합니다.",
            responses = {
                @ApiResponse(description = "조회 성공", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConfirmedReservationWebResponse.class))))
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @GetMapping("/admin/reservations")
    public ResponseEntity<List<ConfirmedReservationWebResponse>> findByCriteria(
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo
    ) {
        List<ConfirmedReservationWebResponse> reservations = confirmedReservationApplicationService.findByCriteria(
                new ConfirmedReservationByCriteriaWebRequest(themeId, memberId, dateFrom, dateTo));
        return ResponseEntity.ok(reservations);
    }

    @Operation(summary = "예약 삭제",
            description = "예약을 삭제합니다. 해당 시간대에 가장 첫번째로 대기 중인 예약이 있다면 '결제 대기' 상태로 변경됩니다.",
            responses = {
                    @ApiResponse(description = "예약 삭제 성공", responseCode = "204")
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @DeleteMapping("/admin/reservations/{reservationId}")
    public ResponseEntity<Void> cancel(
            @PathVariable("reservationId") Long reservationId
    ) {
        confirmedReservationApplicationService.cancel(reservationId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "결제 승인 후 확정 예약 생성",
            description = "결제 승인 요청을 보낸 후 확정 예약을 생성합니다.",
            responses = {
                    @ApiResponse(description = "예약 생성 성공", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfirmedReservationWebResponse.class))),
                    @ApiResponse(description = "- 과거 시간에 예약할 경우\n\n- 이미 예약 중인 멤버인 경우", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(description = "존재하지 않는 멤버 id인 경우", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(description = "날짜, 시간, 테마가 모두 중복되는 예약이 존재할 경우", responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.REGULAR)
    @PostMapping("/reservations")
    public ResponseEntity<ConfirmedReservationWebResponse> createWithPayment(
            @RequestBody ConfirmedReservationCreateWebRequest request,
            @Parameter(hidden = true) MemberInfo memberInfo
    ) {
        ConfirmedReservationWebResponse response = confirmedReservationApplicationService.createWithPayment(
                ConfirmedReservationCreateRequest.of(request, memberInfo),
                PaymentApproveRequest.from(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "결제 승인 없이 확정 예약 생성",
            description = "확정 예약을 생성합니다.",
            responses = {
                    @ApiResponse(description = "예약 생성 성공", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConfirmedReservationWebResponse.class))),
                    @ApiResponse(description = "- 과거 시간에 예약할 경우\n\n- 이미 예약 중인 멤버인 경우", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(description = "존재하지 않는 멤버 id인 경우", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(description = "날짜, 시간, 테마가 모두 중복되는 예약이 존재할 경우", responseCode = "409", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @PostMapping("/admin/reservations")
    public ResponseEntity<ConfirmedReservationWebResponse> create(
            @RequestBody AdminReservationSlotCreateWebRequest request
    ) {
        ConfirmedReservationWebResponse dto = confirmedReservationApplicationService.create(
                ConfirmedReservationCreateRequest.of(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Operation(summary = "내 예약 조회",
            description = "현재 로그인된 멤버의 확정 예약 및 대기 예약을 모두 조회합니다.",
            responses = {
                    @ApiResponse(description = "조회 성공", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MyReservationResponse.class)))),
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.REGULAR)
    @GetMapping("/reservations-mine")
    public ResponseEntity<List<MyReservationResponse>> findMine(@Parameter(hidden = true) MemberInfo memberInfo) {
        List<MyReservationResponse> myReservations = confirmedReservationApplicationService.findMyReservations(
                memberInfo.id());
        return ResponseEntity.ok().body(myReservations);
    }
}
