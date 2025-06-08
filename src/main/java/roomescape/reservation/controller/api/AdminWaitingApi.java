package roomescape.reservation.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.reservation.dto.response.WaitingResponse;

@Tag(name = "AdminWaiting", description = "어드민 예약 대기 API")
@RequestMapping("/admin/waitings")
public interface AdminWaitingApi {

    @Operation(summary = "어드민 예약 대기 조회", description = "어드민 페이지에서 전체 예약 대기를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 예약 대기 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 없음"),
    })
    @RequiredAdmin
    @GetMapping
    ResponseEntity<List<WaitingResponse>> readAllWaiting();

    @Operation(summary = "어드민 예약 대기 삭제", description = "어드민 페이지에서 예약 대기를 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 대기 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 없음"),
            @ApiResponse(responseCode = "404", description = "예약 대기가 존재하지 않음")
    })
    @RequiredAdmin
    @DeleteMapping("/{waitingId}")
    ResponseEntity<Void> deny(@PathVariable("waitingId") final Long waitingId);
}
