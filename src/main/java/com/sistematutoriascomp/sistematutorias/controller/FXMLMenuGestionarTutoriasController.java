/*
 * Autor: Hernandez Romero Jarly
 * Ultima modificación hecha por: Delgado Santiago Darlington Diego
 * Versión: 4.0
 */
package com.sistematutoriascomp.sistematutorias.controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.dominio.AsistenciaImp;
import com.sistematutoriascomp.sistematutorias.dominio.FechaTutoriaImp;
import com.sistematutoriascomp.sistematutorias.dominio.ProblematicaImp;
import com.sistematutoriascomp.sistematutorias.dominio.TutoriaImp;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutor;
import com.sistematutoriascomp.sistematutorias.utilidad.Sesion;
import com.sistematutoriascomp.sistematutorias.utilidad.Utilidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class FXMLMenuGestionarTutoriasController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLMenuGestionarTutoriasController.class);

    @FXML
    private Button btnRegistrarHorario;
    @FXML
    private Button btnRegistrarAsistencia;
    @FXML
    private Button btnRegistrarFecha;
    @FXML
    private Button btnAsignarTutorado;
    @FXML
    private Button btnRegistrarTutorado;
    @FXML
    private Button btnEditarTutorado;
    @FXML
    private Button btnMisTutorados;
    @FXML
    private Button btnEditarHorario;
    @FXML
    private Button btnEditarFecha;
    @FXML
    private Button btnConsultarProblematicas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        Tutor tutor = Sesion.getTutorSesion();
        String rol = Sesion.getRolActual();

        if (tutor != null) {
            if (rol.equals("ACADEMICO")) {
                btnRegistrarHorario.setVisible(true);
                btnRegistrarAsistencia.setVisible(true);
                btnRegistrarFecha.setVisible(false);
                btnAsignarTutorado.setVisible(false);
                btnRegistrarTutorado.setVisible(false);
                btnMisTutorados.setVisible(true);
                btnEditarHorario.setVisible(true);
                btnConsultarProblematicas.setVisible(true);
            } else if (rol.equals("COORDINADOR")) {
                btnRegistrarHorario.setVisible(true);
                btnRegistrarAsistencia.setVisible(true);
                btnRegistrarFecha.setVisible(true);
                btnAsignarTutorado.setVisible(true);
                btnRegistrarTutorado.setVisible(true);
                btnEditarTutorado.setVisible(true);
                btnMisTutorados.setVisible(true);
                btnEditarHorario.setVisible(true);
                btnEditarFecha.setVisible(true);
                btnConsultarProblematicas.setVisible(true);
            } else if (rol.equals("ADMINISTRADOR")) {
                btnRegistrarHorario.setVisible(true);
                btnRegistrarAsistencia.setVisible(true);
                btnRegistrarFecha.setVisible(true);
                btnAsignarTutorado.setVisible(true);
                btnRegistrarTutorado.setVisible(true);
                btnEditarTutorado.setVisible(true);
            }
        }
    }

    @FXML
    private void clicRegistrarHorario(ActionEvent event) {
        HashMap<String, Object> respuesta = TutoriaImp.obtenerFechasPeriodoActual();
        if (!(boolean) respuesta.get("error")) {
            irPantalla("/tutoria/FXMLRegistrarHoraTutoria.fxml", "Registrar Hora de Tutoría", event);
        } else {
            Utilidades.mostrarAlertaSimple("No se puede continuar",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegistrarAsistencia(ActionEvent event) {
        int idTutor = Sesion.getTutorSesion().getIdTutor();

        HashMap<String, Object> respuesta = AsistenciaImp.obtenerSesionesTutor(idTutor);

        if (!(boolean) respuesta.get("error")) {
            irPantalla("/tutoria/FXMLRegistrarAsistenciaTutorado.fxml", "Registrar Asistencia", event);
        } else {
            Utilidades.mostrarAlertaSimple("No se puede continuar",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegistrarFecha(ActionEvent event) {
        irPantalla("/tutoria/FXMLRegistrarFechaTutoria.fxml", "Registrar Fecha de Tutoría", event);
    }

    @FXML
    private void clicAsignarTutorado(ActionEvent event) {
        irPantalla("/tutoria/FXMLAsignarTutorado.fxml", "Asignar Tutorado", event);
    }

    @FXML
    private void clicEditarTutorado(ActionEvent event) {
        irPantalla("/tutoria/FXMLListaTutorados.fxml", "Gestión de Tutorados", event);
    }

    @FXML
    private void clicMisTutorados(ActionEvent event) {
        irPantalla("/tutoria/FXMLMisTutorados.fxml", "Mis Tutorados", event);
    }

    @FXML
    private void clicConsultarProblematicas(ActionEvent event) {
        HashMap<String, Object> respuesta = ProblematicaImp.obtenerProblematicasTutor();
        if (!(boolean) respuesta.get("error")) {
            irPantalla("/tutoria/FXMLConsultarProblematicaTutorado.fxml", "Consultar Problemáticas", event);
        } else {
            Utilidades.mostrarAlertaSimple("Sin problemáticas",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicEditarFecha(ActionEvent event) {
        HashMap<String, Object> respuesta = FechaTutoriaImp.obtenerFechasTutoria();
        if (!(boolean) respuesta.get("error")) {
            irPantalla("/tutoria/FXMLEditarFechaTutoria.fxml", "Editar Fecha de Tutoría", event);
        } else {
            Utilidades.mostrarAlertaSimple("Sin fechas registradas",
                    "Actualmente no hay fechas de tutoría registradas.",
                    Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void clicEditarHorario(ActionEvent event) {
        HashMap<String, Object> respuesta = TutoriaImp.obtenerTutoriasRegistradasTutor();
        if (!(boolean) respuesta.get("error")) {
            irPantalla("/tutoria/FXMLEditarHoraTutoria.fxml", "Editar Horario de Tutoría", event);
        } else {
            Utilidades.mostrarAlertaSimple("No se puede continuar",
                    (String) respuesta.get("mensaje"),
                    Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void clicRegistrarTutorado() {
        try {
            Utilidades.openModal("/tutoria/FXMLRegistrarTutorado.fxml", "Registrar Tutorado");
        } catch (IOException e) {
            manejarError("Error al cambiar a la ventana de registro de tutorado", e,
                    "No se pudo abrir la ventana de registro de tutorado.");
        } catch (Exception ex) {
            manejarError("Error inesperado al cambiar a la ventana de registro de tutorado", ex,
                    "Ocurrió un error inesperado al abrir la ventana de registro de tutorado.");
        }
    }

    private void irPantalla(String ruta, String titulo, ActionEvent event) {
        try {
            Utilidades.goToWindow(ruta, event, titulo);
        } catch (IOException ex) {
            manejarError("Error al cambiar de ventana hacia " + ruta, ex, "No se pudo cambiar de ventana.");
        } catch (NullPointerException ex) {
            manejarError("Error de puntero nulo al cambiar de ventana hacia " + ruta, ex,
                    "Ocurrió un error inesperado al cambiar de ventana.");
        } catch (Exception e) {
            manejarError("Error inesperado al cambiar de ventana hacia " + ruta, e,
                    "Ocurrió un error inesperado al cambiar de ventana.");
        }
    }

    @FXML
    private void clicVolverMenuPrincipal(ActionEvent event) {
        try {
            Utilidades.volverMenuPrincipal(event);
        } catch (IOException ex) {
            manejarError("Error al volver al menú principal", ex, "No se pudo volver al menú principal.");
        } catch (Exception e) {
            manejarError("Error inesperado al volver al menú principal", e,
                    "Ocurrió un error inesperado al volver al menú principal.");
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
