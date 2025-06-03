package roomescape.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.business.model.entity.User;
import roomescape.exception.business.InvalidCreateArgumentException;
import roomescape.exception.business.NotFoundException;
import roomescape.infrastructure.UserRepository;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.UserResponse;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService sut;

    @Test
    void 사용자_등록이_성공적으로_이루어진다() {
        // given
        String name = "테스트유저";
        String email = "test@example.com";
        String password = "password123";
        RegisterRequest request = new RegisterRequest(name, email, password);

        when(userRepository.existsByEmail_Value(email)).thenReturn(false);

        // when
        sut.register(request);

        // then
        verify(userRepository).existsByEmail_Value(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 이미_존재하는_이메일로_사용자_등록_시_예외가_발생한다() {
        // given
        String name = "테스트유저";
        String email = "test@example.com";
        String password = "password123";
        RegisterRequest request = new RegisterRequest(name, email, password);
        when(userRepository.existsByEmail_Value(email)).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> sut.register(request))
                .isInstanceOf(InvalidCreateArgumentException.class);

        verify(userRepository).existsByEmail_Value(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void 이메일로_사용자를_조회할_수_있다() {
        // given
        String email = "test@example.com";
        User userData = User.restore("user-id", "USER", "Test User", email, "password123");
        UserResponse expectedUser = new UserResponse("user-id", "Test User", email);

        when(userRepository.findByEmail_Value(email)).thenReturn(Optional.of(userData));

        // when
        UserResponse result = sut.getByEmail(email);

        // then
        assertThat(result).isEqualTo(expectedUser);
        verify(userRepository).findByEmail_Value(email);
    }

    @Test
    void 존재하지_않는_이메일로_사용자_조회_시_예외가_발생한다() {
        // given
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail_Value(email)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getByEmail(email))
                .isInstanceOf(NotFoundException.class);

        verify(userRepository).findByEmail_Value(email);
    }

    @Test
    void 모든_사용자를_조회할_수_있다() {
        // given
        List<User> userData = Arrays.asList(
                User.restore("user-id-1", "USER", "User One", "user1@example.com", "password1"),
                User.restore("user-id-2", "USER", "User Two", "user2@example.com", "password2")
        );
        List<UserResponse> expectedUsers = Arrays.asList(
                new UserResponse("user-id-1", "User One", "user1@example.com"),
                new UserResponse("user-id-2", "User Two", "user2@example.com")
        );

        when(userRepository.findAll()).thenReturn(userData);

        // when
        List<UserResponse> result = sut.getAll();

        // then
        assertThat(result).isEqualTo(expectedUsers);
        verify(userRepository).findAll();
    }
}
