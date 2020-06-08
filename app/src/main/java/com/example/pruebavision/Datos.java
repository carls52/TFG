package com.example.pruebavision;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Base64;

import androidx.core.content.ContextCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Datos {

    private Context contexto;


    private static final int pswdIterations = 10;
    private static final int keySize = 256;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";

  private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
//    private static final String plainText = "sampleText";
    private static final String plainText = "contraseña";
//    private static final String AESSalt = "exampleSalt";
//    private static final String initializationVector = "8119745113154120";
    private static final String AESSalt = "F291CFB0E014EB9D";
    private static final String initializationVector = "67F49406C90CCA3E";



    public Datos(Context contexto)
    {
        this.contexto = contexto;
    }

    public String leerEncriptado(String key) throws Exception {
        SharedPreferences sharedPreferences = null;
        String encriptar = null;
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

            try {
                encriptar = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sharedPreferences = EncryptedSharedPreferences.create(
                        "Datos Encriptados",
                        encriptar,
                        contexto,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String nombre = sharedPreferences.getString(key, "");
            return nombre;
        }
        else
        {
            sharedPreferences = contexto.getSharedPreferences("Datos",Context.MODE_PRIVATE);
            String encriptado = sharedPreferences.getString(key,"");
            encriptar = decrypt(encriptado);
            return encriptar;
        }
    }
    public void encriptar(String key, String valor) throws Exception {
        SharedPreferences sharedPreferences = null;
        String encriptar = null;
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

            try {
                encriptar = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            sharedPreferences = null;
            try {
                sharedPreferences = EncryptedSharedPreferences.create(
                        "Datos Encriptados",
                        encriptar,
                        contexto,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sharedPreferences.edit().putString(key, valor).commit();
            //  sharedPreferences.edit().putString("Contraseña",contraseña).apply();
        }
        else
        {
            encriptar = encrypt(valor);
            sharedPreferences = contexto.getSharedPreferences("Datos",Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(key,encriptar).commit();
        }
    }
    public Usuario getUsuario() throws Exception {
            String u = leerEncriptado("Usuario");
            Gson gson = new Gson();
            Usuario user = gson.fromJson(u, Usuario.class);
            return user;

    }
    public void setUsuario(Usuario user) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        encriptar("Usuario",json);
    }

    public static String encrypt(String textToEncrypt) throws Exception
    {
        SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText, AESSalt), "AES");
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] encrypted = cipher.doFinal(textToEncrypt.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    // TODO Decryption of Encrypted Message
    public static String decrypt(String textToDecrypt) throws Exception
    {
        byte[] encryted_bytes = Base64.decode(textToDecrypt, Base64.DEFAULT);
        SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText, AESSalt), "AES");
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] decrypted = cipher.doFinal(encryted_bytes);
        return new String(decrypted, "UTF-8");
    }

    // TODO For Generate Secret Key For Encryption and Decryption of Message
    private static byte[] getRaw(String plainText, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyInstance);
            KeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt.getBytes(), pswdIterations, keySize);
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
    public boolean checkPer(String p,Context context)
    {
        int check = ContextCompat.checkSelfPermission(context,p);
        return check == PackageManager.PERMISSION_GRANTED;
    }
    public void setAjustes(String key, String valor)
    {
        SharedPreferences ajustes = contexto.getSharedPreferences("Ajustes",Context.MODE_PRIVATE);
        ajustes.edit().putString(key,valor).commit();
    }
    public String getAjustes(String key)
    {
        SharedPreferences ajustes = contexto.getSharedPreferences("Ajustes",Context.MODE_PRIVATE);
        return ajustes.getString(key,"");
    }
}
//salt=F291CFB0E014EB9D
//        key=1254BA8895753173D7658DA46483BADB5F0880220D03A2B7
//        iv =67F49406C90CCA3E6F198030B70149B8