package roomescape.application;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.reservation.pendingpayment.PendingPaymentRepository;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.domain.reservation.waiting.WaitingRepository;
import roomescape.domain.reservation.waiting.WaitingWithRank;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRepository;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.NotFoundException;
import roomescape.presentation.response.UserReservationRecordsResponse;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReservedRepository reservedRepository;
    private final PendingPaymentRepository pendingPaymentRepository;
    private final WaitingRepository waitingRepository;

    @Transactional
    public User saveUser(final String email, final String password, final String name) {
        validateEmailNotRegistered(email);
        User user = User.register(name, email, password);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserReservationRecordsResponse> findTotalRecordByUserId(Long userId) {
        validateUserExists(userId);
        List<Reserved> reserveds = reservedRepository.findByUserId(userId);
        List<PendingPayment> pendingPayments = pendingPaymentRepository.findByUserId(userId);
        List<WaitingWithRank> waitings = waitingRepository.findWaitingWithRankByUserId(userId);

        List<UserReservationRecordsResponse> reservedResponses = UserReservationRecordsResponse.fromReserves(reserveds);
        List<UserReservationRecordsResponse> pendingPaymentResponses = UserReservationRecordsResponse.fromPendingPayment(
                pendingPayments);
        List<UserReservationRecordsResponse> waitingResponses = UserReservationRecordsResponse.fromWaitingsWithRank(
                waitings);

        List<UserReservationRecordsResponse> userReservationRecordsResponse = new ArrayList<>();
        userReservationRecordsResponse.addAll(reservedResponses);
        userReservationRecordsResponse.addAll(pendingPaymentResponses);
        userReservationRecordsResponse.addAll(waitingResponses);
        return userReservationRecordsResponse;
    }

    private void validateEmailNotRegistered(String email) {
        boolean isEmailAlreadyRegistered = userRepository.existsByEmail(email);

        if (isEmailAlreadyRegistered) {
            throw new AlreadyExistedException("이미 해당 이메일로 가입된 사용자가 있습니다.");
        }
    }

    private void validateUserExists(long userId) {
        boolean isUserExists = userRepository.existsById(userId);

        if (!isUserExists) {
            throw new NotFoundException("존재하지 않는 사용자입니다.");
        }
    }
}
