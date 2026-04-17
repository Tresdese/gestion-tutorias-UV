/*
 * Autor:
 * Ultima modificación hecha por:
 * Versión:
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.CarreraDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutoradoDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Carrera;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutorado;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class FXMLEditarTutoradoController implements Initializable {
    private final Logger LOGGER = LogManager.getLogger(FXMLEditarTutoradoController.class);

    @FXML
    private TextField txtMatricula;
    @FXML
    private TextField txtNombres;
    @FXML
    private TextField txtApellidoPaterno;
    @FXML
    private TextField txtApellidoMaterno;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtSemestre;
    @FXML
    private ComboBox<String> cbProgramaEducativo;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;

    private CarreraDAO carreraDAO = new CarreraDAO();
    private TutoradoDAO tutoradoDAO = new TutoradoDAO();
    private List<Carrera> carreras = new ArrayList<>();
    private Tutorado tutoradoOriginal;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        llenarComboBoxCarreras();
    }

    public void initData(Tutorado tutorado) {
        this.tutoradoOriginal = tutorado;
        txtMatricula.setText(tutorado.getMatricula());
        txtMatricula.setEditable(false);
        txtNombres.setText(tutorado.getNombre());
        txtApellidoPaterno.setText(tutorado.getApellidoPaterno());
        txtApellidoMaterno.setText(tutorado.getApellidoMaterno());
        txtCorreo.setText(tutorado.getCorreo());
        txtSemestre.setText(String.valueOf(tutorado.getSemestre()));

        for (int i = 0; i < carreras.size(); i++) {
            if (carreras.get(i).getIdCarrera() == tutorado.getIdCarrera()) {
                cbProgramaEducativo.getSelectionModel().select(i);
                break;
            }
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (validarCampos()) {
            actualizarTutorado(event);
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        Utilidades.cerrarVentana(event);
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        Utilidades.cerrarVentana(event);
    }

    private void llenarComboBoxCarreras() {
        try {
            carreras = carreraDAO.obtenerTodasCarreras();
            for (Carrera carrera : carreras) {
                cbProgramaEducativo.getItems().add(carrera.getNombre());
            }
        } catch (SQLException ex) {
            manejarError("Error al obtener carreras de la base de datos", ex, "Error de base de datos",
                    "No se pudieron cargar las carreras. Intenta más tarde.");
        } catch (Exception e) {
            manejarError("Error inesperado al obtener carreras", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar las carreras.");
        }
    }

    private void actualizarTutorado(ActionEvent event) {
        try {
            int indiceCarrera = cbProgramaEducativo.getSelectionModel().getSelectedIndex();
            Tutorado tutorado = new Tutorado();
            tutorado.setMatricula(tutoradoOriginal.getMatricula());
            tutorado.setNombre(txtNombres.getText());
            tutorado.setApellidoPaterno(txtApellidoPaterno.getText());
            tutorado.setApellidoMaterno(txtApellidoMaterno.getText());
            tutorado.setCorreo(txtCorreo.getText());
            tutorado.setSemestre(Integer.parseInt(txtSemestre.getText()));
            tutorado.setIdCarrera(carreras.get(indiceCarrera).getIdCarrera());
            tutorado.setActivo(tutoradoOriginal.isActivo());
            tutorado.setIdTutor(tutoradoOriginal.getIdTutor());

            boolean actualizado = tutoradoDAO.updateTutorado(tutorado);
            if (actualizado) {
                LOGGER.info("Tutorado actualizado exitosamente: {}", tutorado.getMatricula());
                Utilidades.mostrarAlertaSimple("Actualización exitosa",
                        "El tutorado ha sido actualizado correctamente",
                        Alert.AlertType.INFORMATION);
                Utilidades.cerrarVentana(event);
            } else {
                LOGGER.error("Error al actualizar el tutorado: {}", tutorado.getMatricula());
                Utilidades.mostrarAlertaSimple("Error al actualizar",
                        "No se pudo actualizar al tutorado",
                        Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            Utilidades.mostrarAlertaSimple("Dato inválido", "El semestre debe ser un número entero.",
                    Alert.AlertType.WARNING);
        } catch (SQLException ex) {
            manejarError("Error al actualizar tutorado en la base de datos", ex, "Error de base de datos",
                    "No se pudo actualizar al tutorado. Intenta nuevamente.");
        } catch (Exception e) {
            manejarError("Error inesperado al actualizar tutorado", e, "Error inesperado",
                    "Ocurrió un error inesperado al actualizar al tutorado.");
        }
    }

    private boolean validarCampos() {
        if (txtNombres.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El nombre es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtApellidoPaterno.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El apellido paterno es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtApellidoMaterno.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El apellido materno es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtCorreo.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El correo es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtSemestre.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El semestre es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (cbProgramaEducativo.getSelectionModel().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El programa educativo es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    private void manejarError(String mensajeLog, Exception excepcion, String titulo, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, titulo, mensajeUsuario);
    }
}
