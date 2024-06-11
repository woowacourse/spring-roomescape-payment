package roomescape.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.application.dto.request.reservation.ReservationPaymentRequest;
import roomescape.application.dto.request.reservation.UserReservationRequest;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.application.dto.response.reservation.UserReservationResponse;

@Tag(name = "사용자 예약", description = "사용자 예약 API")
interface MemberReservationControllerDocs {

    @Operation(summary = "방탈출 예약하기", description = "사용자가 방탈출을 예약한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "예약 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            실패 케이스
                                                        
                            (1) 이미 예약한 방탈출인 경우
                                                        
                            (2) 이미 대기 중인 방탈출인 경우
                            """,
                    content = @Content(examples = @ExampleObject("예약 실패"))
            )
    })
    ResponseEntity<ReservationResponse> makeReservation(
            @Valid UserReservationRequest request,
            @Parameter(hidden = true) MemberInfo member
    );

    @Operation(summary = "결제하기", description = "사용자가 결제 방탈출을 결제한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "결제 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            실패 케이스
                                                        
                            (1) 이미 결제한 방탈출인 경우
                                                        
                            (2) 다른 사람의 방탈출인 경우
                            """,
                    content = @Content(examples = @ExampleObject("결제 실패"))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = """
                            실패 케이스
                                                        
                            (1) 결제 서버에 문제가 발생한 경우
                            """,
                    content = @Content(examples = @ExampleObject("서버 문제로 결제 실패"))
            )
    })
    ResponseEntity<ReservationResponse> paymentForPending(
            @Valid ReservationPaymentRequest request,
            @Parameter(hidden = true) MemberInfo member
    );

    @Operation(summary = "내 예약 목록 조회", description = "사용자가 예약한 방탈출 목록을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내 예약 목록 조회 성공"
            )
    })
    ResponseEntity<List<UserReservationResponse>> findAllMyReservations(@Parameter(hidden = true) MemberInfo member);

    @Operation(summary = "예약 대기 취소하기", description = "사용자가 예약 대기 중인 방탈출을 취소한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "대기 취소 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            실패 케이스
                                                        
                            (1) 존재하지 않는 예약인 경우
                                                        
                            (2) 다른 사람의 예약인 경우
                            """,
                    content = @Content(examples = @ExampleObject("다른 사람의 예약입니다"))
            )
    })
    ResponseEntity<Void> cancelWaiting(
            @Parameter(description = "예약 대기 ID", example = "1") Long waitingId,
            @Parameter(hidden = true) MemberInfo member
    );

    @Operation(summary = "예약 취소하기", description = "사용자가 예약 중인 방탈출을 취소한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "예약 취소 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = """
                            실패 케이스
                                                        
                            (1) 존재하지 않는 예약인 경우
                                                        
                            (2) 다른 사람의 예약인 경우
                            """,
                    content = @Content(examples = @ExampleObject("다른 사람의 예약입니다"))
            )
    })
    ResponseEntity<Void> cancelReservation(
            @Parameter(description = "예약 ID", example = "1") Long reservationId,
            @Parameter(hidden = true) MemberInfo member
    );
}
