package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.service.ReservationCommandService;
import roomescape.reservation.service.ReservationQueryService;

@Tag(name = "Reservation", description = "예약 API")
@Validated
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationQueryService reservationQueryService;
    private final ReservationCommandService reservationCommandService;

    @Operation(summary = "예약 목록 조회", description = "검색 조건에 맞는 예약 목록을 조회합니다.")
    @GetMapping
    public List<ReservationResponse> findReservationsByCriteria(
            @Parameter(description = "예약 검색 조건") @ModelAttribute final ReservationSearchRequest request) {
        return reservationQueryService.findReservationsByCriteria(request);
    }

    @Operation(summary = "내 예약 목록 조회", description = "로그인한 회원의 예약 목록을 조회합니다.")
    @GetMapping("/mine")
    public List<MyReservationResponse> findMyReservations(
            @Parameter(description = "로그인한 회원 정보") final LoginMember loginMember) {
        return reservationQueryService.findMyReservations(loginMember);
    }

    @Operation(summary = "예약 생성", description = "새로운 예약을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse saveReservation(
            @Parameter(description = "예약 생성 요청 정보") @Valid @RequestBody final ReservationRequest request,
            @Parameter(description = "로그인한 회원 정보") final LoginMember member
    ) {
        return reservationCommandService.resisterReservation(request, member);
    }

    @Operation(summary = "예약 취소", description = "예약을 취소합니다.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@Parameter(description = "예약 ID") @PathVariable final Long id) {
        reservationCommandService.cancel(id);
    }
}
