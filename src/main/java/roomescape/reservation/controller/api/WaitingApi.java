package roomescape.reservation.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.WaitingResponse;

@Tag(name = "Waiting", description = "예약 대기 API")
@RequestMapping("/waitings")
public interface WaitingApi {

    @Operation(summary = "예약 대기 생성", description = "전체 예약 시간을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 대기 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 없음"),
            @ApiResponse(responseCode = "404", description = "예약이 존재하지 않음")
    })
    @PostMapping
    ResponseEntity<WaitingResponse> create(
            @Valid @RequestBody final ReservationRequest request,
            final LoginMember loginMember
    );

    @Operation(summary = "예약 대기 삭제", description = "예약 대기를 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 대기 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "예약 대기가 존재하지 않음")
    })
    @DeleteMapping("/{waitingId}")
    ResponseEntity<Void> delete(@PathVariable("waitingId") final Long waitingId);
}
