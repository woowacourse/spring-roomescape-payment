package roomescape.service;

import static roomescape.exception.ExceptionType.NOT_FOUND_MEMBER;
import static roomescape.fixture.MemberFixture.DEFAULT_MEMBER;
import static roomescape.fixture.PaymentFixture.DEFAULT_APPROVE_REQUEST;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import roomescape.domain.payment.ApiCallException;
import roomescape.domain.payment.PaymentApiResponseErrorHandler;
import roomescape.domain.payment.PaymentClient;
import roomescape.exception.RoomescapeException;
import roomescape.repository.CollectionMemberRepository;
import roomescape.repository.CollectionPaymentRepository;
import roomescape.repository.MemberRepository;
import roomescape.repository.PaymentRepository;

class PaymentServiceTest {

    @Test
    @DisplayName("존재하지 않는 회원이 결제를 요청하면 예외가 발생하는지 확인")
    void approveFailWhenNotFoundMember() {
        PaymentClient paymentClient = new PaymentClient(new PaymentApiResponseErrorHandler(new ObjectMapper()));
        PaymentRepository paymentRepository = new CollectionPaymentRepository();
        MemberRepository memberRepository = new CollectionMemberRepository();
        PaymentService paymentService = new PaymentService(paymentClient, paymentRepository, memberRepository);

        Assertions.assertThatThrownBy(() -> paymentService.approve(DEFAULT_APPROVE_REQUEST, DEFAULT_MEMBER.getId()))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(NOT_FOUND_MEMBER.getMessage());
    }

    @Test
    @DisplayName("외부 결제 승인 API 호출이 실패할 경우 예외가 발생하는지 확인")
    void approveFailWhenPaymentApiFail() {
        PaymentClient paymentClient = Mockito.mock(PaymentClient.class);
        Mockito.when(paymentClient.approve(DEFAULT_APPROVE_REQUEST, DEFAULT_MEMBER))
                .thenThrow(ApiCallException.class);

        PaymentRepository paymentRepository = new CollectionPaymentRepository();
        MemberRepository memberRepository = new CollectionMemberRepository(List.of(DEFAULT_MEMBER));
        PaymentService paymentService = new PaymentService(paymentClient, paymentRepository, memberRepository);

        Assertions.assertThatThrownBy(() -> paymentService.approve(DEFAULT_APPROVE_REQUEST, DEFAULT_MEMBER.getId()))
                .isInstanceOf(ApiCallException.class);
    }
}
