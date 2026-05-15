package com.sistematutoriascomp.sistematutorias.controller.tutoria;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sistematutoriascomp.sistematutorias.model.dao.CarreraDAO;
import com.sistematutoriascomp.sistematutorias.model.pojo.Carrera;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutorado;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXMLDetallesTutoradoController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger(FXMLDetallesTutoradoController.class);

    @FXML
    private Label lbEstatus;
    @FXML
    private Label lbCarrera;
    @FXML
    private Label lbMatricula;
    @FXML
    private Label lbSemestre;
    @FXML
    private Label lbCorreo;

    private final CarreraDAO carreraDAO = new CarreraDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void inicializarValores(Tutorado tutorado) {
        lbEstatus.setText(tutorado.isActivo() ? "Activo" : "Inactivo");
        lbMatricula.setText(tutorado.getMatricula() != null ? tutorado.getMatricula() : "—");
        lbSemestre.setText(String.valueOf(tutorado.getSemestre()));
        lbCorreo.setText(tutorado.getCorreo() != null ? tutorado.getCorreo() : "—");
        lbCarrera.setText(obtenerNombreCarrera(tutorado.getIdCarrera()));
    }

    private String obtenerNombreCarrera(int idCarrera) {
        try {
            List<Carrera> carreras = carreraDAO.obtenerTodasCarreras();
            return carreras.stream()
                    .filter(c -> c.getIdCarrera() == idCarrera)
                    .map(Carrera::getNombre)
                    .findFirst()
                    .orElse("—");
        } catch (SQLException ex) {
            LOGGER.error("Error al obtener nombre de carrera para idCarrera={}", idCarrera, ex);
            return "—";
        }
    }

    @FXML
    private void clicCerrar(ActionEvent event) {
        Stage stage = (Stage) lbEstatus.getScene().getWindow();
        stage.close();
    }
}
