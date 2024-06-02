package roomescape.payment.service;

import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EncodingService {

    private final AES256TextEncryptor textEncryptor;

    public EncodingService(@Value("${jasypt.encryptor.password}") String encryptPassword) {
        this.textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(encryptPassword);
    }

    public String encrypt(String plaintext) {
        return textEncryptor.encrypt(plaintext);
    }

    public String decrypt(String ciphertext) {
        return textEncryptor.decrypt(ciphertext);
    }
}
