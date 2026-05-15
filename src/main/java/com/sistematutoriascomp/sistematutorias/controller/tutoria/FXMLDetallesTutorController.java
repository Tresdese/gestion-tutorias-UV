package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.net.URL;
import java.util.ResourceBundle;

import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXMLDetallesTutorController implements Initializable {

    @FXML
    private Label lbNumeroPersonal;
    @FXML
    private Label lbCargo;
    @FXML
    private Label lbHorarioLaboral;
    @FXML
    private Label lbCorreo;
    @FXML
    private Label lbCorreoAlternativo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void inicializarValores(Tutor tutor) {
        lbNumeroPersonal.setText(tutor.getNumeroDePersonal() != null ? tutor.getNumeroDePersonal() : "—");
        lbCargo.setText("N/A");
        lbHorarioLaboral.setText("N/A");
        lbCorreo.setText(tutor.getCorreo() != null ? tutor.getCorreo() : "—");
        lbCorreoAlternativo.setText("N/A");
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
        Stage stage = (Stage) lbNumeroPersonal.getScene().getWindow();
        stage.close();
    }
}
