package roomescape.core.dto.exception;

import java.util.List;
import org.springframework.validation.BindingResult;

public class ExceptionResponse {
    private final int status;
    private final String error;
    private final List<ExceptionDetails> details;

    public ExceptionResponse(final int status, final String error, final BindingResult bindingResult) {
        this.status = status;
        this.error = error;
        this.details = ExceptionDetails.create(bindingResult);
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public List<ExceptionDetails> getDetails() {
        return details;
    }
}