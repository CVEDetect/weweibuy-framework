package com.weweibuy.framework.common.codec.aes;

import com.weweibuy.framework.common.codec.HexUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * A simple utility class for easily encrypting and decrypting data using the AES algorithm.
 * <p>
 * 注意:  JDK  1.8.0_161 之前AES 只支持 126位即16长度的秘钥
 * 之后可以支持  256位即32长度的秘钥
 *
 * @author Chad Adams
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AESUtils {

    public static final String ALGORITHM = "AES";


    public static SecretKey generateKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    /**
     * 生成 16进制key
     *
     * @return
     */
    public static String generateKeyHex() {
        return HexUtils.toHexString(generateKey().getEncoded());
    }


    /**
     * Creates a new {@link SecretKey} based on a password.
     *
     * @param password The password that will be the {@link SecretKey}.
     * @return The key.
     */
    public static SecretKey createKey(String password) {
        byte[] key = password.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); // use only first 128 bit
        return new SecretKeySpec(key, ALGORITHM);
    }

    /**
     * Creates a new {@link SecretKey} based on a password with a specified salt.
     *
     * @param salt     The random salt.
     * @param password The password that will be the {@link SecretKey}.
     * @return The key.
     */
    public static SecretKey createKey(byte[] salt, String password) {
        try {
            byte[] key = (salt + password).getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit

            return new SecretKeySpec(key, ALGORITHM);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

    }

    /**
     * The method that writes the {@link SecretKey} to a file.
     *
     * @param key  The key to write.
     * @param file The file to create.
     * @throws IOException If the file could not be created.
     */
    public static void writeKey(SecretKey key, File file) throws IOException {
        try (FileOutputStream fis = new FileOutputStream(file)) {
            fis.write(key.getEncoded());
        }
    }

    /**
     * The method that will encrypt data.
     *
     * @param secretKey The key used to encrypt the data.
     * @param data      The data to encrypt.
     * @return The encrypted data.
     */
    public static byte[] encrypt(SecretKey secretKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(String secretKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, createKey(secretKey));
        return cipher.doFinal(data);
    }

    public static byte[] encrypt(String secretKey, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, createKey(secretKey));
        return cipher.doFinal(data.getBytes());
    }


    public static String encryptToHex(String secretKey, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, createKey(secretKey));
        return HexUtils.toHexString(cipher.doFinal(data));
    }

    public static String encryptToHex(String secretKey, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, createKey(secretKey));
        return HexUtils.toHexString(cipher.doFinal(data.getBytes()));
    }

    /**
     * Gets a {@link SecretKey} from a {@link File}.
     *
     * @param file The file that is encoded as a key.
     * @return The key.
     * @throws IOException The exception thrown if the file could not be read as a {@link SecretKey}.
     */
    public static SecretKey getSecretKey(File file) throws IOException {
        return new SecretKeySpec(Files.readAllBytes(file.toPath()), ALGORITHM);
    }

    /**
     * 加密 并转为16进制文本
     *
     * @param secretKey 秘钥
     * @param src
     * @return
     */
    public static String encryptToHex(SecretKey secretKey, String src) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return HexUtils.toHexString(encrypt(secretKey, src.getBytes()));
    }

    /**
     * The method that will decrypt a piece of encrypted data.
     *
     * @param password The password used to decrypt the data.
     * @param content  The encrypted data.
     * @return The decrypted data.
     */
    public static byte[] decrypt(String password, byte[] content) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, AESUtils.createKey(password));
        return cipher.doFinal(content);
    }

    /**
     * The method that will decrypt a piece of encrypted data.
     *
     * @param secretKey The key used to decrypt encrypted data.
     * @param encrypted The encrypted data.
     * @return The decrypted data.
     */
    public static byte[] decrypt(SecretKey secretKey, byte[] encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encrypted);
    }

    /**
     * 解密
     *
     * @param secretKey 秘钥
     * @param encrypt   16进制密文
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String decryptHex(SecretKey secretKey, String hexContent) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(HexUtils.fromHexString(hexContent)));
    }

    public static String decryptHex(String secretKey, String hexContent) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, createKey(secretKey));
        return new String(cipher.doFinal(HexUtils.fromHexString(hexContent)));
    }

}
