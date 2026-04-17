/*
 * Autor:
 * Ultima modificación hecha por:
 * Versión:
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.TutoradoDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutorado;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLMisTutoradosController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLMisTutoradosController.class);

    @FXML private Label lblTitulo;
    @FXML private Label lblSubtitulo;
    @FXML private TableView<Tutorado> tblTutorados;
    @FXML private TableColumn<Tutorado, String> tcMatricula;
    @FXML private TableColumn<Tutorado, String> tcNombre;
    @FXML private TableColumn<Tutorado, String> tcApellidoPaterno;
    @FXML private TableColumn<Tutorado, String> tcApellidoMaterno;
    @FXML private TableColumn<Tutorado, Integer> tcSemestre;
    @FXML private TableColumn<Tutorado, Boolean> tcEstado;

    private TutoradoDAO tutoradoDAO = new TutoradoDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarTutorados();
        mostrarNombreTutor();
    }

    private void configurarColumnas() {
        tcMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tcApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        tcApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        tcSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        tcEstado.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tcEstado.setCellFactory(col -> new TableCell<Tutorado, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                    setStyle("");
                } else if (activo) {
                    setText("Activo");
                    setStyle("-fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-alignment: CENTER;");
                } else {
                    setText("Inactivo");
                    setStyle("-fx-text-fill: #C62828; -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });
    }

    private void cargarTutorados() {
        int idTutor = Sesion.getTutorSesion().getIdTutor();
        try {
            List<Tutorado> tutorados = tutoradoDAO.obtenerTutoradosPorTutor(idTutor);
            tblTutorados.setItems(FXCollections.observableArrayList(tutorados));
            if (tutorados.isEmpty()) {
                lblSubtitulo.setText("No tiene tutorados asignados por el momento.");
            } else {
                lblSubtitulo.setText("Tiene " + tutorados.size() + " tutorado(s) asignado(s).");
            }
        } catch (SQLException ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error al cargar tutorados del tutor " + idTutor, ex,
                    "Error de base de datos", "No se pudo cargar la lista de tutorados.");
        } catch (Exception e) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error inesperado al cargar tutorados", e,
                    "Error inesperado", "Ocurrió un error inesperado al cargar los tutorados.");
        }
    }

    private void mostrarNombreTutor() {
        String nombre = Sesion.getTutorSesion().getNombre();
        String apellido = Sesion.getTutorSesion().getApellidoPaterno();
        lblTitulo.setText("Mis Tutorados — " + nombre + " " + apellido);
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarTutorias(event);
        } catch (IOException ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error al volver al menú", ex,
                    "Error", "No se pudo volver al menú de gestión.");
        } catch (Exception e) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error inesperado al volver", e,
                    "Error inesperado", "Ocurrió un error inesperado.");
        }
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        Sesion.cerrarSesion();
        try {
            Utilidades.clicCerrarSesion(event);
        } catch (IOException ex) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error al cerrar sesión", ex,
                    "Error", "No se pudo cerrar la sesión.");
        } catch (Exception e) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error inesperado al cerrar sesión", e,
                    "Error inesperado", "Ocurrió un error inesperado al cerrar la sesión.");
        }
    }
}
