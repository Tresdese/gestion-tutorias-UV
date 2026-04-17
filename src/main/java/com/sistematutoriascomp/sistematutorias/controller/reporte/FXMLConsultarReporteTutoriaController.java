/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.controller.reporte;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.ReporteTutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.dao.ProblematicaDAO;
import com.sistematutoriascomp.sistematutorias.model.dao.TutoriaDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Problematica;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteTutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLConsultarReporteTutoriaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLConsultarReporteTutoriaController.class);

    @FXML
    private Label lbEstatus;
    @FXML
    private Label lbFecha;
    @FXML
    private Label lbTotalTutorados;
    @FXML
    private Label lbTotalAsistentes;
    @FXML
    private Label lbTotalInasistentes;
    @FXML
    private Label lbTotalProblematicas;
    @FXML
    private TextArea txtaObservaciones;
    @FXML
    private TableView<Problematica> tblProblematicas;
    @FXML
    private TableColumn<Problematica, String> tcTitulo;
    @FXML
    private TableColumn<Problematica, String> tcDescripcion;
    @FXML
    private Button btnDescargarEvidencia;
    @FXML
    private VBox vbRespuesta;
    @FXML
    private TextArea txtaRespuesta;
    @FXML
    private Button btnEnviar;
    @FXML
    private Button btnResponder;
    
    private ReporteTutoria reporteActual;
    private boolean esCoordinador = false;
    private Integer idTutorReporte = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
    }

    public void inicializarInformacion(ReporteTutoria reporte, boolean esCoordinador) {
        this.reporteActual = reporte;
        this.esCoordinador = esCoordinador;
        try {
            idTutorReporte = TutoriaDAO.obtenerIdTutorPorTutoria(reporte.getIdTutoria());
        } catch (SQLException e) {
            manejarError("No se pudo obtener el tutor propietario de la tutoría " + reporte.getIdTutoria(), e,
                    "No se pudo cargar la información del tutor del reporte.");
            idTutorReporte = null;
        }
        cargarDatosUI();
        cargarTotales(reporte.getIdTutoria());
        cargarProblematicas(reporte.getIdTutoria());
        configurarBotonEvidencia(reporte.getIdTutoria());
    }

    private void configurarTabla() {
        tcTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        tcDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tblProblematicas.setPlaceholder(new Label("Sin problemáticas para mostrar"));
    }

    private void cargarDatosUI() {
        if (reporteActual != null) {
            lbFecha.setText(reporteActual.getFechaFormato());
            txtaObservaciones.setText(reporteActual.getObservaciones());
            lbEstatus.setText(reporteActual.getEstatus());

            btnEnviar.setVisible(false);
            btnEnviar.setManaged(false);
            btnResponder.setVisible(false);
            btnResponder.setManaged(false);

            boolean esDelTutorSesion = false;
            if (Sesion.getTutorSesion() != null && idTutorReporte != null) {
                esDelTutorSesion = Sesion.getTutorSesion().getIdTutor() == idTutorReporte;
            }

            if (esCoordinador) {
                if ("BORRADOR".equalsIgnoreCase(reporteActual.getEstatus()) && esDelTutorSesion) {
                    btnEnviar.setVisible(true);
                    btnEnviar.setManaged(true);
                    lbEstatus.setStyle(
                            "-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-padding: 5 10; -fx-background-radius: 5;");
                }

                if ("ENVIADO".equalsIgnoreCase(reporteActual.getEstatus())) {
                    btnResponder.setVisible(true);
                    btnResponder.setManaged(true);
                    lbEstatus.setStyle(
                            "-fx-background-color: #D4EDDA; -fx-text-fill: #155724; -fx-padding: 5 10; -fx-background-radius: 5;");
                } else if ("REVISADO".equalsIgnoreCase(reporteActual.getEstatus())) {
                    lbEstatus.setStyle(
                            "-fx-background-color: #CCE5FF; -fx-text-fill: #004085; -fx-padding: 5 10; -fx-background-radius: 5;");
                }
            } else {
                if ("BORRADOR".equalsIgnoreCase(reporteActual.getEstatus()) && esDelTutorSesion) {
                    btnEnviar.setVisible(true);
                    btnEnviar.setManaged(true);
                    lbEstatus.setStyle(
                            "-fx-background-color: #FFF3CD; -fx-text-fill: #856404; -fx-padding: 5 10; -fx-background-radius: 5;");
                } else if ("REVISADO".equalsIgnoreCase(reporteActual.getEstatus())) {
                    lbEstatus.setStyle(
                            "-fx-background-color: #CCE5FF; -fx-text-fill: #004085; -fx-padding: 5 10; -fx-background-radius: 5;");
                } else {
                    lbEstatus.setStyle(
                            "-fx-background-color: #D4EDDA; -fx-text-fill: #155724; -fx-padding: 5 10; -fx-background-radius: 5;");
                }
            }

            if (reporteActual.getRespuesta() != null && !reporteActual.getRespuesta().trim().isEmpty()) {
                vbRespuesta.setVisible(true);
                vbRespuesta.setManaged(true);
                txtaRespuesta.setText(reporteActual.getRespuesta());
            } else {
                vbRespuesta.setVisible(false);
                vbRespuesta.setManaged(false);
            }
        }
    }

    private void cargarTotales(int idTutoria) {
        HashMap<String, Object> respuesta = ReporteTutoriaImp.cargarTotales(idTutoria);
        if (!(boolean) respuesta.get("error")) {
            HashMap<String, Integer> totales = (HashMap<String, Integer>) respuesta.get("totales");
            lbTotalTutorados.setText(String.valueOf(totales.get("tutorados")));
            lbTotalAsistentes.setText(String.valueOf(totales.get("asistentes")));
            lbTotalInasistentes.setText(String.valueOf(totales.get("faltantes")));
            lbTotalProblematicas.setText(String.valueOf(totales.get("problematicas")));
        }
    }

    private void cargarProblematicas(int idTutoria) {
        try {
            List<Problematica> lista = ProblematicaDAO.obtenerProblematicasPorTutoria(idTutoria);
            tblProblematicas.setItems(FXCollections.observableArrayList(lista));
            tblProblematicas.refresh();
            LOGGER.info("Problemáticas cargadas para tutoria {}: {} registros", idTutoria, lista.size());

            if (lista.isEmpty()) {
                Utilidades.mostrarAlertaSimple("Sin problemáticas",
                        "No hay problemáticas registradas para esta tutoría.",
                        Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            manejarError("Error al cargar problemáticas del reporte", e,
                    "No se pudieron cargar las problemáticas de la tutoría.");
        }
    }

    private void configurarBotonEvidencia(int idTutoria) {
        try {
            byte[] evidencia = TutoriaDAO.obtenerEvidencia(idTutoria);
            if (evidencia != null && evidencia.length > 0) {
                btnDescargarEvidencia.setDisable(false);
                btnDescargarEvidencia.setText("Ver Evidencia");
            } else {
                btnDescargarEvidencia.setDisable(true);
                btnDescargarEvidencia.setText("Sin Evidencia Adjunta");
            }
        } catch (SQLException e) {
            manejarError("Error al obtener evidencia para la tutoría " + idTutoria, e,
                    "No se pudo obtener la evidencia de la sesión.");
            btnDescargarEvidencia.setDisable(true);
        }
    }

    @FXML
    private void clicVerEvidencia(ActionEvent event) {
        try {
            byte[] evidencia = TutoriaDAO.obtenerEvidencia(reporteActual.getIdTutoria());
            if (evidencia != null) {
                File archivoTemporal = File
                        .createTempFile("Evidencia_Reporte_" + reporteActual.getIdReporteTutoria() + "_", ".pdf");
                archivoTemporal.deleteOnExit();

                try (FileOutputStream fos = new FileOutputStream(archivoTemporal)) {
                    fos.write(evidencia);
                }

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivoTemporal);
                } else {
                    Utilidades.mostrarAlertaSimple("Error", "El sistema no soporta la apertura automática de archivos.",
                            Alert.AlertType.ERROR);
                }
            }
        } catch (Exception e) {
            manejarError("Error al visualizar evidencia", e,
                    "Ocurrió un error al intentar abrir el archivo de evidencia.");
        }
    }

    @FXML
    private void clicEnviar(ActionEvent event) {
        boolean confirmar = Utilidades.mostrarAlertaConfirmacion("Confirmar Envío",
                "No podrás hacer cambios después. ¿Continuar con el envío?");
        if (confirmar) {
            enviarReporte();
        }
    }

    private void enviarReporte() {
        HashMap<String, Object> respuesta = ReporteTutoriaImp.enviarReporte(reporteActual.getIdReporteTutoria());
        if (!(boolean) respuesta.get("error")) {
            Utilidades.mostrarAlertaSimple("Reporte enviado correctamente", (String) respuesta.get("mensaje"),
                    Alert.AlertType.INFORMATION);
            reporteActual.setEstatus("ENVIADO");
            cargarDatosUI();
        } else {
            Utilidades.mostrarAlertaSimple("Error", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void clicResponder(ActionEvent event) {
        abrirVentanaRespuesta();
    }

    private void abrirVentanaRespuesta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/sistematutoriascomp/sistematutorias/views/reporte/FXMLResponderReporteTutoria.fxml"));
            Parent root = loader.load();
            FXMLResponderReporteTutoriaController controlador = loader.getController();
            controlador.inicializarReporte(reporteActual);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Responder Reporte");
            stage.setScene(scene);
            stage.showAndWait();

            if (controlador.isRespuestaGuardada()) {
                reporteActual.setEstatus("REVISADO");
                reporteActual.setRespuesta(controlador.getRespuestaTexto());
                cargarDatosUI();
            }

        } catch (IOException ex) {
            manejarError("Error al abrir ventana de respuesta", ex,
                    "No se pudo abrir la ventana para responder el reporte.");
        } catch (Exception e) {
            manejarError("Error inesperado al abrir ventana de respuesta", e,
                    "Ocurrió un error inesperado al responder el reporte.");
        }
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        Utilidades.cerrarVentana(event);
    }

    @FXML
    private void clicCerrarSesion(ActionEvent event) {
        Sesion.cerrarSesion();
        try {
            Utilidades.clicCerrarSesion(event);
        } catch (IOException ex) {
            manejarError("Error al cerrar sesión", ex, "No se pudo cerrar la sesión.");
        } catch (Exception e) {
            manejarError("Error inesperado al cerrar sesión", e, "Ocurrió un error inesperado al cerrar la sesión.");
        }
    }

    private void manejarError(String mensajeLog, Exception excepcion, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, "Error", mensajeUsuario);
    }
}
