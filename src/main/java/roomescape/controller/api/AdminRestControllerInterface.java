package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.domain.admin.dto.AdminReservationRequest;
import roomescape.domain.admin.dto.AdminReservationResponse;
import roomescape.domain.admin.dto.ReservationSearchRequest;
import roomescape.domain.admin.dto.ReservationWaitingResponse;

@Tag(
        name = "Admin",
        description = """
                어드민 관련 API
                
                어드민 계정으로 로그인한 후에만 정상적으로 테스트할 수 있습니다.
                
                인증은 쿠키에 토큰이 저장되는 방식이며, Swagger try it out에서는 쿠키 인증을 지원하지 않아 직접 테스트가 불가능할 수 있습니다.
                
                실제 인증이 필요한 API 호출은 별도의 클라이언트(예: Postman)나 브라우저에서 쿠키를 포함하여 요청해야 합니다.
                """
)
@RequestMapping("/admin")
public interface AdminRestControllerInterface {

    @Operation(summary = "관리자 예약 생성", description = "관리자가 새로운 예약을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "예약이 이미 존재합니다.")
    })
    @PostMapping("/reservations")
    ResponseEntity<AdminReservationResponse> createReservation(
            @RequestBody final AdminReservationRequest adminReservationRequest
    );

    @Operation(summary = "관리자 예약 삭제", description = "관리자가 새로운 예약을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없습니다.")
    })
    @DeleteMapping("/reservations/{id}")
    ResponseEntity<Void> deleteReservation(@PathVariable final Long id);

    @Operation(summary = "조회 가능한 예약 정보 검색", description = "관리자가 예약된 정보들을 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 검색 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없습니다.")
    })
    @GetMapping("/searchable-reservations")
    ResponseEntity<List<AdminReservationResponse>> getReservationsBySearch(
            @ModelAttribute final ReservationSearchRequest searchRequest);

    @Operation(summary = "대기 정보 조회", description = "관리자가 대기 정보들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "대기 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
    })
    @GetMapping("/waitings")
    ResponseEntity<List<ReservationWaitingResponse>> waitingManagement();

    @Operation(summary = "대기 삭제", description = "관리자가 대기 정보를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "대기 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "대기 정보를 찾을 수 없습니다.")
    })
    @DeleteMapping("/waitings/{id}")
    ResponseEntity<Void> deleteWaiting(@PathVariable final Long id);
}
