/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 6.0
 */
package com.sistematutoriascomp.sistematutorias.controller.reporte;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.PeriodoDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.ReporteGeneralDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutorDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Periodo;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteGeneral;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLFormularioReporteGeneralController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLFormularioReporteGeneralController.class);

    @FXML
    private Button btnVolver;
    @FXML
    private Button btnResponder;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnEditar;
    @FXML
    private TextField txtTotalEstudiantesRiesgo;
    @FXML
    private TextField txtIdReporteGeneral;
    @FXML
    private TextField txtFechaGeneracion;
    @FXML
    private TextField txtTotalTutorados;
    @FXML
    private TextField txtTotalTutores;
    @FXML
    private TextField txtPorcentajeAsistencia;
    @FXML
    private TextField txtTotalProblematicas;
    @FXML
    private ComboBox cbEstado;
    @FXML
    private ComboBox cbPeriodo;
    @FXML
    
    private ComboBox cbCoordinador;
    private TutorDAO tutorDAO = new TutorDAO();
    private PeriodoDAO periodoDAO = new PeriodoDAO();
    private ReporteGeneralDAO reporteGeneralDAO = new ReporteGeneralDAO();
    private ReporteGeneral reporteEnEdicion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarCombos();
        
        configurarModoCreacion();
    }

    public void inicializarParaEdicion(ReporteGeneral reporte) {
        this.reporteEnEdicion = reporte;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        txtIdReporteGeneral.setText(String.valueOf(reporte.getIdReporteGeneral()));

        if (reporte.getFechaGeneracion() != null) {
            txtFechaGeneracion.setText(reporte.getFechaGeneracion().format(formatter));
        } else {
            txtFechaGeneracion.clear();
        }

        txtTotalTutorados.setText(String.valueOf(reporte.getTotalTutorados()));
        txtTotalTutores.setText(String.valueOf(reporte.getTotalTutores()));
        txtPorcentajeAsistencia.setText(
                reporte.getPorcentajeAsistencia() != null ? reporte.getPorcentajeAsistencia().toPlainString() : "");
        txtTotalProblematicas.setText(String.valueOf(reporte.getTotalProblematicas()));

        cbEstado.setValue(reporte.getEstado());
        cbPeriodo.setValue(reporte.getNombrePeriodo());

        try {
            List<Tutor> tutores = tutorDAO.getAllTutors();
            for (Tutor tutor : tutores) {
                if (tutor.getIdTutor() == reporte.getIdCoordinador()) {
                    cbCoordinador.setValue(tutor.getNombre());
                    break;
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al cargar coordinador para edición de reporte", ex);
            Utilidades.mostrarAlertaSimple("Error de base de datos",
                    "No se pudo cargar el coordinador del reporte: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al cargar coordinador para edición de reporte", e);
            Utilidades.mostrarAlertaSimple("Error inesperado",
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }

        configurarModoSoloLecturaEdicion();
    }

    public int obtenerIdPeriodo() {
        int idPeriodo = -1;
        try {
            idPeriodo = periodoDAO.obtenerIdPorNombre(cbPeriodo.getValue().toString());
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener IDs de la base de datos", ex);
            Utilidades.mostrarAlertaSimple("Error de base de datos",
                    "Error al obtener IDs: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener IDs de la base de datos", e);
            Utilidades.mostrarAlertaSimple("Error inesperado",
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
        return idPeriodo;
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        cerrarVentana(event);
    }

    @FXML
    private void clicResponder(ActionEvent event) {
        responderReporte();
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        if (validarCampos()) {
            guardarReporteGeneral();
        }
    }

    @FXML
    private void clicEditar(ActionEvent event) {
        habilitarEdicion(true);
        btnGuardar.setDisable(false);
        btnEditar.setVisible(false);
        btnEditar.setManaged(false);
    }

    private void cargarCombos() {
        cargarCombosPeriodos();
        cargarCombosEstados();
        cargarCombosCoordinadores();
    }

    private void configurarModoCreacion() {
        reporteEnEdicion = null;
        btnEditar.setVisible(false);
        btnEditar.setManaged(false);
        btnGuardar.setDisable(false);
        btnResponder.setVisible(false);
        habilitarEdicion(true);
    }

    private void configurarModoSoloLecturaEdicion() {
        habilitarEdicion(false);
        btnResponder.setVisible(true);
        btnGuardar.setDisable(true);
        btnEditar.setVisible(true);
        btnEditar.setManaged(true);
    }

    private void habilitarEdicion(boolean habilitar) {
        txtTotalTutorados.setDisable(!habilitar);
        txtTotalTutores.setDisable(!habilitar);
        txtPorcentajeAsistencia.setDisable(!habilitar);
        txtTotalProblematicas.setDisable(!habilitar);

        cbEstado.setDisable(!habilitar);
        cbPeriodo.setDisable(!habilitar);
        cbCoordinador.setDisable(!habilitar);
    }

    private void responderReporte() {
        if (reporteEnEdicion == null) {
            Utilidades.mostrarAlertaSimple("Sin reporte seleccionado",
                    "No hay un reporte general para responder.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/sistematutoriascomp/sistematutorias/views/reporte/FXMLRespuestaReporteGeneral.fxml"));
            Parent vista = loader.load();
            FXMLResponderReporteGeneralController controller = loader.getController();
            controller.inicializarReporte(reporteEnEdicion);

            Stage escenario = new Stage();
            escenario.setScene(new Scene(vista));
            escenario.setTitle("Responder Reporte General");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

            if (controller.isRespuestaGuardada()) {
                cbEstado.setValue(reporteEnEdicion.getEstado());
            }

        } catch (IOException e) {
            LOGGER.error("Error al abrir la ventana para responder un reporte general", e);
            Utilidades.mostrarAlertaSimple("Error",
                    "No se pudo abrir la ventana de respuesta del reporte general: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (NullPointerException ex) {
            LOGGER.error("Error al responder el reporte general", ex);
            Utilidades.mostrarAlertaSimple("Error",
                    "No se pudo abrir la ventana de respuesta del reporte general: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al responder el reporte general", e);
            Utilidades.mostrarAlertaSimple("Error inesperado",
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void guardarReporteGeneral() {

        boolean esEdicion = (reporteEnEdicion != null);

        ReporteGeneral reporteGeneral = new ReporteGeneral();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (esEdicion) {
            reporteGeneral.setIdReporteGeneral(reporteEnEdicion.getIdReporteGeneral());
        } else {
            String idTexto = txtIdReporteGeneral.getText() != null ? txtIdReporteGeneral.getText().trim() : "";
            if (!idTexto.isEmpty()) {
                reporteGeneral.setIdReporteGeneral(Integer.parseInt(idTexto));
            }
        }

        String fechaTexto = txtFechaGeneracion.getText() != null ? txtFechaGeneracion.getText().trim() : "";
        LocalDateTime fechaGeneracion;
        if (fechaTexto.isEmpty()) {
            fechaGeneracion = LocalDateTime.now();
            txtFechaGeneracion.setText(fechaGeneracion.format(formatter));
        } else {
            fechaGeneracion = LocalDateTime.parse(fechaTexto, formatter);
        }
        reporteGeneral.setFechaGeneracion(fechaGeneracion);

        reporteGeneral.setTotalTutorados(Integer.parseInt(txtTotalTutorados.getText().trim()));
        reporteGeneral.setTotalTutores(Integer.parseInt(txtTotalTutores.getText().trim()));
        reporteGeneral.setPorcentajeAsistencia(
                BigDecimal.valueOf(Double.parseDouble(txtPorcentajeAsistencia.getText().trim())));
        reporteGeneral.setTotalProblematicas(Integer.parseInt(txtTotalProblematicas.getText().trim()));

        reporteGeneral.setEstado(cbEstado.getValue().toString());
        reporteGeneral.setIdPeriodo(obtenerIdPeriodo());
        reporteGeneral.setIdCoordinador(obtenerIdCoordinador());

        try {
            boolean exito;
            if (esEdicion) {
                exito = reporteGeneralDAO.actualizar(reporteGeneral);
            } else {
                exito = reporteGeneralDAO.insertar(reporteGeneral);
            }

            if (exito) {
                String mensaje = esEdicion
                        ? "El reporte general se ha actualizado correctamente."
                        : "El reporte general se ha guardado correctamente.";
                Utilidades.mostrarAlertaSimple("Éxito", mensaje, Alert.AlertType.INFORMATION);
            } else {
                Utilidades.mostrarAlertaSimple("Error", "No se pudo guardar el reporte general.",
                        Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            LOGGER.error("Error inesperado al guardar el reporte general en la base de datos", e);
            Utilidades.mostrarAlertaSimple("Error inesperado",
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private boolean validarCampos() {
        boolean respuesta = true;
        if (cbPeriodo.getSelectionModel().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El periodo es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (cbCoordinador.getSelectionModel().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El coordinador es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (cbEstado.getSelectionModel().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El estado es obligatorio", Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtTotalTutorados.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El total de tutorados es obligatorio",
                    Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtTotalTutores.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El total de tutores es obligatorio",
                    Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtPorcentajeAsistencia.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El porcentaje de asistencia es obligatorio",
                    Alert.AlertType.WARNING);
            respuesta = false;
        }
        if (txtTotalProblematicas.getText().trim().isEmpty()) {
            Utilidades.mostrarAlertaSimple("Campos vacíos", "El total de problemáticas es obligatorio",
                    Alert.AlertType.WARNING);
            respuesta = false;
        }
        return respuesta;
    }

    private void cargarCombosCoordinadores() {
        List<Tutor> tutores;
        try {
            tutores = tutorDAO.getAllTutors();
            for (Tutor tutor : tutores) {
                if (tutor.getIdRol() == 2) {
                    cbCoordinador.getItems().add(tutor.getNombre());
                }
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener tutores de la base de datos", ex);
            Utilidades.mostrarAlertaSimple("Error de base de datos",
                    "Error al cargar tutores: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener tutores de la base de datos", e);
            Utilidades.mostrarAlertaSimple("Error inesperado",
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private int obtenerIdCoordinador() {
        int idCoordinador = -1;
        try {
            idCoordinador = tutorDAO.obtenerIdPorNombre(cbCoordinador.getValue().toString());
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener ID del coordinador de la base de datos", ex);
            Utilidades.mostrarAlertaSimple("Error de base de datos",
                    "Error al obtener ID del coordinador: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener ID del coordinador de la base de datos", e);
            Utilidades.mostrarAlertaSimple("Error inesperado",
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
        return idCoordinador;
    }

    private void cargarCombosEstados() {
        cbEstado.getItems().addAll("Pendiente", "Revisado", "Enviado a Director");
    }

    private void cargarCombosPeriodos() {
        List<Periodo> periodos;
        try {
            periodos = periodoDAO.obtenerTodosPeriodos();
            for (Periodo periodo : periodos) {
                cbPeriodo.getItems().add(periodo.getNombre());
            }
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener periodos de la base de datos", ex);
            Utilidades.mostrarAlertaSimple("Error de base de datos",
                    "Error al cargar periodos: " + ex.getMessage(),
                    Alert.AlertType.ERROR);
        } catch (Exception e) {
            LOGGER.error("Error inesperado al obtener periodos de la base de datos", e);
            Utilidades.mostrarAlertaSimple("Error inesperado",
                    "Ocurrió un error inesperado: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
