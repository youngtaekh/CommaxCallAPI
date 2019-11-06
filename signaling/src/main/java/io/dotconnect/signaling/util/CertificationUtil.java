package io.dotconnect.signaling.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static io.dotconnect.api.util.Configuration.APP_NAME;

public class CertificationUtil {

    public static void copyAssets(Context context) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e(APP_NAME, "Failed to get asset file list.");
        }
        if(files != null) {
            for (String filename : files) {
                if (filename.contains("root_cert_comodo")) {
                    InputStream in = null;
                    OutputStream outputStream = null;
                    try {
                        in = assetManager.open(filename);

                        File outFile = new File(context.getFilesDir(), filename);

                        if (!outFile.exists()) {
                            outputStream = new FileOutputStream(outFile);
                            copyFile(in, outputStream);
                            in.close();
                            in = null;
                            outputStream.flush();
                            outputStream.close();
                            outputStream = null;
                        }
                    } catch (IOException e) {
                        Log.e(APP_NAME, "Failed to copy asset file: " + filename);
                    }
                }
            }
        }
    }

    public static void copyPEMFile(String domain, String outboundProxyAddress, Context context) {
        try {

            String certificateFileName = "domain_cert_" + domain + ".pem";
            String privateKeyFileName = "domain_key_" + domain + ".pem";
            String certificateFileName2 = "domain_cert_*." + domain + ".pem";
            String privateKeyFileName2 = "domain_key_*." + domain + ".pem";
            String certificateFileName3 = "domain_cert_" + outboundProxyAddress + ".pem";
            String privateKeyFileName3 = "domain_key_" + outboundProxyAddress + ".pem";
            File certificateFile = new File(context.getFilesDir(), certificateFileName);
            File privateKeyFile = new File(context.getFilesDir(), privateKeyFileName);
            File certificateFile2 = new File(context.getFilesDir(), certificateFileName2);
            File privateKeyFile2 = new File(context.getFilesDir(), privateKeyFileName2);
            File certificateFile3 = new File(context.getFilesDir(), certificateFileName3);
            File privateKeyFile3 = new File(context.getFilesDir(), privateKeyFileName3);

            InputStream in = new FileInputStream(certificateFile);
            OutputStream out = new FileOutputStream(certificateFile2);

            copyFile(in, out);
            in.close();
            out.close();

            in = new FileInputStream(certificateFile);
            out = new FileOutputStream(certificateFile3);

            copyFile(in, out);
            in.close();
            out.close();

            in = new FileInputStream(privateKeyFile);
            out = new FileOutputStream(privateKeyFile2);

            copyFile(in, out);
            in.close();
            out.close();

            in = new FileInputStream(privateKeyFile);
            out = new FileOutputStream(privateKeyFile3);

            copyFile(in, out);
            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }

    public static String makeSHA256(String str){
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
}
