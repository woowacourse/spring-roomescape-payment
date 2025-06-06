package roomescape.user.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.user.application.service.UserQueryService;
import roomescape.user.ui.dto.UserResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserQueryService userQueryService;

    @Override
    public List<UserResponse> getAll() {
        log.info("[USER] 전체 사용자 조회 요청");
        return UserResponse.from(
                userQueryService.getAll());
    }
}
