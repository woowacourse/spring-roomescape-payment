package roomescape.reservation.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.application.dto.CreateReservationServiceRequest;
import roomescape.reservation.infrastructure.entity.ReservationViewRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationViewQueryService {

    private final ReservationViewRepository viewRepository;

    public boolean existsByParams(final CreateReservationServiceRequest request, final Long userId) {
        return viewRepository.existsByParams(request.date(), request.timeId(), request.themeId(), userId);
    }

    public Optional<Long> findFirstWaitingByReservationId(final Long id) {
        return viewRepository.findFirstWaitingByReservationId(id);
    }
}
