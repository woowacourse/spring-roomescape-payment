package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.admin.domain.dto.SearchReservationRequestDto;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.dto.ReservationInfo;
import roomescape.reservation.domain.dto.ReservationRequestDto;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.reservation.exception.DuplicateReservationException;
import roomescape.reservation.exception.InvalidReservationTimeException;
import roomescape.reservation.exception.NotFoundReservationException;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.exception.InvalidThemeException;
import roomescape.theme.repository.ThemeRepository;
import roomescape.user.domain.User;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentService paymentService;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            WaitingRepository waitingRepository,
            PaymentService paymentService
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.waitingRepository = waitingRepository;
        this.paymentService = paymentService;
    }

    public List<ReservationResponseDto> findAll() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::convertReservationResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponseDto add(ReservationRequestDto requestDto, User user) {
        Reservation reservation = convertReservation(requestDto, user);
        validateDuplicateDateTime(reservation);
        Reservation savedReservation = reservationRepository.save(reservation);
        return convertReservationResponseDto(savedReservation);
    }

    @Transactional
    public ReservationInfo cancelReservationAndReturnInfo(Long id) {
        Reservation oldReservation = findByIdOrThrow(id);
        paymentService.cancelPaymentByReservation(oldReservation);
        oldReservation.changeStatus(ReservationStatus.CANCELLED);
        return new ReservationInfo(oldReservation.getDate(), oldReservation.getReservationTime(), oldReservation.getTheme());
    }

    @Transactional
    public void approveWaiting(ReservationInfo reservationInfo) {
        Optional<Waiting> waitingOptional = waitingRepository.findOneByReservationInfoDesc(reservationInfo.date(),
                reservationInfo.time().getId(), reservationInfo.theme().getId());
        if (waitingOptional.isEmpty()) {
            return;
        }

        Waiting waiting = waitingOptional.get();
        waitingRepository.deleteById(waiting.getId());

        Reservation newReservation = Reservation.ofWaiting(waiting);
        reservationRepository.save(newReservation);
    }

    private Reservation findByIdOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundReservationException("해당 예약 id가 존재하지 않습니다."));
    }

    private void validateDuplicateDateTime(Reservation inputReservation) {
        boolean exists = reservationRepository.existsByDateAndReservationTime(
                inputReservation.getDate(),
                inputReservation.getReservationTime()
        );
        if (exists) {
            throw new DuplicateReservationException();
        }
    }

    public List<ReservationResponseDto> findReservationsByUserAndThemeAndFromAndTo(
            SearchReservationRequestDto searchReservationRequestDto) {
        List<Reservation> reservations = reservationRepository.findReservationsByUserAndThemeAndFromAndTo(
                searchReservationRequestDto.userId(),
                searchReservationRequestDto.themeId(),
                searchReservationRequestDto.from(),
                searchReservationRequestDto.to()
        );

        return reservations.stream()
                .map(this::convertReservationResponseDto)
                .toList();
    }

    private Reservation convertReservation(ReservationRequestDto dto, User user) {
        ReservationTime reservationTime = reservationTimeRepository.findById(dto.timeId())
                .orElseThrow(InvalidReservationTimeException::new);
        Theme theme = themeRepository.findById(dto.themeId())
                .orElseThrow(InvalidThemeException::new);

        return dto.toEntity(reservationTime, theme, user);
    }

    private ReservationResponseDto convertReservationResponseDto(Reservation reservation) {
        return ReservationResponseDto.of(reservation);
    }
}
