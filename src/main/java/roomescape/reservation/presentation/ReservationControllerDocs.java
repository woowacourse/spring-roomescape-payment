package roomescape.reservation.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.common.argumentResolver.Login;
import roomescape.member.dto.request.LoginMember;
import roomescape.reservation.dto.request.ReservationConditionRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationAndWaitingResponse;
import roomescape.reservation.dto.response.ReservationResponse;

import java.util.List;

@Tag(name = "예약", description = "예약 관련 API")
public interface ReservationControllerDocs {

    @Operation(summary = "예약 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<ReservationResponse>> getReservations(
            @ModelAttribute final ReservationConditionRequest request);

    @Operation(summary = "예약 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "이미 예약된 시간",
                            value = "{\"message\": \"이미 예약된 시간입니다.\"}"
                    ),
                    @ExampleObject(
                            name = "결제 정보 누락",
                            value = "{\"message\": \"결제 정보가 필요합니다.\"}"
                    )
            })),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping
    ResponseEntity<ReservationResponse> createReservation(
            @RequestBody final ReservationRequest request,
            @Login final LoginMember loginMember
    );

    @Operation(summary = "예약 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(examples = {
                    @ExampleObject(
                            name = "이미 취소된 예약",
                            value = "{\"message\": \"이미 취소된 예약입니다.\"}"
                    )
            })),
            @ApiResponse(responseCode = "404", description = "예약을 찾을 수 없음", content = @Content(examples = {
                    @ExampleObject(
                            name = "존재하지 않는 예약",
                            value = "{\"message\": \"해당 예약을 찾을 수 없습니다.\"}"
                    )
            }))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteReservationById(@PathVariable("id") final Long id);

    @Operation(summary = "내 예약 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 예약 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/mine")
    ResponseEntity<List<MyReservationAndWaitingResponse>> getMyReservations(@Login LoginMember loginMember);
} 