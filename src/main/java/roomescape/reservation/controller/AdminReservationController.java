package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.reservation.dto.request.CreateReservationByAdminRequest;
import roomescape.reservation.dto.response.CreateReservationResponse;
import roomescape.reservation.dto.response.FindAdminReservationResponse;
import roomescape.reservation.service.ReservationService;

import java.net.URI;
import java.util.List;

@Tag(name = "관리자 예약 API", description = "관리자 예약 관련 API")
@RestController
@RequestMapping("/admin/reservations")
public class AdminReservationController {
    private final ReservationService reservationService;

    public AdminReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "관리자 예약 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "관리자 예약 생성 성공"),
            @ApiResponse(responseCode = "400", description = """
                    1. 예약 날짜는 현재보다 과거일 수 없습니다.
                    2. 예약 등록 시 예약 날짜는 필수입니다.
                    3. 예약하고자 하는 회원 식별자는 양수만 가능합니다.
                    4. 예약 등록 시 회원은 필수입니다.
                    5. 예약 등록 시 시간 식별자는 양수만 가능합니다.
                    6. 예약 등록 시 시간은 필수입니다.
                    7. 예약 등록 시 테마 식별자는 양수만 가능합니다.
                    8. 예약 등록 시 테마는 필수입니다.
                    9. 이미 해당 날짜의 선택한 테마의 시간에 예약이 존재하여 예약을 생성할 수 없습니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = """
                    1. 식별자에 해당하는 시간이 존재하지 않습니다.
                    2. 식별자에 해당하는 테마가 존재하지 않습니다.
                    3. 식별자에 해당하는 회원이 존재하지 않습니다.
                    """, content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<CreateReservationResponse> createReservationByAdmin(
            @Valid @RequestBody CreateReservationByAdminRequest createReservationByAdminRequest) {
        CreateReservationResponse createReservationResponse = reservationService.createReservationByAdmin(
                createReservationByAdminRequest);
        return ResponseEntity.created(URI.create("/reservations/" + createReservationResponse.id())).body(createReservationResponse);
    }

    @Operation(summary = "관리자 예약 조회 API")
    @ApiResponse(responseCode = "200", description = "관리자 예약 조회 성공")
    @GetMapping
    public ResponseEntity<List<FindAdminReservationResponse>> getReservationsByAdmin() {
        return ResponseEntity.ok(reservationService.getReservations());
    }
}
