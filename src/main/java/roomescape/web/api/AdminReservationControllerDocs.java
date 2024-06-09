package roomescape.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.application.dto.request.reservation.ReservationRequest;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.domain.member.Member;

@Tag(name = "관리자 예약", description = "관리자 예약 API")
interface AdminReservationControllerDocs {

    @Operation(summary = "예약하기", description = "관리자가 방탈출을 예약한다.")
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
                    content = @Content(examples = @ExampleObject("이미 예약한 방탈출입니다"))
            )
    })
    ResponseEntity<ReservationResponse> makeReservation(@RequestBody @Valid ReservationRequest request);

    @Operation(summary = "전체 예약 조회", description = "전체 예약을 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 예약 조회 성공"
            )
    })
    ResponseEntity<List<ReservationResponse>> findAllReservations();

    @Operation(summary = "예약 검색", description = "예약을 검색한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "예약 검색 성공"
            )
    })
    ResponseEntity<List<ReservationResponse>> searchAllReservations(
            @Parameter(description = "시작 일", example = "2024-04-05") LocalDate start,
            @Parameter(description = "종료 일", example = "2024-04-09") LocalDate end,
            @Parameter(description = "멤버 ID", example = "1") Long memberId,
            @Parameter(description = "테마 ID", example = "1") Long themeId
    );

    @Operation(summary = "전체 예약 대기 조회", description = "전체 예약 대기를 조회한다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 대기 조회 성공"
            )
    })
    ResponseEntity<List<ReservationResponse>> findAllWaitings();

    @Operation(summary = "예약 대기 취소", description = "예약 대기를 취소한다.")
    ResponseEntity<Void> cancelWaiting(
            @Parameter(description = "예약 대기 ID", example = "1") Long waitingId,
            @Parameter(hidden = true) Member member
    );

    @Operation(summary = "예약 취소", description = "예약을 취소한다.")
    ResponseEntity<Void> cancelReservation(
            @Parameter(description = "예약 ID", example = "1") Long reservationId,
            @Parameter(hidden = true) Member member
    );
}
