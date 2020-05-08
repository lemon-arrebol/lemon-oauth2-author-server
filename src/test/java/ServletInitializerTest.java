import org.jasypt.util.text.BasicTextEncryptor;

public class ServletInitializerTest {
    public static void main(String[] args) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        String password = "qwertyuiop";
        String plainText = "123456";
        textEncryptor.setPassword(password);
        String cipherText = textEncryptor.encrypt(plainText);
        System.out.println(cipherText);
    }
}