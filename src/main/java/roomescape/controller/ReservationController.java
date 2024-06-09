package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.AdminReservationRequest;
import roomescape.dto.ErrorResponse;
import roomescape.dto.LoginMemberRequest;
import roomescape.dto.PaymentErrorResponse;
import roomescape.dto.PaymentRequest;
import roomescape.dto.ReservationDetailResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.dto.ReservationWithPaymentRequest;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;

@Tag(name = "예약 API", description = "예약과 관련된 요청을 처리한다.")
@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationController(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @Operation(description = "로그인 한 유저가 예약을 생성할 수 있는 API",
            //todo 파라미터 정보 추가
            responses = {
                    @ApiResponse(responseCode = "201", description = "예약 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationResponse.class)
                            ),
                            headers = {
                                    @Header(name = "Location", required = true, schema = @Schema(example = "Location:/reservations/1"))}),
                    @ApiResponse(responseCode = "401", description = "로그인이 안 된 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"로그인이 필요합니다.\"}")
                            ))}
    )
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Authenticated LoginMemberRequest loginMemberRequest,
            //Todo 다시 ReservationRequest 로 변경하기
            @RequestBody ReservationWithPaymentRequest reservationWithPaymentRequest
    ) {
        ReservationResponse savedReservationResponse = reservationService.saveByUser(loginMemberRequest,
                ReservationRequest.from(reservationWithPaymentRequest));
        return ResponseEntity.created(URI.create("/reservations/" + savedReservationResponse.id()))
                .body(savedReservationResponse);
    }

    @Operation(description = "예약 결제 API",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(schema = @Schema(implementation = PaymentRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 성공"),
                    @ApiResponse(responseCode = "4XX/5XX", description = "결제 실패",
                            content = @Content(schema = @Schema(implementation = PaymentErrorResponse.class)))
            }
    )
    @PostMapping("/payment/{id}")
    public void payReservation(
            @PathVariable long id,
            @Authenticated LoginMemberRequest loginMemberRequest,
            @RequestBody PaymentRequest paymentRequest
    ) {
        reservationService.validateReservationsAuthority(id, loginMemberRequest);
        paymentService.pay(id, paymentRequest);
    }

    @Operation(description = "관리자 권한으로 예약을 추가할 수 있는 API",
            //todo 파라미터 정보 추가
            responses = {
                    @ApiResponse(responseCode = "201", description = "예약 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationResponse.class)
                            ),
                            headers = {
                                    @Header(name = "Location", required = true, schema = @Schema(example = "Location:/reservations/1"))}),
                    @ApiResponse(responseCode = "401", description = "로그인이 안 된 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"로그인이 필요합니다.\"}")
                            )),
                    @ApiResponse(responseCode = "403", description = "관리자가 아닐 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"접근 권한이 없습니다.\"}")
                            ))}
    )
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(
            @RequestBody AdminReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.saveByAdmin(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(description = "모든 예약 정보를 조회하는 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "예약 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationResponse.class)
                            ))}
    )
    @GetMapping("/reservations")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @Operation(description = "관리자가 모든 예약 대기를 확인할 수 있는 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "예약 대기 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationResponse.class)
                            )),
                    @ApiResponse(responseCode = "401", description = "로그인이 안 된 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"로그인이 필요합니다.\"}")
                            )),
                    @ApiResponse(responseCode = "403", description = "관리자가 아닐 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"접근 권한이 없습니다.\"}")
                            ))}
    )
    @GetMapping("/reservations/waiting")
    @AdminOnly
    public List<ReservationResponse> findAllRemainedWaiting() {
        return reservationService.findAllRemainedWaiting();
    }

    @Operation(description = "본인의 모든 예약 내역을 확인할 수 있는 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "예약 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReservationResponse.class)
                            )),
                    @ApiResponse(responseCode = "401", description = "로그인이 안 된 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"로그인이 필요합니다.\"}")
                            ))}
    )
    @GetMapping("/reservations/mine")
    public List<ReservationDetailResponse> findMemberReservations(
            @Authenticated LoginMemberRequest loginMemberRequest) {
        return reservationService.findAllByMemberId(loginMemberRequest.id());
    }

    @Operation(description = "예약 조건부 검색 API, 조회 기간을 입력하지 않으면 오늘 날짜를 기준으로 조회한다.",
            parameters = {
                    @Parameter(name = "themeId", description = "테마 id", example = "1", schema = @Schema(type = "long")),
                    @Parameter(name = "memberId", description = "예약자 id", example = "1", schema = @Schema(type = "long")),
                    @Parameter(name = "dateFrom", description = "조회 기간 시작일", example = "2024-09-08", schema = @Schema(type = "LocalDate")),
                    @Parameter(name = "dateTo", description = "조회 기간 종료일", example = "2024-09-20", schema = @Schema(type = "LocalDate"))
            }, responses = {
            @ApiResponse(responseCode = "200", description = "검색 결과 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReservationResponse.class)))
    })
    @GetMapping("/reservations/search")
    public List<ReservationResponse> searchReservation(@RequestParam(required = false) Long themeId,
                                                       @RequestParam(required = false) Long memberId,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateFrom,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateTo) {
        return reservationService.searchReservation(themeId, memberId, dateFrom, dateTo);
    }

    @Operation(description = "본인의 예약을 삭제할 수 있는 API",
            responses = {
                    @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "로그인이 안 된 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"로그인이 필요합니다.\"}")
                            )),
                    @ApiResponse(responseCode = "403", description = "다른 사람의 예약을 삭제 시도할 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"접근 권한이 없습니다.\"}")
                            ))}
    )
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@Authenticated LoginMemberRequest loginMemberRequest,
                                       @PathVariable long id) {
        reservationService.deleteByUser(loginMemberRequest, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "관리자 권한으로 예약을 삭제할 수 있는 API",
            responses = {
                    @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "로그인이 안 된 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"로그인이 필요합니다.\"}")
                            )),
                    @ApiResponse(responseCode = "403", description = "관리자가 아닐 경우",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class, example = "{\"message\":\"접근 권한이 없습니다.\"}")
                            ))}
    )
    @DeleteMapping("/admin/reservations/{id}")
    @AdminOnly
    public ResponseEntity<Void> deleteByAdmin(@PathVariable long id) {
        reservationService.deleteWaitingByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
