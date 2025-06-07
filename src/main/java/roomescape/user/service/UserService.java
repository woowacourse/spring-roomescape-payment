package roomescape.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.dto.ReservationWithStateDto;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.user.domain.User;
import roomescape.user.domain.dto.UserRequestDto;
import roomescape.user.domain.dto.UserResponseDto;
import roomescape.user.exception.NotFoundUserException;
import roomescape.user.exception.UserForbiddenException;
import roomescape.user.repository.UserRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingWithRank;
import roomescape.waiting.exception.NotFoundWaitingException;
import roomescape.waiting.repository.WaitingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;

    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDto::of)
                .toList();
    }

    @Transactional
    public User add(UserRequestDto userRequestDto) {
        User user = userRequestDto.toEntity();
        return userRepository.save(user);
    }

    public List<ReservationWithStateDto> findAllReservationByMember(User member) {
        validateExistsById(member.getId());

        List<ReservationWithStateDto> reservationDtos = findReservationWithPaymentByMember(member);
        List<ReservationWithStateDto> waitingDtos = findWaitingsWithRankByMember(member);

        reservationDtos.addAll(waitingDtos);
        return reservationDtos;
    }

    private List<ReservationWithStateDto> findWaitingsWithRankByMember(User member) {
        List<Waiting> waitings = waitingRepository.findByMember(member);
        List<WaitingWithRank> waitingWithRanks = waitingRepository.findWaitingsWithRankByMemberId(
                member.getId());

        return convertReservationWithStateDto(waitings, waitingWithRanks);
    }

    private List<ReservationWithStateDto> findReservationWithPaymentByMember(User member) {
        List<Reservation> reservations = reservationRepository.findByUser(member);

        return reservations.stream()
                .map(reservation -> {
                    Optional<Payment> paymentOptional = paymentRepository.findByReservation(reservation);
                    PaymentResponseDto paymentDto = paymentOptional.map(PaymentResponseDto::of).orElse(null);

                    return ReservationWithStateDto.of(reservation, paymentDto);
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static List<ReservationWithStateDto> convertReservationWithStateDto(List<Waiting> waitings,
                                                                                List<WaitingWithRank> waitingWithRanks) {
        Map<Waiting, WaitingWithRank> rankMap = waitingWithRanks.stream()
                .collect(Collectors.toMap(WaitingWithRank::waiting, Function.identity()));

        return waitings.stream()
                .map(rankMap::get)
                .filter(Objects::nonNull)
                .map(ReservationWithStateDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteWaitingByMember(Long waitingId, User member) {
        validateExistsById(member.getId());
        Waiting waiting = waitingRepository.findById(waitingId)
                .orElseThrow(NotFoundWaitingException::new);

        if (!waiting.isSameMember(member)) {
            throw new UserForbiddenException("본인의 예약 대기만 삭제할 수 있습니다.");
        }

        waitingRepository.deleteById(waitingId);
    }

    public User findByIdOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(NotFoundUserException::new);
    }

    private void validateExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundUserException();
        }
    }
}
