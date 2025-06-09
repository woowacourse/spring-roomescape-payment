package roomescape.reservation.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.reservation.dto.MyRegistrationResponse;
import roomescape.reservation.repository.RegistrationQueryRepository;
import roomescape.reservation.repository.dto.MemberRegistrationProjection;

@Service
@RequiredArgsConstructor
public class MyPageService {
    private final RegistrationQueryRepository registrationQueryRepository;

    public List<MyRegistrationResponse> getMyRegistrations(Long memberId) {
        List<MemberRegistrationProjection> registrationsData =
                registrationQueryRepository.findAllRegistrationsByMemberId(memberId);

        return MyRegistrationResponse.from(registrationsData);
    }
}
