package roomescape.reservation.controller.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.request.FilteringReservationRequest;
import roomescape.reservation.dto.request.ReservationPaymentRequest;
import roomescape.reservation.dto.response.BookedReservationTimeResponse;
import roomescape.reservation.dto.response.MyReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;

@Tag(name = "Reservation", description = "예약 API")
@RequestMapping("/reservations")
public interface ReservationApi {

    @Operation(summary = "전체 예약 조회", description = "전체 예약 대기를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 예약 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<ReservationResponse>> readAllReservations();

    @Operation(summary = "예약 가능 시간 조회", description = "지정한 날짜와 테마에 예약 가능한 시간을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 예약 가능 시간 조회 성공"),
    })
    @GetMapping("/times/available")
    ResponseEntity<List<BookedReservationTimeResponse>> readAvailableReservationTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("themeId") final Long themeId
    );

    @Operation(summary = "예약 결제 생성", description = "지정한 날짜와 테마, 시간에 대한 예약과 결제를 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "예약 결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 없음"),
            @ApiResponse(responseCode = "409", description = "중복 예약 존재 및 예약 대기 존재")
    })
    @PostMapping
    ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final ReservationPaymentRequest request,
            final LoginMember loginMember
    );

    @Operation(summary = "예약 결제 삭제", description = "지정한 날짜와 테마, 시간에 대한 예약과 결제를 삭제한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "예약 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "로그인 정보 없음"),
            @ApiResponse(responseCode = "404", description = "예약과 결제가 존재하지 않음")
    })
    @DeleteMapping("/{reservationId}")
    ResponseEntity<Void> delete(@PathVariable("reservationId") final Long reservationId);

    @Operation(summary = "조건 예약 조회", description = "날짜, 시간, 테마 조건에 맞는 예약을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조건 예약 조회 성공")
    })
    @GetMapping("/filtering")
    ResponseEntity<List<ReservationResponse>> findAllByFilter(
            @ModelAttribute @Valid final FilteringReservationRequest request
    );

    @Operation(summary = "내 예약 조회", description = "내 전체 예약을 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 예약 조회 성공")
    })
    @GetMapping("/my")
    ResponseEntity<List<MyReservationsResponse>> getMyReservations(final @Valid LoginMember loginMember);
}
