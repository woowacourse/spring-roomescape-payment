package roomescape.web;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.web.support.AdminHandlerInterceptor;

abstract class ExcludeInterceptorTest extends ControllerTest {

    @MockBean
    private AdminHandlerInterceptor adminHandlerInterceptor;

    @BeforeEach
    void setUp() {
        Mockito.doReturn(true)
                .when(adminHandlerInterceptor)
                .preHandle(any(), any(), any());
    }
}
