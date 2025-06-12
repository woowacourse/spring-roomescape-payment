package roomescape.presentation.api.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.command.CreateReservationTimeService;
import roomescape.application.reservation.command.DeleteReservationTimeService;
import roomescape.presentation.api.reservation.request.CreateReservationTimeRequest;

import java.net.URI;

@RestController
@Tag(name = "관리자 예약 시간 API")
@RequestMapping("/admin/times")
public class AdminReservationTimeController {

    private static final String RESERVATION_TIMES_URL = "/times/%d";

    private final CreateReservationTimeService createReservationTimeService;
    private final DeleteReservationTimeService deleteReservationTimeService;

    public AdminReservationTimeController(final CreateReservationTimeService createReservationTimeService,
                                          final DeleteReservationTimeService deleteReservationTimeService) {
        this.createReservationTimeService = createReservationTimeService;
        this.deleteReservationTimeService = deleteReservationTimeService;
    }

    @Operation(
            summary = "관리자 예약 시간 생성",
            description = "관리자가 예약 시간을 생성합니다. 요청 본문에 필요한 예약 시간 정보를 포함해야 합니다."
    )
    @PostMapping
    public ResponseEntity<Void> create(
            @Valid @RequestBody final CreateReservationTimeRequest createReservationTImeRequest) {
        final Long id = createReservationTimeService.register(createReservationTImeRequest.toCreateCommand());
        return ResponseEntity.created(URI.create(RESERVATION_TIMES_URL.formatted(id)))
                .build();
    }

    @Operation(
            summary = "관리자 예약 시간 삭제",
            description = "관리자가 예약 시간을 삭제합니다. 예약 시간 ID를 경로 변수로 전달해야 합니다."
    )
    @DeleteMapping("/{timeId}")
    public ResponseEntity<Void> deleteReservationTime(@PathVariable("timeId") final Long reservationTimeId) {
        deleteReservationTimeService.removeById(reservationTimeId);
        return ResponseEntity.noContent().build();
    }
}
