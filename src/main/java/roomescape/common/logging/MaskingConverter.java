package roomescape.common.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class MaskingConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        message = maskPaymentKey(message);
        message = maskOrderId(message);
        message = maskEmail(message);
        return message;
    }

    private String maskPaymentKey(String message) {
        if (message == null) return null;

        return message.replaceAll(
                "(paymentKey=)(.{6}).+?(.{4})(?=[^a-zA-Z0-9]|$)",
                "$1$2****$3"
        );
    }

    private String maskOrderId(String message) {
        if (message == null) return null;

        return message.replaceAll(
                "(orderId=)(.{6}).+?(.{4})(?=[^a-zA-Z0-9]|$)",
                "$1$2****$3"
        );
    }

    private String maskEmail(String message) {
        return message.replaceAll(
                "(email=)([^@\\s]{1})[^@\\s]*(@[^\\s,\\]]+)",
                "$1$2****$3"
        );
    }
}
