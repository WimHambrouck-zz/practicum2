package org.hambrouck.wim.practicum2.library;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;


/**
 *
 * @author Wim Hambrouck
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BeveiligingsBibliotheekTest {
    
    private final String invoer;
    private final String uitvoer_versleuteld;
    private final String uitvoer_ontsleuteld;
    private final String wachtwoord;
    private final String inhoud;
    private final Logger logger;
    
    public BeveiligingsBibliotheekTest() {
        logger = Logger.getLogger("TestLogger");

        invoer = "test.txt";
        uitvoer_versleuteld = "test_encrypted.txt";
        uitvoer_ontsleuteld = "test_decrypted.txt";
        wachtwoord = "correct horse battery staple"; //cfr: https://xkcd.com/936/
        inhoud = "Dit is een testbestand, om te testen...";
    }
    
    @Before
    public void setUp() {
        logger.log(Level.INFO, "Testbestanden aanmaken..");
        //testbestand aanmaken
        PrintWriter uitvoerBestand = null;
        try {
            uitvoerBestand = new PrintWriter(invoer);
            uitvoerBestand.print(inhoud);
        } catch (FileNotFoundException e) {
            fail("Testbestand (test.txt) niet gevonden");
        } finally {
            uitvoerBestand.close();
        }

        //testbestand versleutelen
        Path inPath = Paths.get(uitvoer_versleuteld);

        try
        {
            BeveiligingsBibliotheek.versleutelBestand(invoer, uitvoer_versleuteld, wachtwoord);

            byte[] bestandBytes;

            bestandBytes = Files.readAllBytes(inPath);


            //bytes encripteren
            //byte[] cipherText = BeveiligingsBibliotheek.geefCypher(key).doFinal(bestandBytes);

            //assertArrayEquals(bestandBytes, cipherText);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @After
    public void tearDown() {
        //testbestand en uitgevoerde bestanden verwijderen
        logger.log(Level.INFO, "Testbestand en uitgevoerde bestanden verwijderen...");
        Path in = Paths.get(invoer);
        Path uitvoer1 = Paths.get(uitvoer_versleuteld);
        Path uitvoer2 = Paths.get(uitvoer_ontsleuteld);

        try {
            Files.delete(in);
            Files.delete(uitvoer1);
            Files.delete(uitvoer2);
        } catch (IOException e) {
            //kan zijn dat sommige niet zijn aangemaakt, maar hezien dit niet het punt van de oefening is, maakt het niet te veel uit, denk ik zo
        }

    }

    @Test
    public void testOntsleutelBestand() {
        logger.log(Level.INFO, "Test ontsleutel bestand");
        try {
            BeveiligingsBibliotheek.ontsleutelBestand(uitvoer_versleuteld, uitvoer_ontsleuteld, wachtwoord);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //uitvoerbestand inlezen
        byte[] bestandBytes;
        String bestandInhoud;
        Path inPath = Paths.get(uitvoer_ontsleuteld);
        try {
            bestandBytes = Files.readAllBytes(inPath);
            //inhoud omzetten naar String
            bestandInhoud = new String(bestandBytes, Charset.defaultCharset());
            assertEquals(inhoud, bestandInhoud);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Test (expected = AuthorizationException.class)
    public void testOngeldigWachtwoord() throws Exception {
        logger.log(Level.INFO, "Test met ongeldig wachtwoord");

        BeveiligingsBibliotheek.ontsleutelBestand(uitvoer_versleuteld, uitvoer_ontsleuteld, String.format("dit is het originele wachtwoord: %s, met wat extra tekst", wachtwoord));
    }

    @Test (expected = AuthorizationException.class)
    public void testBestandsIntegriteit() throws Exception {
        //Path inPath = Paths.get(uitvoer_versleuteld);
        try {
            FileOutputStream uitvoerBestand = new FileOutputStream(uitvoer_versleuteld, true);
            uitvoerBestand.write("een beetje extra info die de intergriteit van het bestand onzeep helpt".getBytes());
            uitvoerBestand.close();
        } catch (Exception e){
            e.printStackTrace();
        }

        try {
            BeveiligingsBibliotheek.ontsleutelBestand(uitvoer_versleuteld, uitvoer_ontsleuteld, wachtwoord);
        } catch (Exception e) {
            throw e;
        }


    }
}
