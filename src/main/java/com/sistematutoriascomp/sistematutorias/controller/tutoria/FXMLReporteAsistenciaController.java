package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.AsistenciaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.ReporteAsistenciaRow;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
public class FXMLReporteAsistenciaController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLReporteAsistenciaController.class);

    @FXML private TableView<ReporteAsistenciaRow> tblReporte;
    @FXML private TableColumn<ReporteAsistenciaRow, String> tcMatricula;
    @FXML private TableColumn<ReporteAsistenciaRow, String> tcNombre;
    @FXML private TableColumn<ReporteAsistenciaRow, Integer> tcSemestre;
    @FXML private TableColumn<ReporteAsistenciaRow, String> tcFechaSesion;
    @FXML private TableColumn<ReporteAsistenciaRow, String> tcAsistencia;
    @FXML private Label lbTotalRegistros;
    @FXML private Label lbTotalAsistencias;
    @FXML private Label lbTotalFaltas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarReporte();
    }

    private void configurarTabla() {
        tcMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        tcNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        tcSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        tcFechaSesion.setCellValueFactory(new PropertyValueFactory<>("fechaSesion"));
        tcAsistencia.setCellValueFactory(new PropertyValueFactory<>("asistencia"));
    }

    private void cargarReporte() {
        int idTutor = Sesion.getTutorSesion().getIdTutor();
        HashMap<String, Object> respuesta = AsistenciaImp.obtenerReporteAsistencia(idTutor);

        if ((boolean) respuesta.get("error")) {
            String mensaje = (String) respuesta.get("mensaje");
            LOGGER.warn("No se pudo cargar el reporte de asistencia: {}", mensaje);
            Utilidades.mostrarAlertaSimple("Sin asistencias", mensaje, Alert.AlertType.INFORMATION);
            return;
        }

        @SuppressWarnings("unchecked")
        List<ReporteAsistenciaRow> lista = (List<ReporteAsistenciaRow>) respuesta.get("reporte");
        tblReporte.setItems(FXCollections.observableArrayList(lista));

        long asistencias = lista.stream().filter(r -> "Asistió".equals(r.getAsistencia())).count();
        long faltas = lista.size() - asistencias;
        lbTotalRegistros.setText(String.valueOf(lista.size()));
        lbTotalAsistencias.setText(String.valueOf(asistencias));
        lbTotalFaltas.setText(String.valueOf(faltas));
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarReportes(event);
        } catch (IOException ex) {
            LOGGER.error("Error al volver al menú de reportes", ex);
        }
    }
}
