/*
 * Autor: Delgado Santiago Darlington Diego
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.controller.reporte;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.ReporteTutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.dao.PeriodoDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Periodo;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteTutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLListadoReportesTutoriaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLListadoReportesTutoriaController.class);

    @FXML
    private TableView<ReporteTutoria> tblReportesTutoria;
    @FXML
    private TableColumn<ReporteTutoria, String> tcFechaGeneracion;
    @FXML
    private TableColumn<ReporteTutoria, String> tcEstatus;
    @FXML
    private TableColumn<ReporteTutoria, String> tcObservaciones;
    @FXML
    private ComboBox<Periodo> cbPeriodos;
    
    private ObservableList<ReporteTutoria> listaReportes;
    private ObservableList<Periodo> listaPeriodos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarPeriodos(); 
    }

    private void configurarTabla() {
        tcFechaGeneracion.setCellValueFactory(new PropertyValueFactory<>("fechaFormato"));
        tcEstatus.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        tcObservaciones.setCellValueFactory(new PropertyValueFactory<>("observaciones"));
    }

    private void cargarPeriodos() {
        try {
            List<Periodo> resultado = PeriodoDAO.obtenerTodosPeriodos();
            listaPeriodos = FXCollections.observableArrayList(resultado);
            cbPeriodos.setItems(listaPeriodos);

            int idActual = Sesion.getIdPeriodoActual();
            if (idActual > 0) {
                for (Periodo p : listaPeriodos) {
                    if (p.getIdPeriodo() == idActual) {
                        cbPeriodos.getSelectionModel().select(p);
                        cargarInformacion(idActual); 
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            manejarError("Error SQL al cargar periodos", ex, "No se pudieron cargar los periodos escolares.");
        } catch (Exception e) {
            manejarError("Error al cargar periodos", e, "No se pudieron cargar los periodos escolares.");
        }
    }

    @FXML
    private void clicBuscar(ActionEvent event) {
        Periodo p = cbPeriodos.getValue();
        if (p != null) {
            cargarInformacion(p.getIdPeriodo());
        } else {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Por favor, selecciona un periodo escolar.", Alert.AlertType.WARNING);
        }
    }

    private void cargarInformacion(int idPeriodo) {
        try {
            int idTutor = Sesion.getTutorSesion().getIdTutor();
            HashMap<String, Object> respuesta = ReporteTutoriaImp.obtenerReportesPorTutorYPeriodo(idTutor, idPeriodo);

            if (!(boolean) respuesta.get("error")) {
                List<ReporteTutoria> reportes = (List<ReporteTutoria>) respuesta.get("reportes");
                listaReportes = FXCollections.observableArrayList(reportes);
                tblReportesTutoria.setItems(listaReportes);

                if (listaReportes.isEmpty()) {
                    Utilidades.mostrarAlertaSimple("Sin reportes", "No tienes reportes de tutoría en el periodo seleccionado.", Alert.AlertType.INFORMATION);
                }
            } else {
                Utilidades.mostrarAlertaSimple("Error", (String) respuesta.get("mensaje"), Alert.AlertType.ERROR);
            }
        } catch (Exception e) {
            manejarError("Error al cargar la lista de reportes", e,
                    "No se pudo cargar la información de los reportes.");
        }
    }

    @FXML
    private void clicConsultar(ActionEvent event) {
        ReporteTutoria reporteSeleccionado = tblReportesTutoria.getSelectionModel().getSelectedItem();

        if (reporteSeleccionado == null) {
            Utilidades.mostrarAlertaSimple("Selección requerida", "Por favor, selecciona un reporte de la lista.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/sistematutoriascomp/sistematutorias/views/reporte/FXMLConsultarReporteTutoria.fxml"));
            Parent root = loader.load();
            FXMLConsultarReporteTutoriaController controladorDetalles = loader.getController();
            controladorDetalles.inicializarInformacion(reporteSeleccionado, false);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Detalles del Reporte");
            stage.setScene(scene);
            stage.showAndWait();

            Periodo p = cbPeriodos.getValue();
            if (p != null) {
                cargarInformacion(p.getIdPeriodo());
            }

        } catch (IOException ex) {
            manejarError("Error al abrir detalles del reporte", ex, "No se pudo abrir la ventana de detalles.");
        } catch (Exception e) {
            manejarError("Error inesperado al abrir detalles del reporte", e,
                    "Ocurrió un error inesperado al abrir la ventana de detalles.");
        }
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarReportes(event);
        } catch (IOException ex) {
            manejarError("Error al volver al menú de reportes", ex, "No se pudo volver al menú de reportes.");
        } catch (Exception e) {
            manejarError("Error inesperado al volver al menú de reportes", e,
                    "Ocurrió un error inesperado al volver al menú de reportes.");
        }
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
