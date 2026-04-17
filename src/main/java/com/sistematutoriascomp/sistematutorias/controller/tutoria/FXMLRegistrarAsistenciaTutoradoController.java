/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Hernandez Romero Jarly
 * Versión: 7.0
 */
package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import com.sistematutoriascomp.sistematutorias.dominio.AsistenciaImp;
import com.sistematutoriascomp.sistematutorias.dominio.TutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.AsistenciaRow;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutoria;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FXMLRegistrarAsistenciaTutoradoController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLRegistrarAsistenciaTutoradoController.class);

    @FXML
    private ComboBox<Tutoria> cbSesiones;
    @FXML
    private TableView<AsistenciaRow> tvAsistencia;
    @FXML
    private TableColumn<AsistenciaRow, String> colMatricula;
    @FXML
    private TableColumn<AsistenciaRow, String> colNombre;
    @FXML
    private TableColumn<AsistenciaRow, Integer> colSemestre;
    @FXML
    private TableColumn<AsistenciaRow, Boolean> colAsistio;
    @FXML
    private TableColumn<AsistenciaRow, Void> colAcciones;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnSubirEvidencia;
    @FXML
    private Label lbErrorSesion;
    @FXML
    private Label lbMensajeInfo;

    private ObservableList<AsistenciaRow> listaAlumnos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        inicializarEstadoUI();
        configurarTabla();
        configurarEventos();
        cargarSesiones();
    }

    private void inicializarEstadoUI() {
        listaAlumnos = FXCollections.observableArrayList();
        tvAsistencia.setItems(listaAlumnos);
        limpiarErrorSesion();
        ocultarMensajeInfo();
        btnRegistrar.setDisable(false);
        btnSubirEvidencia.setDisable(true);
    }

    private void configurarEventos() {
        cbSesiones.valueProperty().addListener((obs, oldVal, newVal) -> onSesionSeleccionada(newVal));
    }

    private void configurarTabla() {
        colMatricula.setCellValueFactory(new PropertyValueFactory<>("matricula"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colSemestre.setCellValueFactory(new PropertyValueFactory<>("semestre"));
        colAsistio.setCellValueFactory(cellData -> cellData.getValue().asistioProperty());
        colAsistio.setCellFactory(CheckBoxTableCell.forTableColumn(colAsistio));
        tvAsistencia.setEditable(true);
        configurarColumnaAccionesProblematica();
    }

    private void configurarColumnaAccionesProblematica() {
        Callback<TableColumn<AsistenciaRow, Void>, TableCell<AsistenciaRow, Void>> cellFactory
                = new Callback<TableColumn<AsistenciaRow, Void>, TableCell<AsistenciaRow, Void>>() {
            @Override
            public TableCell<AsistenciaRow, Void> call(final TableColumn<AsistenciaRow, Void> param) {
                return new TableCell<AsistenciaRow, Void>() {
                    private final Button btn = new Button("Problemática");

                    {
                        btn.setOnAction((ActionEvent event) -> manejarClickProblematica());
                    }

                    private void manejarClickProblematica() {
                        AsistenciaRow data = getTableView().getItems().get(getIndex());
                        Tutoria sesionActual = cbSesiones.getValue();

                        if (data == null || sesionActual == null) {
                            return;
                        }

                        boolean asistenciaCerrada = AsistenciaImp.yaTieneAsistenciaRegistrada(sesionActual.getIdTutoria());

                        if (asistenciaCerrada) {
                            Utilidades.mostrarAlertaSimple(
                                    "Registro cerrado",
                                    "La asistencia ya fue registrada. No se pueden agregar más problemáticas.",
                                    Alert.AlertType.WARNING
                            );
                            return;
                        }

                        if (data.isAsistio()) {
                            abrirVentanaProblematica(data.getIdTutorado(), data.getNombreCompleto());
                        } else {
                            Utilidades.mostrarAlertaSimple(
                                    "Aviso",
                                    "El alumno debe tener asistencia para registrar problemática.",
                                    Alert.AlertType.WARNING
                            );
                        }
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colAcciones.setCellFactory(cellFactory);
    }

    private void cargarSesiones() {
        int idTutor = Sesion.getTutorSesion().getIdTutor();
        HashMap<String, Object> respuesta = AsistenciaImp.obtenerSesionesTutor(idTutor);

        if (!(boolean) respuesta.get("error")) {
            List<Tutoria> sesiones = (List<Tutoria>) respuesta.get("sesiones");
            cbSesiones.setItems(FXCollections.observableArrayList(sesiones));
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Sin sesiones",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.INFORMATION
            );
        }
    }

    private void onSesionSeleccionada(Tutoria nuevaSesion) {
        limpiarErrorSesion();
        if (nuevaSesion == null) {
            limpiarTablaAsistencia();
            ocultarMensajeInfo();
            btnSubirEvidencia.setDisable(true);
            btnRegistrar.setDisable(false);
            return;
        }

        cargarAlumnos();
        configurarEstadoBotones(nuevaSesion.getIdTutoria());
    }

    private void configurarEstadoBotones(int idTutoria) {
        boolean yaTieneAsistencia = AsistenciaImp.yaTieneAsistenciaRegistrada(idTutoria);

        btnRegistrar.setDisable(yaTieneAsistencia);

        try {
            boolean tieneEvidencia = TutoriaImp.comprobarExistenciaEvidencia(idTutoria);

            if (tieneEvidencia) {
                btnSubirEvidencia.setDisable(true);
                mostrarMensajeInfo("Ya se ha subido evidencia para esta sesión.", "#2e7d32");
            } else if (yaTieneAsistencia) {
                btnSubirEvidencia.setDisable(false);
                ocultarMensajeInfo();
            } else {
                btnSubirEvidencia.setDisable(true);
                ocultarMensajeInfo();
            }
        } catch (Exception e) {
            manejarError("Error al configurar estado de los botones para la sesión " + idTutoria, e,
                    "No se pudo actualizar el estado de los botones.");
        }
    }

    private void cargarAlumnos() {
        Tutoria sesion = cbSesiones.getValue();
        if (sesion == null) {
            limpiarTablaAsistencia();
            return;
        }
        int idTutor = Sesion.getTutorSesion().getIdTutor();
        HashMap<String, Object> respuesta = AsistenciaImp.obtenerListaAsistencia(idTutor, sesion.getIdTutoria());
        if (!(boolean) respuesta.get("error")) {
            listaAlumnos = FXCollections.observableArrayList(
                    (List<AsistenciaRow>) respuesta.get("tutorados"));
            tvAsistencia.setItems(listaAlumnos);
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.ERROR
            );
            limpiarTablaAsistencia();
        }
    }

    private void limpiarTablaAsistencia() {
        if (listaAlumnos != null) {
            listaAlumnos.clear();
        }
    }

    @FXML
    private void clicRegistrar(ActionEvent event) {
        Tutoria sesion = cbSesiones.getValue();
        if (!esSesionSeleccionadaValida(sesion)) {
            return;
        }
        guardarAsistenciaSesion(sesion);
    }

    private boolean esSesionSeleccionadaValida(Tutoria sesion) {
        if (sesion == null) {
            lbErrorSesion.setText("Selecciona una sesión");
            lbErrorSesion.setVisible(true);
            lbErrorSesion.setManaged(true);
            return false;
        }
        return true;
    }

    private void guardarAsistenciaSesion(Tutoria sesion) {
        if (listaAlumnos == null || listaAlumnos.isEmpty()) {
            Utilidades.mostrarAlertaSimple(
                    "Sin alumnos",
                    "No hay tutorados para registrar asistencia.",
                    Alert.AlertType.WARNING
            );
            return;
        }

        HashMap<String, Object> res = AsistenciaImp.guardarListaAsistencia(
                sesion.getIdTutoria(),
                new ArrayList<>(listaAlumnos));
        if (!(boolean) res.get("error")) {
            Utilidades.mostrarAlertaSimple(
                    "Éxito",
                    (String) res.get("mensaje"),
                    Alert.AlertType.INFORMATION
            );
            btnRegistrar.setDisable(true);
            habilitarSubirEvidenciaSiCorresponde(sesion.getIdTutoria());
        } else {
            Utilidades.mostrarAlertaSimple(
                    "Error",
                    (String) res.get("mensaje"),
                    Alert.AlertType.ERROR
            );
        }
    }

    private void habilitarSubirEvidenciaSiCorresponde(int idTutoria) {
        try {
            boolean tieneEvidencia = TutoriaImp.comprobarExistenciaEvidencia(idTutoria);
            
            if (!tieneEvidencia) {
                btnSubirEvidencia.setDisable(false);
                ocultarMensajeInfo();
            } else {
                btnSubirEvidencia.setDisable(true);
                mostrarMensajeInfo("Ya se ha subido evidencia para esta sesión.", "#2e7d32");
            }
        } catch (Exception e) {
            manejarError("Error al habilitar subida de evidencia para la sesión " + idTutoria, e,
                    "No se pudo habilitar la carga de evidencia.");
        }
    }

    @FXML
    private void clicSubirEvidencia(ActionEvent event) {
        Tutoria sesion = cbSesiones.getValue();
        if (!validarSesionParaEvidencia(sesion)) {
            return;
        }
        if (yaTieneEvidenciaEnBD(sesion)) {
            return;
        }
        File archivo = mostrarSelectorArchivoPdf();
        if (archivo == null) {
            return;
        }
        if (!validarArchivoPdf(archivo)) {
            return;
        }
        subirEvidenciaAServicio(sesion, archivo);
    }

    private boolean validarSesionParaEvidencia(Tutoria sesion) {
        boolean respuesta = true;
        if (sesion == null) {
            Utilidades.mostrarAlertaSimple(
                    "Selección requerida",
                    "Por favor seleccione una sesión de tutoría.",
                    Alert.AlertType.WARNING
            );
            respuesta = false;
        }
        return respuesta;
    }

    private boolean yaTieneEvidenciaEnBD(Tutoria sesion) {
        boolean respuesta = false;
        try {
            if (TutoriaImp.comprobarExistenciaEvidencia(sesion.getIdTutoria())) {
                Utilidades.mostrarAlertaSimple(
                        "Aviso",
                        "Ya existe una evidencia para esta sesión.",
                        Alert.AlertType.INFORMATION
                );
                btnSubirEvidencia.setDisable(true);
                mostrarMensajeInfo("Ya se ha subido evidencia para esta sesión.", "#2e7d32");
                respuesta = true;
            }
        } catch (Exception e) {
            manejarError("Error al verificar evidencia existente para la sesión " + sesion.getIdTutoria(), e,
                    "No se pudo verificar la evidencia de la sesión.");
        }
        return respuesta;
    }

    private File mostrarSelectorArchivoPdf() {
        FileChooser dialogo = new FileChooser();
        dialogo.setTitle("Seleccionar evidencia (PDF)");
        dialogo.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );
        return dialogo.showOpenDialog(btnSubirEvidencia.getScene().getWindow());
    }

    private boolean validarArchivoPdf(File archivo) {
        boolean respuesta = true;
        long maxBytes = 5L * 1024L * 1024L;
        if (archivo.length() > maxBytes) {
            Utilidades.mostrarAlertaSimple(
                    "Archivo muy pesado",
                    "El archivo debe pesar menos de 5MB.",
                    Alert.AlertType.WARNING
            );
            respuesta = false;
        }

        try {
            String mimeType = Files.probeContentType(archivo.toPath());

            if (mimeType != null && !mimeType.equals("application/pdf")) {
                Utilidades.mostrarAlertaSimple(
                        "Formato incorrecto",
                        "El archivo seleccionado no es un PDF válido.",
                        Alert.AlertType.WARNING
                );
                respuesta = false;
            }

            if (mimeType == null && !archivo.getName().toLowerCase().endsWith(".pdf")) {
                Utilidades.mostrarAlertaSimple(
                        "Formato incorrecto",
                        "El archivo debe tener extensión .pdf",
                        Alert.AlertType.WARNING
                );
                respuesta = false;
            }

        } catch (IOException e) {
            manejarError("Error al validar archivo PDF", e, "No se pudo validar el archivo seleccionado.");
            respuesta = false;
        }

        return respuesta;
    }

    private void subirEvidenciaAServicio(Tutoria sesion, File archivo) {
        try {
            byte[] datosArchivo = Files.readAllBytes(archivo.toPath());
            HashMap<String, Object> respuesta
                    = TutoriaImp.subirEvidencia(sesion.getIdTutoria(), datosArchivo);

            if (!(boolean) respuesta.get("error")) {
                Utilidades.mostrarAlertaSimple(
                        "Éxito",
                        (String) respuesta.get("mensaje"),
                        Alert.AlertType.INFORMATION
                );
                btnSubirEvidencia.setDisable(true);
                mostrarMensajeInfo("Ya se ha subido evidencia para esta sesión.", "#2e7d32");
            } else {
                Utilidades.mostrarAlertaSimple(
                        "Error",
                        (String) respuesta.get("mensaje"),
                        Alert.AlertType.ERROR
                );
            }
        } catch (IOException ex) {
            manejarError("Error al leer archivo para subir evidencia de la sesión " + sesion.getIdTutoria(), ex,
                    "No se pudo leer el archivo seleccionado.");
        } catch (NullPointerException ex) {
            manejarError("Error al leer archivo para subir evidencia de la sesión " + sesion.getIdTutoria(), ex,
                    "No se pudo leer el archivo seleccionado.");
        } catch (Exception ex) {
            manejarError("Error al subir evidencia para la sesión " + sesion.getIdTutoria(), ex,
                    "No se pudo procesar la evidencia seleccionada.");
        }
    }

    private void abrirVentanaProblematica(int idTutorado, String nombreAlumno) {
        try {
            Tutoria sesionActual = cbSesiones.getValue();
            if (sesionActual == null) {
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/sistematutoriascomp/sistematutorias/views/tutoria/FXMLRegistrarProblematica.fxml")
            );
            Parent root = loader.load();

            FXMLRegistrarProblematicaController controlador = loader.getController();
            controlador.inicializarValores(
                    sesionActual.getIdTutoria(),
                    idTutorado,
                    nombreAlumno
            );

            Stage escenario = new Stage();
            escenario.setScene(new Scene(root));
            escenario.setTitle("Registrar Problemática");
            escenario.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            escenario.showAndWait();

        } catch (IOException ex) {
            manejarError("Error al cargar ventana de problemática para el alumno " + nombreAlumno, ex,
                    "No se pudo cargar la ventana para registrar la problemática.");
        } catch (Exception ex) {
            manejarError("Error al abrir ventana de problemática para el alumno " + nombreAlumno, ex,
                    "No se pudo abrir la ventana para registrar la problemática.");
        }
    }

    @FXML
    private void clicVolver(ActionEvent event) {
        try {
            Utilidades.volverMenuGestionarTutorias(event);
        } catch (IOException ex) {
            manejarError("Error al volver al menú de tutorías", ex, "No se pudo volver al menú de tutorías.");
        } catch (Exception e) {
            manejarError("Error inesperado al volver al menú de tutorías", e,
                    "Ocurrió un error inesperado al volver al menú de tutorías.");
        }
    }

    private void limpiarErrorSesion() {
        lbErrorSesion.setText("");
        lbErrorSesion.setVisible(false);
        lbErrorSesion.setManaged(false);
    }

    private void mostrarMensajeInfo(String texto, String colorHex) {
        lbMensajeInfo.setText(texto);
        lbMensajeInfo.setStyle("-fx-text-fill: " + colorHex + ";");
        lbMensajeInfo.setVisible(true);
        lbMensajeInfo.setManaged(true);
    }

    private void ocultarMensajeInfo() {
        lbMensajeInfo.setText("");
        lbMensajeInfo.setVisible(false);
        lbMensajeInfo.setManaged(false);
    }

    private void manejarError(String mensajeLog, Exception excepcion, String mensajeUsuario) {
        Utilidades.manejarErrorTecnico(LOGGER, mensajeLog, excepcion, "Error", mensajeUsuario);
    }
}
