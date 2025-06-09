package roomescape.time.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.utils.UriFactory;
import roomescape.member.auth.RoleRequired;
import roomescape.member.domain.Role;
import roomescape.time.controller.dto.CreateReservationTimeWebRequest;
import roomescape.time.controller.dto.ReservationTimeWebResponse;
import roomescape.time.service.ReservationTimeService;

@RequiredArgsConstructor
@RestController
@RequestMapping(ReservationTimeController.BASE_PATH)
@Tag(name = "ReservationTime", description = "예약 시간 관리 api")
public class ReservationTimeController {

    public static final String BASE_PATH = "/times";

    private final ReservationTimeService reservationTimeService;

    @Operation(summary = "예약 시간 조회", description = "예약 시간 전체를 조회합니다.")
    @GetMapping
    public List<ReservationTimeWebResponse> getAll() {
        return reservationTimeService.getAll();
    }

    @Operation(summary = "예약 시간 생성", description = "어드민 권한으로 예약 시간을 생성합니다.")
    @RoleRequired(value = Role.ADMIN)
    @PostMapping
    public ResponseEntity<ReservationTimeWebResponse> create(
            @RequestBody final CreateReservationTimeWebRequest createReservationTimeWebRequest) {
        final ReservationTimeWebResponse reservationTimeWebResponse = reservationTimeService.create(createReservationTimeWebRequest);
        final URI location = UriFactory.buildPath(BASE_PATH, String.valueOf(reservationTimeWebResponse.id()));
        return ResponseEntity.created(location)
                .body(reservationTimeWebResponse);
    }

    @Operation(summary = "예약 시간 삭제", description = "어드민 권한으로 예약 시간을 삭제합니다.")
    @RoleRequired(value = Role.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
