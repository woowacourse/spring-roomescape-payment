package roomescape.unit.business.service;

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
import roomescape.business.model.entity.Member;
import roomescape.business.service.MemberService;
import roomescape.exception.business.InvalidCreateArgumentException;
import roomescape.exception.business.NotFoundException;
import roomescape.infrastructure.MemberRepository;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.MemberResponse;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService sut;

    @Test
    void 사용자_등록이_성공적으로_이루어진다() {
        // given
        String name = "테스트유저";
        String email = "test@example.com";
        String password = "password123";
        RegisterRequest request = new RegisterRequest(name, email, password);

        when(memberRepository.existsByEmail_Value(email)).thenReturn(false);

        // when
        sut.register(request);

        // then
        verify(memberRepository).existsByEmail_Value(email);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    void 이미_존재하는_이메일로_사용자_등록_시_예외가_발생한다() {
        // given
        String name = "테스트유저";
        String email = "test@example.com";
        String password = "password123";
        RegisterRequest request = new RegisterRequest(name, email, password);
        when(memberRepository.existsByEmail_Value(email)).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> sut.register(request))
                .isInstanceOf(InvalidCreateArgumentException.class);

        verify(memberRepository).existsByEmail_Value(email);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void 이메일로_사용자를_조회할_수_있다() {
        // given
        String email = "test@example.com";
        Member memberData = Member.restore("user-id", "USER", "Test User", email, "password123");
        MemberResponse expectedUser = new MemberResponse("user-id", "Test User", email);

        when(memberRepository.findByEmail_Value(email)).thenReturn(Optional.of(memberData));

        // when
        MemberResponse result = sut.getByEmail(email);

        // then
        assertThat(result).isEqualTo(expectedUser);
        verify(memberRepository).findByEmail_Value(email);
    }

    @Test
    void 존재하지_않는_이메일로_사용자_조회_시_예외가_발생한다() {
        // given
        String email = "nonexistent@example.com";

        when(memberRepository.findByEmail_Value(email)).thenReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> sut.getByEmail(email))
                .isInstanceOf(NotFoundException.class);

        verify(memberRepository).findByEmail_Value(email);
    }

    @Test
    void 모든_사용자를_조회할_수_있다() {
        // given
        List<Member> memberData = Arrays.asList(
                Member.restore("user-id-1", "USER", "User One", "user1@example.com", "password1"),
                Member.restore("user-id-2", "USER", "User Two", "user2@example.com", "password2")
        );
        List<MemberResponse> expectedUsers = Arrays.asList(
                new MemberResponse("user-id-1", "User One", "user1@example.com"),
                new MemberResponse("user-id-2", "User Two", "user2@example.com")
        );

        when(memberRepository.findAll()).thenReturn(memberData);

        // when
        List<MemberResponse> result = sut.getAll();

        // then
        assertThat(result).isEqualTo(expectedUsers);
        verify(memberRepository).findAll();
    }
}
