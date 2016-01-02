package org.hambrouck.wim.practicum2.library;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.security.*;
import java.util.Arrays;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 *
 * @author Wim Hambrouck
 */
public class BeveiligingsBibliotheek {
    private static Key geefSleutelVoorWachtwoord(String wachtwoord) throws Exception {
        final int iteraties = 1000;
        final int sleutelLengte = 128;

        char[] caWachtwoord = wachtwoord.toCharArray();
        byte[] zout = "teVeelZouKanNefastZijnVoorDeGezondheid".getBytes();

        PBEKeySpec pbeKeySpec = new PBEKeySpec(caWachtwoord, zout, iteraties, sleutelLengte);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

        Key key = secretKeyFactory.generateSecret(pbeKeySpec);

        return new SecretKeySpec(key.getEncoded(), "AES"); //om het te laten werken met AES...
    }


    private static Cipher geefCypher(Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher;
    }

    private static byte[] geefMac(Key key, byte[] cipherText) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(key);
        return mac.doFinal(cipherText);
    }

    public static void versleutelBestand(String invoerPad, String uitvoerPad, String wachtwoord) throws Exception {
        //bestand omzetten naar byte array
        byte[] bestandBytes;
        Path inPath = Paths.get(invoerPad);
        bestandBytes = Files.readAllBytes(inPath);

        Key key = geefSleutelVoorWachtwoord(wachtwoord);

        //cipher
        Cipher cipher = geefCypher(key);

        //bytes versleutelen
        byte[] cipherText = cipher.doFinal(bestandBytes);

        //initialisatievector
        byte[] iv = cipher.getIV();

        //hmac
        byte[] hmac = geefMac(key, cipherText);

        //alles wegschrijven
        FileOutputStream uitvoerStroompje = new FileOutputStream(uitvoerPad);
        uitvoerStroompje.write(hmac);
        uitvoerStroompje.write(iv);
        uitvoerStroompje.write(cipherText);
        uitvoerStroompje.close();
    }

    public static void ontsleutelBestand(String invoerPad, String uitvoerPad, String wachtwoord) throws Exception, AuthorizationException {
        //bestand omzetten naar byte array
        byte[] bestandBytes;
        Path inPath = Paths.get(invoerPad);
        bestandBytes = Files.readAllBytes(inPath);

        Key key = geefSleutelVoorWachtwoord(wachtwoord);

        //iv uit bestand halen == vanaf 20 (hmac overslaan), 16 bytes lang
        byte[] iv = Arrays.copyOfRange(bestandBytes, 20, 36);


        byte[] ontcijferdeInhoud = new byte[0];
        //integriteitscheck
        if(controleerBestand(bestandBytes, key))
        {
            //integriteit klopt, ontcijferen maar!
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
            ontcijferdeInhoud = cipher.doFinal(haalEffectieveInhoudUitBestand(bestandBytes));
        } else {
            //mislukt
            throw new AuthorizationException();
        }


        //alles wegschrijven
        FileOutputStream uitvoerStroompje = new FileOutputStream(uitvoerPad);
        uitvoerStroompje.write(ontcijferdeInhoud);
        uitvoerStroompje.close();
    }

    private static boolean controleerBestand(byte[] bestandBytes, Key key) throws Exception {
        //mac uit bestand halen == de eerste 20 bytes
        byte[] mac = Arrays.copyOfRange(bestandBytes, 0, 20);

        //hmac ophalen
        byte[] hmac = geefMac(key, haalEffectieveInhoudUitBestand(bestandBytes));

        return Arrays.equals(hmac, mac); // hmac.equals(mac);
    }

    private static byte[] haalEffectieveInhoudUitBestand(byte[] bestandBytes)
    {
        //de effective inhoud (start vanaf 20 + 16 = 32)
       return Arrays.copyOfRange(bestandBytes, 36, bestandBytes.length);
    }
}
