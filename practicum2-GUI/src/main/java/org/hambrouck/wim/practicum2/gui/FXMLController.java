package org.hambrouck.wim.practicum2.gui;

import java.io.File;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.hambrouck.wim.practicum2.library.AuthorizationException;
import org.hambrouck.wim.practicum2.library.BeveiligingsBibliotheek;


import javax.naming.AuthenticationException;
import javax.swing.*;

public class FXMLController implements Initializable {


    @FXML
    private Parent hoofdscherm;
    @FXML
    private TextField txt_invoer;
    @FXML
    private TextField txt_uitvoer;
    @FXML
    private PasswordField txt_wachtwoord;
    @FXML
    private PasswordField txt_wachtwoord_herhaald;
    @FXML
    private Button btn_kiesInvoer;
    @FXML
    private Button btn_kiesUitvoer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    private void maakError(String Message, String Title, String HeaderText)
    {
        Alert alert = new Alert()
    }

    @FXML
    private void encodeer(ActionEvent event) {
        if(checkFields(true)) {
            String invoer = txt_invoer.getText();
            String uitvoer = txt_uitvoer.getText();
            String wachtwoord = txt_wachtwoord.getText();


            try {
                BeveiligingsBibliotheek.versleutelBestand(invoer, uitvoer, wachtwoord);
            } catch (AccessDeniedException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, String.format("Bestand niet gevonden of geen schrijfrechten op uitvoerbestand (%s)", e.getClass()), ButtonType.OK);
                alert.setTitle("Probleem");
                alert.setHeaderText("Er is iets fout gegaan!");
                alert.showAndWait();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, String.format("Fout: %s (%s)", e.getMessage(), e.getClass()), ButtonType.OK);
                alert.setTitle("Probleem");
                alert.setHeaderText("Er is iets fout gegaan!");
                alert.showAndWait();
                return;
            }
            JOptionPane.showMessageDialog(null, "Klaar!", "Encryptor 3000", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @FXML
    private void decodeer(ActionEvent event) {
        if(checkFields(false))
        {
            String invoer = txt_invoer.getText();
            String uitvoer = txt_uitvoer.getText();
            String wachtwoord = txt_wachtwoord.getText();

            try {
                BeveiligingsBibliotheek.ontsleutelBestand(invoer, uitvoer, wachtwoord);
            }catch (AuthorizationException e)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, String.format("Ongeldig wachtwoord of integriteitsfout bestand (%s)", e.getClass()), ButtonType.OK);
                alert.setTitle("Probleem");
                alert.setHeaderText("Er is iets fout gegaan!");
                alert.showAndWait();
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, String.format("Fout: %s (%s)", e.getMessage(), e.getClass()), ButtonType.OK);
                alert.setTitle("Probleem");
                alert.setHeaderText("Er is iets fout gegaan!");
                alert.showAndWait();
                return;
            }
            JOptionPane.showMessageDialog(null, "Klaar!", "Encryptor 3000", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean checkFields(boolean wachtwoordControle)
    {
        if(txt_invoer.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Gelieve een invoerbestand op te geven.", "Probleem", JOptionPane.ERROR_MESSAGE);
            txt_invoer.requestFocus();
            return false;
        } else if(txt_uitvoer.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Gelieve een uitvoerbestand op te geven.", "Probleem", JOptionPane.ERROR_MESSAGE);
            txt_uitvoer.requestFocus();
            return false;
        } else if(txt_wachtwoord.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Gelieve een wachtwoord op te geven.", "Probleem", JOptionPane.ERROR_MESSAGE);
            txt_wachtwoord.requestFocus();
            return false;
        } else if(wachtwoordControle && txt_wachtwoord_herhaald.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(null, "Gelieve je wachtwoord opnieuw op te geven.", "Probleem", JOptionPane.ERROR_MESSAGE);
            txt_wachtwoord_herhaald.requestFocus();
            return false;
        } else if(wachtwoordControle && (!txt_wachtwoord.getText().equals(txt_wachtwoord_herhaald.getText())))
        {
            JOptionPane.showMessageDialog(null, "Wachtwoorden komen niet overeen!", "Probleem", JOptionPane.ERROR_MESSAGE);
            txt_wachtwoord.requestFocus();
            return false;
        }
        return true;
    }

    @FXML
    private void kiesInvoer(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Kies invoerbestand");

        File invoerBestand = fileChooser.showOpenDialog(hoofdscherm.getScene().getWindow());

        if(invoerBestand != null)
        {
            String invoerPad = invoerBestand.getAbsolutePath();
            String uitvoerExt = invoerPad;
            uitvoerExt = uitvoerExt.replace("\\", "/");
            uitvoerExt = invoerPad.substring(uitvoerExt.lastIndexOf("/"));
            uitvoerExt = uitvoerExt.substring(uitvoerExt.indexOf("."));


            txt_invoer.setText(invoerPad);
            txt_uitvoer.setText(String.format("%s_uitvoer%s", invoerPad.substring(0, invoerPad.indexOf(uitvoerExt)), uitvoerExt));
        }
    }

    @FXML
    private void kiesUitvoer(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Kies uitvoerbestand");

        File invoerBestand = fileChooser.showOpenDialog(hoofdscherm.getScene().getWindow());

        if(invoerBestand != null)
        {
            txt_uitvoer.setText(invoerBestand.getAbsolutePath());
        }
    }
}
