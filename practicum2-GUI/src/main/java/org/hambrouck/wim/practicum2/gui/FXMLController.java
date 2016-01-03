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

    private void maakAlert(String message, String title, Alert.AlertType alertType)
    {
        Alert alert = new Alert(alertType, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.showAndWait();
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
                
                maakAlert(String.format("Bestand niet gevonden of geen schrijfrechten op uitvoerbestand (%s)", e.getClass()), "Probleem", Alert.AlertType.ERROR);
                return;
            } catch (Exception e) {
                e.printStackTrace();

                maakAlert(String.format("Fout: %s (%s)", e.getMessage(), e.getClass()), "Probleem", Alert.AlertType.ERROR);
                return;
            }
            maakAlert("Klaar!", "Encryptor 3000", Alert.AlertType.INFORMATION);
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
                maakAlert(String.format("Ongeldig wachtwoord of integriteitsfout bestand (%s)", e.getClass()), "Probleem", Alert.AlertType.ERROR);
                return;
            }
            catch (Exception e) {
                e.printStackTrace();
                maakAlert(String.format("Fout: %s (%s)", e.getMessage(), e.getClass()), "Probleem", Alert.AlertType.ERROR);
                return;
            }
            
            maakAlert("Klaar!", "Encryptor 3000", Alert.AlertType.INFORMATION);
        }
    }

    private boolean checkFields(boolean wachtwoordControle)
    {
        if(txt_invoer.getText().isEmpty())
        {
            maakAlert("Gelieve een invoerbestand op te geven.", "Probleem", Alert.AlertType.ERROR);
            txt_invoer.requestFocus();
            return false;
        } else if(txt_uitvoer.getText().isEmpty())
        {
            maakAlert("Gelieve een uitvoerbestand op te geven.", "Probleem", Alert.AlertType.ERROR);
            txt_uitvoer.requestFocus();
            return false;
        } else if(txt_wachtwoord.getText().isEmpty())
        {
            maakAlert("Gelieve een wachtwoord op te geven.", "Probleem", Alert.AlertType.ERROR);
            txt_wachtwoord.requestFocus();
            return false;
        } else if(wachtwoordControle && txt_wachtwoord_herhaald.getText().isEmpty())
        {
            maakAlert("Gelieve je wachtwoord opnieuw op te geven.", "Probleem", Alert.AlertType.ERROR);
            txt_wachtwoord_herhaald.requestFocus();
            return false;
        } else if(wachtwoordControle && (!txt_wachtwoord.getText().equals(txt_wachtwoord_herhaald.getText())))
        {
            maakAlert("Wachtwoorden komen niet overeen!", "Probleem", Alert.AlertType.ERROR);
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
