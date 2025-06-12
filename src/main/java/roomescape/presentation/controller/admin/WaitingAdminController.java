package roomescape.presentation.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.WaitingAdminService;
import roomescape.dto.response.WaitingAdminResponseDto;

@Tag(name = "관리자 전용 웨이팅 API")
@RestController
@RequestMapping("/admin/waitings")
@RequiredArgsConstructor
public class WaitingAdminController {

    private final WaitingAdminService waitingAdminService;

    @Operation(summary = "웨이팅 전체 조회")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<WaitingAdminResponseDto> getWaitings() {
        return waitingAdminService.getAllWaitings();
    }

    @Operation(summary = "웨이팅 삭제")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void rejectWaiting(@PathVariable Long id) {
        waitingAdminService.rejectWaiting(id);
    }
}
