package com.weweibuy.framework.common.codec.aes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author durenhao
 * @date 2021/10/30 13:51
 **/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Aes256GcmUtils {

    static final int KEY_LENGTH_BYTE = 32;
    static final int TAG_LENGTH_BIT = 128;

    public static SecretKey createKey(String hexKey32) {
        byte[] keyBytes = hexKey32.getBytes();
        if (keyBytes.length != KEY_LENGTH_BYTE) {
            throw new IllegalArgumentException("密钥长度必须为32个字节");
        }
        return new SecretKeySpec(keyBytes, AESUtils.ALGORITHM);
    }

    /**
     * 解密
     *
     * @param encryptText
     * @param associatedData
     * @param nonce
     * @param secretKey
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String decrypt(byte[] encryptText, byte[] associatedData, byte[] nonce, SecretKey secretKey)
            throws GeneralSecurityException, IOException {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, nonce);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
            cipher.updateAAD(associatedData);
            return new String(cipher.doFinal(encryptText),
                    "utf-8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalArgumentException("AES解密失败", e);
        }
    }

    /**
     * 解密
     *
     * @param encryptText
     * @param associatedData
     * @param nonce
     * @param secretKey
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String decrypt(String encryptText, String associatedData, String nonce, SecretKey secretKey)
            throws GeneralSecurityException, IOException {
        return decrypt(encryptText.getBytes(), associatedData.getBytes(), nonce.getBytes(), secretKey);
    }


    public static String decryptBase64Text(String encryptText, byte[] associatedData, byte[] nonce, SecretKey secretKey)
            throws GeneralSecurityException, IOException {
        return decrypt(Base64.getDecoder().decode(encryptText), associatedData, nonce, secretKey);
    }

    public static String decryptBase64Text(String encryptText, String associatedData, String nonce, SecretKey secretKey)
            throws GeneralSecurityException, IOException {
        return decrypt(Base64.getDecoder().decode(encryptText), associatedData.getBytes(), nonce.getBytes(), secretKey);
    }
}
