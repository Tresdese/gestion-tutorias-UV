/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 6.0
 */
package com.sistematutoriascomp.sistematutorias.controller.reporte;

import com.sistematutoriascomp.sistematutorias.dominio.ReporteGeneralImp;
import com.sistematutoriascomp.sistematutorias.model.dao.PeriodoDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.FechaTutoria;
import com.sistematutoriascomp.sistematutorias.model.pojo.Periodo;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteGeneral;
import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class FXMLGenerarReporteGeneralController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLGenerarReporteGeneralController.class);

    @FXML
    private ComboBox<Periodo> cbPeriodo;
    @FXML
    private ComboBox<FechaTutoria> cbNumeroSesion;
    @FXML
    private Button btnGenerar;
    @FXML
    private Button btnVolver;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnFinalizar;
    @FXML
    private HBox hbBotonesEdicion;
    @FXML
    private VBox vbDatosReporte;
    @FXML
    private Label lbTotalTutorados;
    @FXML
    private Label lbTotalAsistentes;
    @FXML
    private Label lbTotalInasistentes;
    @FXML
    private Label lbTotalProblematicas;
    @FXML
    private TableView<Problematica> tblProblematicas;
    @FXML
    private TableColumn<Problematica, String> tcTitulo;
    @FXML
    private TableColumn<Problematica, String> tcDescripcion;
    @FXML
    private TextArea txtaComentariosGenerales;
    
    private ReporteGeneral reporteCalculado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBoxes();
        configurarTabla();

        vbDatosReporte.setVisible(false);
        vbDatosReporte.setManaged(false);

        btnFinalizar.setVisible(false);
        btnFinalizar.setManaged(false);
    }

    private void configurarTabla() {
        tcTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tcDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    private void configurarComboBoxes() {
        cargarPeriodos();

        cbPeriodo.valueProperty().addListener((obs, oldVal, newVal) -> {
            cbNumeroSesion.setItems(FXCollections.observableArrayList());
            if (newVal != null) {
                cargarSesiones(newVal.getIdPeriodo());
            }
        });

        cbPeriodo.setConverter(new StringConverter<Periodo>() {
            @Override
            public String toString(Periodo periodo) {
                return periodo == null ? null : periodo.getNombre();
            }

            @Override
            public Periodo fromString(String string) {
                return null;
            }
        });

        cbNumeroSesion.setConverter(new StringConverter<FechaTutoria>() {
            @Override
            public String toString(FechaTutoria object) {
                return object == null ? null : object.toString();
            }

            @Override
            public FechaTutoria fromString(String string) {
                return null;
            }
        });
    }

    private void cargarPeriodos() {
        try {
            List<Periodo> lista = PeriodoDAO.obtenerTodosPeriodos();
            cbPeriodo.setItems(FXCollections.observableArrayList(lista));
        } catch (SQLException e) {
            Utilidades.manejarErrorTecnico(LOGGER, "Error al cargar periodos.", e, "Error",
                    "No se pudieron cargar los periodos. Intenta más tarde.");
        }
    }

    private void cargarSesiones(int idPeriodo) {
        HashMap<String, Object> respuesta = ReporteGeneralImp.obtenerSesionesPorPeriodo(idPeriodo);
        if (!(boolean) respuesta.get("error")) {
            List<FechaTutoria> lista = (List<FechaTutoria>) respuesta.get("sesiones");
            cbNumeroSesion.setItems(FXCollections.observableArrayList(lista));
        } else {
            Utilidades.mostrarAlertaSimple("Error", "Error al cargar sesiones.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicGenerar(ActionEvent event) {
        Periodo periodoSeleccionado = cbPeriodo.getValue();
        FechaTutoria sesionSeleccionada = cbNumeroSesion.getValue();

        if (periodoSeleccionado == null || sesionSeleccionada == null) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "Por favor selecciona un periodo escolar y una sesión.",
                    Alert.AlertType.WARNING);
            return;
        }

        HashMap<String, Object> respuesta = ReporteGeneralImp.calcularDatosReporte(
                periodoSeleccionado.getIdPeriodo(),
                sesionSeleccionada.getIdFechaTutoria());

        if (!(boolean) respuesta.get("error")) {
            reporteCalculado = (ReporteGeneral) respuesta.get("reporte");

            lbTotalTutorados.setText(String.valueOf(reporteCalculado.getTotalTutorados()));
            lbTotalAsistentes.setText(String.valueOf(reporteCalculado.getTotalAsistentes()));
            lbTotalInasistentes.setText(String.valueOf(reporteCalculado.getTotalFaltantes()));
            lbTotalProblematicas.setText(String.valueOf(reporteCalculado.getTotalProblematicas()));

            List<Problematica> listaProbs = (List<Problematica>) respuesta.get("listaProblematicas");
            tblProblematicas.setItems(FXCollections.observableArrayList(listaProbs));

            vbDatosReporte.setVisible(true);
            vbDatosReporte.setManaged(true);

            cbPeriodo.setDisable(true);
            cbNumeroSesion.setDisable(true);
            btnGenerar.setDisable(true);
            btnVolver.setDisable(true);

        } else {
            Utilidades.mostrarAlertaSimple("Error", "Error al generar reporte.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        String comentarios = txtaComentariosGenerales.getText();

        if (comentarios == null || comentarios.trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos requeridos", "Es necesario agregar observaciones generales.",
                    Alert.AlertType.WARNING);
            return;
        }

        if (reporteCalculado != null) {
            reporteCalculado.setObservaciones(comentarios);
            reporteCalculado.setFechaGeneracion(LocalDateTime.now());
            reporteCalculado.setEstado("Pendiente");

            if (Sesion.getTutorSesion() != null) {
                reporteCalculado.setIdCoordinador(Sesion.getTutorSesion().getIdTutor());
            }

            HashMap<String, Object> respuesta = ReporteGeneralImp.guardarReporteGeneral(reporteCalculado);

            if (!(boolean) respuesta.get("error")) {
                Utilidades.mostrarAlertaSimple("Éxito", "Reporte General guardado correctamente.",
                        Alert.AlertType.INFORMATION);

                txtaComentariosGenerales.setEditable(false);
                txtaComentariosGenerales.setStyle("-fx-opacity: 1; -fx-background-color: #f4f4f4;");

                hbBotonesEdicion.setVisible(false);
                hbBotonesEdicion.setManaged(false);

                btnFinalizar.setVisible(true);
                btnFinalizar.setManaged(true);
            } else {
                Utilidades.mostrarAlertaSimple("Error", "Error al guardar en la base de datos.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        clicVolver(event);
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarReportes(event);
        } catch (IOException ex) {
            manejarError("Error al volver al menú de reportes.", ex, "No se pudo volver al menú de reportes.");
        } catch (Exception e) {
            manejarError("Error inesperado al volver al menú de reportes.", e,
                    "Ocurrió un error inesperado al volver al menú de reportes.");
        }
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        Sesion.cerrarSesion();
        try {
            Utilidades.clicCerrarSesion(event);
        } catch (IOException ex) {
            manejarError("Error al cerrar sesión.", ex, "No se pudo cerrar la sesión.");
        } catch (Exception e) {
            manejarError("Error inesperado al cerrar sesión.", e, "Ocurrió un error inesperado al cerrar la sesión.");
        }
    }

    private void manejarError(String mensajeLog, Exception excepcion, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, "Error", mensajeUsuario);
    }
}
