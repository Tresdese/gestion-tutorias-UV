/*
 * Autor: Delgado Santiago Darlington Diego
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.controller.reporte;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.ReporteGeneralDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteGeneral;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLAdministrarReporteGeneralController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLAdministrarReporteGeneralController.class);

    @FXML
    private Button btnVolver;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnVerReporte;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableView<ReporteGeneral> tblReportesGenerales;
    @FXML
    private TableColumn<?, ?> colIdReporteGeneral;
    @FXML
    private TableColumn<?, ?> colPeriodo;
    @FXML
    private TableColumn<?, ?> colFechaGeneracion;
    @FXML
    private TableColumn<?, ?> colEstado;
    
    private ReporteGeneralDAO reporteGeneralDAO = new ReporteGeneralDAO();
    private ObservableList<ReporteGeneral> observableListReportesGenerales;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosUsuario();
        llenarTabla();
        cargarInformacion();
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        irVolver(event);
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        abrirVentanaRegistrarReporteGeneral();
    }

    @FXML
    private void clicVerReporte(ActionEvent event) {
        editarReporteSeleccionado();
    }

    private void cargarDatosUsuario() {
        Tutor tutor = Sesion.getTutorSesion();
        String rol = Sesion.getRolActual();

        if (tutor != null) {
            if (rol.equals("ACADEMICO")) {
                btnRegistrar.setVisible(true);
            } else if (rol.equals("COORDINADOR")) {
                btnRegistrar.setVisible(true);
            } else if (rol.equals("ADMINISTRADOR")) {
                btnRegistrar.setVisible(false);
            }
        }
    }

    private void cargarInformacion() {
        try {
            observableListReportesGenerales = reporteGeneralDAO.obtenerTodos();

            if (observableListReportesGenerales != null) {
                tblReportesGenerales.setItems(observableListReportesGenerales);
                configurarBusqueda();
            } else {
                Utilidades.mostrarAlertaSimple("Error", "No se pudieron cargar los reportes generales",
                        Alert.AlertType.ERROR);
            }
        } catch (SQLException e) {
            manejarError("Error al cargar los reportes generales desde la base de datos", e, "Error de base de datos",
                    "No se pudieron cargar los reportes generales. Intenta más tarde.");
        } catch (Exception e) {
            manejarError("Error inesperado al cargar los reportes generales", e, "Error inesperado",
                    "Ocurrió un error inesperado al cargar los reportes generales.");
        }
    }

    private void configurarBusqueda() {
    }

    private void llenarTabla() {
        colIdReporteGeneral.setCellValueFactory(new PropertyValueFactory<>("idReporteGeneral"));
        colPeriodo.setCellValueFactory(new PropertyValueFactory<>("nombrePeriodo"));
        colFechaGeneracion.setCellValueFactory(new PropertyValueFactory<>("fechaGeneracion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void irVolver(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarReportes(event);
        } catch (IOException ex) {
            manejarError("Error al volver al menú de reportes", ex, "Error",
                    "No se pudo volver al menú de reportes.");
        } catch (Exception e) {
            manejarError("Error inesperado al volver al menú de reportes", e, "Error inesperado",
                    "Ocurrió un error inesperado al volver al menú de reportes.");
        }
    }

    private void abrirVentanaRegistrarReporteGeneral() {
        try {
            Utilidades.openModal("/reporte/FXMLFormularioReporteGeneral.fxml", "Generar Reporte General");
            cargarInformacion();
        } catch (IOException e) {
            manejarError("Error al abrir la ventana para registrar un reporte general", e, "Error",
                    "No se pudo abrir la ventana para registrar un reporte general.");
        } catch (NullPointerException e) {
            manejarError("Recurso no encontrado al abrir la ventana para registrar un reporte general", e,
                    "Recurso no encontrado", "No se encontró el recurso para registrar un reporte general.");
        } catch (Exception e) {
            manejarError("Error inesperado al abrir la ventana para registrar un reporte general", e,
                    "Error inesperado", "Ocurrió un error inesperado al registrar un reporte general.");
        }
    }

    private void editarReporteSeleccionado() {
        ReporteGeneral seleccionado = tblReportesGenerales.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Utilidades.mostrarAlertaSimple("Sin selección",
                    "Debe seleccionar un reporte general en la tabla para poder editarlo.",
                    Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(Utilidades.class.getResource(
                    "/com/sistematutoriascomp/sistematutorias/views/reporte/FXMLFormularioReporteGeneral.fxml"));
            Parent vista = loader.load();

            FXMLFormularioReporteGeneralController controller = loader.getController();
            controller.inicializarParaEdicion(seleccionado);

            Stage escenario = new Stage();
            escenario.setScene(new Scene(vista));
            escenario.setTitle("Editar Reporte General");
            escenario.initModality(Modality.APPLICATION_MODAL);
            escenario.showAndWait();

            cargarInformacion();
        } catch (IOException e) {
            manejarError("Error al abrir la ventana para editar un reporte general", e, "Error",
                    "No se pudo abrir la ventana de edición del reporte general.");
        } catch (Exception e) {
            manejarError("Error inesperado al editar un reporte general", e, "Error inesperado",
                    "Ocurrió un error inesperado al editar el reporte general.");
        }
    }

    private void manejarError(String mensajeLog, Exception excepcion, String titulo, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, titulo, mensajeUsuario);
    }
}
