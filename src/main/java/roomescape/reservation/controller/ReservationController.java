package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.common.exception.dto.ErrorResponse;
import roomescape.payment.domain.PaymentMethod;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.reservation.dto.request.FilteringReservationRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationPaymentRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.BookedReservationTimeResponse;
import roomescape.reservation.dto.response.MyReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@Tag(name = "예약", description = "예약 관련 API")
@Slf4j
@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "예약 생성", description = "결제 내역을 포함하여 예약을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 생성 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(responseCode = "400", description = "이미 예약이 있거나 대기가 존재하거나 유효하지 않은 요청",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 테마, 시간, 회원",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final ReservationPaymentRequest request,
            final LoginMember loginMember
    ) {
        log.info("사용자 예약 생성 요청: memberId={}, date={}, timeId={}, themeId={}, amount={}",
                loginMember.id(), request.date(), request.timeId(), request.themeId(), request.amount());

        ReservationCreateRequest createRequest = ReservationCreateRequest.from(
                new ReservationRequest(
                        request.date(),
                        request.timeId(),
                        request.themeId()
                ), loginMember
        );
        PaymentRequest paymentRequest = new PaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount(),
                PaymentMethod.TOSS
        );
        ReservationResponse response = reservationService.createWithPayment(createRequest, paymentRequest);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @Operation(summary = "전체 예약 조회", description = "모든 예약을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "전체 예약 조회 성공",
            content = @Content(
                    mediaType = "application/json"
            ))
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readAllReservations() {
        List<ReservationResponse> response = reservationService.getAll();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "필터링된 예약 조회", description = "조건을 기반으로 예약을 필터링하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "필터링된 예약 조회 성공",
            content = @Content(
                    mediaType = "application/json"
            ))
    @GetMapping("/filtering")
    public ResponseEntity<List<ReservationResponse>> readFilteredReservations(
            @ModelAttribute @Valid final FilteringReservationRequest request
    ) {
        final List<ReservationResponse> reservationResponses =
                reservationService.getFilteredReservations(request);
        return ResponseEntity.ok(reservationResponses);
    }

    @Operation(summary = "예약 가능한 시간 조회", description = "특정 날짜와 테마에 예약 가능한 시간 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 가능한 시간 조회 성공",
            content = @Content(
                    mediaType = "application/json"
            ))
    @GetMapping("/times/available")
    public ResponseEntity<List<BookedReservationTimeResponse>> readAvailableReservationTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("themeId") final Long themeId
    ) {
        List<BookedReservationTimeResponse> responses = reservationService.getSortedAvailableTimes(date, themeId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "내 예약, 대기 조회", description = "로그인한 사용자의 예약과 대기 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 예약 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json"
            ))
    @GetMapping("/my")
    public ResponseEntity<List<MyReservationsResponse>> readMyReservations(final @Valid LoginMember loginMember) {
        List<MyReservationsResponse> response = reservationService.getAllMyReservations(loginMember);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 삭제", description = "예약 ID를 기반으로 예약을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 삭제 성공",
                    content = @Content(
                            mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 예약 ID",
                    content = @Content(
                            mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> delete(@PathVariable("reservationId") final Long reservationId) {
        log.info("예약 삭제 요청: reservationId={}", reservationId);
        reservationService.delete(reservationId);
        return ResponseEntity.noContent().build();
    }
}
