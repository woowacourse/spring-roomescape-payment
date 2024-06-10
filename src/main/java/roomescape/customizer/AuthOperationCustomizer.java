package roomescape.customizer;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class AuthOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        Parameter param = new Parameter()
                .in(ParameterIn.COOKIE.toString())
                .schema(new StringSchema()._default(
                        "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6M30.rGRgG2z5yS_1o-91vsdjjkdshj76sbdsj").name("token"))
                .name("Cookies")
                .description("유저 토큰")
                .required(true);

        operation.addParametersItem(param);
        return operation;
    }
}
