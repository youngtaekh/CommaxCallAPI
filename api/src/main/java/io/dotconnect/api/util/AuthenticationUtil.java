package io.dotconnect.api.util;

import android.content.Context;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.UUID;

public class AuthenticationUtil {
    private static final String CLASS = "AuthenticationUtil - ";
    private static final int derivedKeyLength = 192;
    private static final int iterationsForMsg = 2;
    private static final String MessageSalt = "TyphoneMessage";
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final String algorithm = "PBKDF2WithHmacSHA1";

    private static final int derivedKeyBitLength = 256;
    private static final int derivedSaltKeyBitLength = 768;
    private static final int iterations = 10;
    private static final String passwordSalt = "voIce";

    /**
     * 가변적인 Salt를 생성하여 스트링을 암호화하는 메소드
     *
     * 알고리즘 : PBKDF2 with SHA-1
     * Pseudorandom function : HMAC
     * 입력키 : 사용자의 패스워드
     * Salt : 아래 salt 생성 함수를 사용하여 얻은 384bits (48bytes) 짜리 salt
     * Iteration : 10
     * Derived key length : 256bits (32bytes)
     *
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static String getEncryptedPasswordWithPasswordBasedSalt(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] salt = AuthenticationUtil.generateSalt(password);
        byte[] encryptedStringByte = encryptString(password, salt, iterations, derivedKeyBitLength);
        return bytesToHex(encryptedStringByte).toLowerCase();
    }

    /**
     * 가변적인 Salt를 생성하여 스트링을 암호화하는 메소드
     *
     * 알고리즘 : PBKDF2 with SHA-1
     * Pseudorandom function : HMAC
     * 입력키 : 사용자의 패스워드
     * Salt : 아래 salt 생성 함수를 사용하여 얻은 384bits (48bytes) 짜리 salt
     * Iteration : 2
     * Derived key length : 192bits (24bytes)
     */
    public static String getEncryptedMessageId(String str) {

        try {
            byte[] encryptedStringByte = encryptString(str, MessageSalt.getBytes(), iterationsForMsg, derivedKeyLength);
            return bytesToHex(encryptedStringByte).toLowerCase();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 가변적인 Salt 생성 메소드
     *
     * 알고리즘 : PBKDF2 with SHA-1
     * Pseudorandom function : HMAC
     * 입력키 : 사용자의 패스워드
     * Salt : voIce
     * Iteration : 10
     * Derived key length : 768bits (96bytes)
     *
     * salt 생성 함수로 나온 96bytes의 derived key에서 0,2,4,6,8,10,... 번째 byte 값을 추출해서 48bytes 짜리 최종 salt 값을 얻음.
     *
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static byte[] generateSalt(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encryptedPasswordByte = null;
        int saltByteSize = (derivedSaltKeyBitLength / 8) / 2;
        byte[] saltByte = new byte[saltByteSize];
        int saltIndex = 0;
        int derivedKeyIndex = 0;
        try{
            encryptedPasswordByte = encryptString(password, passwordSalt.getBytes(), iterations, derivedSaltKeyBitLength);
        }catch(InvalidKeySpecException e){
            e.printStackTrace();
        }
        while(saltIndex < saltByteSize){
            saltByte[saltIndex] = encryptedPasswordByte[derivedKeyIndex];
            saltIndex++;
            derivedKeyIndex = derivedKeyIndex + 2;
        }
        return saltByte;
    }

    private static byte[] encryptString(String str, byte[] salt, int iterations, int derivedKeyLength) throws NoSuchAlgorithmException, InvalidKeySpecException{
        byte[] encryptedStringByte = null;
        KeySpec spec = new PBEKeySpec(str.toCharArray(), salt, iterations, derivedKeyLength);
        SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
        encryptedStringByte = f.generateSecret(spec).getEncoded();
        return encryptedStringByte;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String makeSHA256(String str) {
        String SHA;
        try{
            MessageDigest sh = MessageDigest.getInstance("SHA-256");
            sh.update(str.getBytes());
            byte byteData[] = sh.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByteData : byteData) {
                sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
            }
            SHA = sb.toString();
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            SHA = null;
        }
        return SHA;
    }

    public static String getUUID(Context context){
        ConnectSharedPreference sharedPreference = ConnectSharedPreference.getInstance(context);

        if(sharedPreference.getUUID().equals("")) {
            sharedPreference.setUUID(UUID.randomUUID().toString().toUpperCase());
        }
        return sharedPreference.getUUID().toUpperCase();
    }
}
