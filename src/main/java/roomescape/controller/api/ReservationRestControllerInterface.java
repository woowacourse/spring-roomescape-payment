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
import roomescape.domain.auth.dto.LoginMember;
import roomescape.domain.reservation.dto.CreateReservationRequest;
import roomescape.domain.reservation.dto.CreateReservationResponse;
import roomescape.domain.reservation.dto.ReservationMineResponse;
import roomescape.domain.time.dto.AvailableReservationTimeRequest;
import roomescape.domain.time.dto.AvailableReservationTimeResponse;
import roomescape.domain.waiting.dto.CreateWaitingRequest;
import roomescape.domain.waiting.dto.CreateWaitingResponse;

@Tag(name = "Reservation", description = "예약 관련 API")
@RequestMapping("/reservations")
public interface ReservationRestControllerInterface {

    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 생성 성공"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없습니다."),
            @ApiResponse(responseCode = "409", description = "중복된 데이터가 존재합니다."),
            @ApiResponse(responseCode = "422", description = "과거 날짜는 예약 할 수 없습니다.")
    })
    @PostMapping
    ResponseEntity<CreateReservationResponse> createReservation(
            @RequestBody final CreateReservationRequest createReservationRequest,
            final LoginMember loginMember
    );

    @Operation(summary = "예약 조회", description = "예약된 정보들을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<CreateReservationResponse>> getReservations();

    @Operation(summary = "이용 가능한 시간 조회", description = "이용 가능한 시간을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이용 가능한 시간 조회 성공")
    })
    @GetMapping("/available-times")
    ResponseEntity<List<AvailableReservationTimeResponse>> getAvailableReservationTimes(
            @ModelAttribute final AvailableReservationTimeRequest request);

    @Operation(summary = "나의 예약 정보 조회", description = "나의 예약 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "나의 예약 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없습니다.")
    })
    @GetMapping("/mine")
    ResponseEntity<List<ReservationMineResponse>> getMyReservations(final LoginMember member);

    @Operation(summary = "대기 생성", description = "새로운 대기를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "대기 생성 성공"),
            @ApiResponse(responseCode = "404", description = "찾을 수 없습니다."),
            @ApiResponse(responseCode = "422", description = "도메인 규칙으로 인해 대기 생성 불가합니다.")
    })
    @PostMapping("/waitings")
    ResponseEntity<CreateWaitingResponse> createWaitingReservation(
            @RequestBody final CreateWaitingRequest createWaitingRequest,
            final LoginMember loginMember
    );

    @Operation(summary = "대기 삭제", description = "특정 ID에 해당하는 대기를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "대기 삭제 성공")
    })
    @DeleteMapping("/waitings/{id}")
    ResponseEntity<Void> deleteWaitingReservation(@PathVariable final Long id);
}
