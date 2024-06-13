package roomescape.log;

public interface LogManager {

    void logInfo(Exception e);

    void logInfo(String message, Exception e);
}
