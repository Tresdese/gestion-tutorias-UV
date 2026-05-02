/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.ProblematicaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FXMLRegistrarProblematicaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLRegistrarProblematicaController.class);

    @FXML
    private TextField txtTitulo;
    @FXML
    private TextArea txtaDescripcion;
    @FXML
    private Label lbNombreAlumno;
    private int idTutoria;
    private int idTutorado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtTitulo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 120) {
                txtTitulo.setText(oldValue);
            }
        });
    }

    public void inicializarValores(int idTutoria, int idTutorado, String nombreAlumno) {
        this.idTutoria = idTutoria;
        this.idTutorado = idTutorado;
        this.lbNombreAlumno.setText(nombreAlumno);
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        String titulo = txtTitulo.getText() != null ? txtTitulo.getText().trim() : "";
        String descripcion = txtaDescripcion.getText() != null ? txtaDescripcion.getText().trim() : "";
        if (titulo.isEmpty() || descripcion.isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "Por favor llena todos los campos.", Alert.AlertType.WARNING);
            return;
        } else if (titulo.length() > 120) {
            Utilidades.mostrarAlertaSimple("Título demasiado largo",
                    "El título no puede exceder los 120 caracteres. Actualmente tiene " + titulo.length() + ".",
                    Alert.AlertType.WARNING);
            return;
        } else if (descripcion.length() > 500) {
            Utilidades.mostrarAlertaSimple("Descripción demasiado larga",
                    "La descripción no puede exceder los 500 caracteres. Actualmente tiene " + descripcion.length() + ".",
                    Alert.AlertType.WARNING);
            return;
        }
        Problematica nuevaProblematica = prepararProblematica(titulo, descripcion);
        enviarAlDominio(nuevaProblematica);
    }

    private Problematica prepararProblematica(String titulo, String descripcion) {
        Problematica p = new Problematica();
        p.setIdTutoria(this.idTutoria);
        p.setIdTutorado(this.idTutorado);
        p.setTitulo(titulo);
        p.setDescripcion(descripcion);
        p.setFecha(LocalDate.now());
        return p;
    }

    private void enviarAlDominio(Problematica problematica) {
        HashMap<String, Object> respuesta = ProblematicaImp.registrarProblematica(problematica);

        if (!(boolean) respuesta.get("error")) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Éxito");
            alerta.setHeaderText(null);
            alerta.setContentText((String) respuesta.get("mensaje"));
            ButtonType btnContinuar = new ButtonType("Continuar");
            alerta.getButtonTypes().setAll(btnContinuar);
            alerta.showAndWait();
            cerrarVentana();
        } else {
            LOGGER.error("Error al registrar problemática: " + respuesta.get("mensaje"));
            Utilidades.mostrarAlertaSimple("Error", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Estás seguro que quieres cancelar la acción?");
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                cerrarVentana();
            }
        });
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtTitulo.getScene().getWindow();
        stage.close();
    }
}
