package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.dto.SessionMember;
import roomescape.reservation.controller.dto.CreateReservationRequest;
import roomescape.reservation.controller.dto.MyReservationResponse;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.repository.dto.MyReservationWithTossPayment;


@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "사용자 예약 생성 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "사용자 예약 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 사용자입니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "현재 시간 이후로만 예약할 수 있습니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "이미 해당 일정에 예약이 존재합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "(외부 api 지정 예외 상태 코드 전송)",
                    description = "(외부 api 지정 예외 메시지 전송)",
                    content = @Content(schema = @Schema(hidden = true))
            ),
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody @Valid final CreateReservationRequest request,
            final SessionMember sessionMember
    ) {
        ReservationResponse response = reservationService.createReservationWithPayment(request, sessionMember.id());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(summary = "사용자 예약 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "사용자 예약 삭제 성공"
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") final Long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 예약 조회 API", description = "내 예약 및 예약 대기 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 예약 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인이 필요합니다.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    })
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationWithTossPayment>> findMyReservation(
            final SessionMember sessionMember
    ) {
        final List<MyReservationWithTossPayment> response = reservationService.findAllMyReservation(sessionMember.id());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 필터링 조회 API", description = "예약 목록을 특정 조건으로 필터링하여 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 조회 성공"
            )
    })
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservationsWithFilter(
            @RequestParam(required = false) final Long memberId,
            @RequestParam(required = false) final Long themeId,
            @RequestParam(required = false) final LocalDate fromDate,
            @RequestParam(required = false) final LocalDate toDate
    ) {
        final List<ReservationResponse> responses = reservationService.findAllReservationsWithFilter(
                memberId,
                themeId,
                fromDate,
                toDate
        );
        return ResponseEntity.ok(responses);
    }
}
