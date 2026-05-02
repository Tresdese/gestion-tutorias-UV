/*
 * Autor: Fidel Cruz Reyes
 * Ultima modificación hecha por: Fidel Cruz Reyes
 * Versión: 1.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.net.URL;
import java.util.ResourceBundle;

import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class FXMLDetallesProblematicaController implements Initializable {

    @FXML
    private Label lbTitulo;
    @FXML
    private Label lbFecha;
    @FXML
    private Label lbEstatus;
    @FXML
    private TextArea txtaDescripcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void inicializarValores(Problematica problematica) {
        lbTitulo.setText(problematica.getTitulo() != null ? problematica.getTitulo() : "—");
        lbFecha.setText(problematica.getFecha() != null ? problematica.getFecha().toString() : "—");
        lbEstatus.setText(problematica.getEstatus() != null ? problematica.getEstatus() : "—");
        txtaDescripcion.setText(problematica.getDescripcion() != null ? problematica.getDescripcion() : "");
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
        Stage stage = (Stage) lbTitulo.getScene().getWindow();
        stage.close();
    }
}
