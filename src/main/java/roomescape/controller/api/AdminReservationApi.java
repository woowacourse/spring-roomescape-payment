package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.response.PendingReservationResponse;
import roomescape.dto.response.ReservationResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "5. 어드민 전용 API")
public interface AdminReservationApi {

    @Operation(summary = "예약 추가")
    ResponseEntity<ReservationResponse> save(
            @RequestBody(required = true) CreateReservationRequest request
    );

    @Operation(summary = "예약 필터링 조회")
    ResponseEntity<List<ReservationResponse>> getAllByFilter(
            @Parameter Long memberId,
            @Parameter Long themeId,
            @Parameter LocalDate dateFrom,
            @Parameter LocalDate dateTo
    );

    @Operation(summary = "대기 예약 조회")
    ResponseEntity<List<PendingReservationResponse>> getAllPendings();

    @Operation(summary = "대기 예약 거절")
    ResponseEntity<Void> denyPending(
            @Parameter(required = true) long reservationId
    );
}
