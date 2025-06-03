package roomescape.business.service;

import static roomescape.exception.ErrorCode.EMAIL_DUPLICATED;
import static roomescape.exception.ErrorCode.USER_NOT_EXIST;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.model.entity.User;
import roomescape.business.model.vo.Id;
import roomescape.exception.business.InvalidCreateArgumentException;
import roomescape.exception.business.NotFoundException;
import roomescape.infrastructure.UserRepository;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.UserResponse;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail_Value(request.email())) {
            throw new InvalidCreateArgumentException(EMAIL_DUPLICATED);
        }
        User user = User.create(request.name(), request.email(), request.password());
        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(final String userIdValue) {
        User user = userRepository.findById(Id.create(userIdValue))
                .orElseThrow(() -> new NotFoundException(USER_NOT_EXIST));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getByEmail(final String email) {
        User user = userRepository.findByEmail_Value(email)
                .orElseThrow(() -> new NotFoundException(USER_NOT_EXIST));
        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }
}
