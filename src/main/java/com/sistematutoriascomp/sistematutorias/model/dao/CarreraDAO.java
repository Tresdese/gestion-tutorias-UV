package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.Carrera;

public class CarreraDAO {
    private static final String SQL_SELECT_ALL = "SELECT * FROM carrera";

    public List<Carrera> obtenerTodasCarreras() throws SQLException {
        List<Carrera> carreras = new ArrayList<>();

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        carreras.add(mapResultSetToCarrera(resultSet));
                    }
                }
            }
        }

        return carreras;
    }

    private Carrera mapResultSetToCarrera(ResultSet resultSet) throws SQLException {
        Carrera carrera = new Carrera();
        carrera.setIdCarrera(resultSet.getInt("idCarrera"));
        carrera.setNombre(resultSet.getString("nombre"));
        return carrera;
    }
}
