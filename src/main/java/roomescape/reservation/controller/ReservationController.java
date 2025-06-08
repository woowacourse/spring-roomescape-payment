package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.utils.UriFactory;
import roomescape.member.auth.LoginMember;
import roomescape.member.auth.RoleRequired;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.member.domain.Role;
import roomescape.reservation.controller.dto.AvailableReservationTimeWebResponse;
import roomescape.reservation.controller.dto.CreateReservationWebRequest;
import roomescape.reservation.controller.dto.CreateReservationWithMemberIdWebRequest;
import roomescape.reservation.controller.dto.CreateWaitingWebRequest;
import roomescape.reservation.controller.dto.ReservationSearchWebRequest;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.controller.dto.ReservationWithStatusResponse;
import roomescape.reservation.service.ReservationService;

@RequiredArgsConstructor
@RestController
@RequestMapping
public class ReservationController {

    public static final String BASE_PATH = "/reservations";

    private final ReservationService reservationService;

    @Operation(summary = "모든 예약 조회", description = "어드민 권한으로 모든 예약을 조회합니다.")
    @RoleRequired(value = Role.ADMIN)
    @GetMapping(BASE_PATH)
    public List<ReservationWebResponse> getAll() {
        return reservationService.getAll();
    }

    @Operation(summary = "모든 예약 대기 조회", description = "어드민 권한으로 모든 예약 대기를 조회합니다.")
    @RoleRequired(value = Role.ADMIN)
    @GetMapping("/waitings")
    public List<ReservationWebResponse> getAllWaiting() {
        return reservationService.getAllWaiting();
    }

    @Operation(summary = "내 예약 조회", description = "로그인된 회원의 예약 목록을 조회합니다.")
    @GetMapping(BASE_PATH + "/mine")
    public List<ReservationWithStatusResponse> getAll(@LoginMember MemberInfo memberInfo) {
        return reservationService.getByMemberId(memberInfo.id());
    }

    @Operation(summary = "예약 가능 시간 조회", description = "예약 시간과 해당 시간이 예약이 가능한지 여부를 반환합니다.")
    @GetMapping(BASE_PATH + "/times")
    public List<AvailableReservationTimeWebResponse> getAvailable(
            @RequestParam final LocalDate date,
            @RequestParam final Long themeId) {
        return reservationService.getAvailable(date, themeId);
    }

    @Operation(summary = "내 예약 생성", description = "로그인된 회원의 예약을 생성합니다.")
    @PostMapping(BASE_PATH)
    public ResponseEntity<ReservationWithStatusResponse> create(
            @RequestBody final CreateReservationWebRequest createReservationWebRequest,
            @LoginMember MemberInfo memberInfo) {
        final ReservationWithStatusResponse reservationWithStatusResponse = reservationService.create(
                createReservationWebRequest,
                memberInfo);
        final URI location = UriFactory.buildPath(BASE_PATH,
                String.valueOf(reservationWithStatusResponse.id()));
        return ResponseEntity.created(location)
                .body(reservationWithStatusResponse);
    }

    @Operation(summary = "내 예약 대기 생성", description = "로그인된 회원의 예약 대기를 생성합니다.")
    @PostMapping("/waitings")
    public ResponseEntity<ReservationWithStatusResponse> createWaiting(
        @RequestBody final CreateWaitingWebRequest createWaitingWebRequest,
        @LoginMember MemberInfo memberInfo) {
        final ReservationWithStatusResponse reservationWithStatusResponse = reservationService.createWaiting(
            createWaitingWebRequest,
            memberInfo);
        final URI location = UriFactory.buildPath(BASE_PATH,
            String.valueOf(reservationWithStatusResponse.id()));
        return ResponseEntity.created(location)
            .body(reservationWithStatusResponse);
    }

    @Operation(summary = "예약 생성", description = "어드민 권한으로 특정 회원의 예약을 생성합니다.")
    @PostMapping("/admin" + BASE_PATH)
    public ResponseEntity<ReservationWebResponse> createReservationByAdmin(
            @RequestBody final CreateReservationWithMemberIdWebRequest createReservationWithMemberIdWebRequest) {
        final ReservationWebResponse reservationWebResponse = reservationService.create(
                createReservationWithMemberIdWebRequest);
        final URI location = UriFactory.buildPath(BASE_PATH, String.valueOf(reservationWebResponse.id()));
        return ResponseEntity.created(location)
                .body(reservationWebResponse);
    }

    @Operation(summary = "전체 예약 조회", description = "어드민 권한으로 전체 예약을 조회합니다.")
    @GetMapping("/admin" + BASE_PATH)
    public ResponseEntity<List<ReservationWebResponse>> getReservationsByAdmin(
            @ModelAttribute final ReservationSearchWebRequest reservationSearchWebRequest) {
        return ResponseEntity.ok(reservationService.search(reservationSearchWebRequest));
    }

    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다.")
    @DeleteMapping(BASE_PATH + "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 대기 삭제", description = "어드민 권한으로 예약 대기를 삭제합니다.")
    @RoleRequired(value = Role.ADMIN)
    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable final Long id) {
        reservationService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }
}
