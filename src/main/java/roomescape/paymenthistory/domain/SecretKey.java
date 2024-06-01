package roomescape.paymenthistory.domain;

public class SecretKey {

    private final String secretKey;

    public SecretKey(String secretKey) {
        this.secretKey = secretKey;
        validation();
    }

    public String getSecretKey() {
        return secretKey;
    }

    private void validation() {
        if (secretKey == null) {
            throw new IllegalArgumentException("토스 연결에 실패했습니다. 관리자에게 문의해주세요.",
                    new Throwable("TossProperties 에 SecretKey가 null 입니다."));
        }
    }
}
