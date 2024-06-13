package roomescape.reservation.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.auth.Authenticated;
import roomescape.auth.dto.Accessor;
import roomescape.reservation.dto.MemberMyReservationResponse;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationStatusResponse;
import roomescape.reservation.dto.MemberReservationWithPaymentAddRequest;
import roomescape.reservation.dto.ReservationResponse;

@Tag(name = "예약", description = "예약 API")
public interface ReservationControllerDocs {

    @Operation(summary = "예약 목록 조회", description = "예약 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))))
    public ResponseEntity<List<ReservationResponse>> getReservationList();

    @Operation(summary = "예약 조회", description = "예약을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 조회 성공",
                    content = @Content(schema = @Schema(implementation = MemberReservationStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "해당하는 예약이 존재하지 않을 경우")
    })
    public ResponseEntity<MemberReservationStatusResponse> getReservationById(Long id);

    @Operation(summary = "회원, 테마, 기간별 예약 조회", description = "회원, 테마, 기간별 예약을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원, 테마, 기간별 예약 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ReservationResponse.class))))
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            Long memberId, Long themeId, LocalDate dateFrom, LocalDate dateTo);

    @Operation(summary = "내 예약 조회", description = "로그인한 회원의 예약을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 예약 조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MemberMyReservationResponse.class))))
    public ResponseEntity<List<MemberMyReservationResponse>> findMemberReservationStatus(Accessor accessor);

    @Operation(summary = "예약 추가", description = "예약을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 추가 성공",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = """
                    1. 예약 중복인 경우
                    2. 지나간 시간인 경우
                    3. 존재하지 않는 회원, 테마, 시간인 경우
                    """)
    })
    @Parameters({
            @Parameter(name = "date", description = "예약 날짜 필수", required = true),
            @Parameter(name = "timeId", description = "예약 시간 선택 필수", required = true),
            @Parameter(name = "themeId", description = "테마 선택 필수", required = true),
            @Parameter(name = "paymentKey", description = "결제키 필수", required = true),
            @Parameter(name = "orderId", description = "주문 ID 필수", required = true),
            @Parameter(name = "amount", description = "결제 금액 필수", required = true)
    })
    public ResponseEntity<ReservationResponse> saveMemberReservation(
            @Authenticated Accessor accessor,
            @RequestBody MemberReservationWithPaymentAddRequest memberReservationWithPaymentAddRequest);

    @Operation(summary = "예약 대기 추가", description = "예약 대기를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "대기 예약 추가 성공",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = """
                    1. 예약 중복인 경우
                    2. 지나간 시간인 경우
                    3. 존재하지 않는 회원, 테마, 시간인 경우
                    4. 결제 승인 시 문제가 생긴 경우
                    """),
            @ApiResponse(responseCode = "500", description = "결제 승인 시 문제가 생긴 경우")
    })
    @Parameters({
            @Parameter(name = "date", description = "예약 날짜 필수", required = true),
            @Parameter(name = "timeId", description = "예약 시간 선택 필수", required = true),
            @Parameter(name = "themeId", description = "테마 선택 필수", required = true)
    })
    public ResponseEntity<ReservationResponse> saveMemberWaitingReservation(
            @Authenticated Accessor accessor,
            @RequestBody MemberReservationAddRequest memberReservationAddRequest);

    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "해당하는 예약이 존재하지 않을 경우")
    })
    public ResponseEntity<Void> removeReservation(Long id);
}
