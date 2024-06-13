package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.core.AuthenticationPrincipal;
import roomescape.auth.domain.AuthInfo;
import roomescape.reservation.dto.request.CreateMyReservationRequest;
import roomescape.reservation.dto.response.CreateReservationResponse;
import roomescape.reservation.dto.response.FindAvailableTimesResponse;
import roomescape.reservation.dto.response.FindReservationResponse;
import roomescape.reservation.dto.response.FindReservationWithPaymentResponse;
import roomescape.reservation.service.ReservationService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "예약 API", description = "예약 관련 API")
@RestController
@RequestMapping
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "예약 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "400", description = """
                    1. 예약 날짜는 현재보다 과거일 수 없습니다.
                    2. 예약 등록 시 예약 날짜는 필수입니다.
                    3. 예약하고자 하는 회원 식별자는 양수만 가능합니다.
                    4. 예약 등록 시 시간은 필수입니다.
                    5. 예약 등록 시 테마 식별자는 양수만 가능합니다.
                    6. 예약 등록 시 테마는 필수입니다.
                    7. 결제 키를 입력해주세요.
                    8. 주문을 입력해주세요.
                    9. 결제 금액을 입력해주세요.
                    10. 이미 해당 날짜의 선택한 테마의 시간에 예약이 존재하여 예약을 생성할 수 없습니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = """
                    1. 식별자에 해당하는 시간이 존재하지 않습니다.
                    2. 식별자에 해당하는 테마가 존재하지 않습니다.
                    3. 식별자에 해당하는 회원이 존재하지 않습니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "결제 api 실패",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping("/reservations")
    public ResponseEntity<CreateReservationResponse> createReservation(
            @AuthenticationPrincipal AuthInfo authInfo,
            @Valid @RequestBody CreateMyReservationRequest createReservationRequest) {
        CreateReservationResponse createReservationResponse =
                reservationService.createMyReservationWithPayment(authInfo, createReservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + createReservationResponse.id()))
                .body(createReservationResponse);
    }

    @Operation(summary = "예약 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 예약 조회 성공"),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 예약이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/reservations/{id}")
    public ResponseEntity<FindReservationResponse> getReservation(@PathVariable final Long id) {
        return ResponseEntity.ok(reservationService.getReservation(id));
    }

    @Operation(summary = "예약 시간 조회 API")
    @ApiResponse(responseCode = "200", description = "예약 시간 조회 성공")
    @GetMapping("/reservations/times")
    public ResponseEntity<List<FindAvailableTimesResponse>> getAvailableTimes(@RequestParam LocalDate date,
                                                                              @RequestParam Long themeId) {
        return ResponseEntity.ok(reservationService.getAvailableTimes(date, themeId));
    }

    @Operation(summary = "예약 검색 API")
    @ApiResponse(responseCode = "200", description = "예약 검색 성공")
    @GetMapping("/reservations/search")
    public ResponseEntity<List<FindReservationResponse>> searchBy(@RequestParam(required = false) Long themeId,
                                                                  @RequestParam(required = false) Long memberId,
                                                                  @RequestParam(required = false) LocalDate dateFrom,
                                                                  @RequestParam(required = false) LocalDate dateTo) {
        return ResponseEntity.ok(reservationService.searchBy(themeId, memberId, dateFrom, dateTo));
    }

    @Operation(summary = "예약 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없는 식별자를 가진 회원",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "식별자에 해당하는 예약이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))})
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal AuthInfo authInfo,
                                                  @PathVariable Long id) {
        reservationService.deleteReservation(authInfo, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 예약 목록 조회 API")
    @ApiResponse(responseCode = "200", description = "사용자 예약 목록 조회 성공")
    @GetMapping("/members/reservations")
    public ResponseEntity<List<FindReservationWithPaymentResponse>> getReservations(
            @AuthenticationPrincipal AuthInfo authInfo) {
        return ResponseEntity.ok(reservationService.getReservations(authInfo));
    }
}
