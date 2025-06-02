package roomescape.common.exception;

public class ConnectionException extends CustomException {

    public ConnectionException(String message) {
        super(message, GenaralErrorCode.EXTERNAL_SERVER_CONNECTION_ERROR);
    }
}
