/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 6.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.Tutorado;

public class TutoradoDAO {
    private static final String SQL_INSERT = "INSERT INTO tutorado (matricula, nombre, apellidoPaterno, apellidoMaterno, correo, idCarrera, semestre, esActivo, idTutor) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE tutorado SET nombre = ?, apellidoPaterno = ?, apellidoMaterno = ?, correo = ?, idCarrera = ?, semestre = ?, esActivo = ?, idTutor = ? WHERE matricula = ?";
    private static final String SQL_UPDATE_ASIGNAR_TUTOR = "UPDATE tutorado SET idTutor = ? WHERE idTutorado = ?";
    private static final String SQL_DELETE = "DELETE FROM tutorado WHERE idTutorado = ?";
    private static final String SQL_SELECT_BY_MATRICULA = "SELECT * FROM tutorado WHERE matricula = ?";
    private static final String SQL_SELECT_ALL = "SELECT * FROM tutorado";
    private static final String SQL_SELECT_ALL_POR_ASIGNAR = "SELECT * FROM tutorado WHERE idTutor IS NULL AND esActivo = 1";
    private static final String SQL_SELECT_POR_TUTOR = "SELECT * FROM tutorado WHERE idTutor = ?";
    private static final String SQL_BAJA = "UPDATE tutorado SET esActivo = 0 WHERE matricula = ?";

    public boolean insertarTutorado(Tutorado tutorado) throws SQLException {
        boolean resultado = false;
        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_INSERT);
                statement.setString(1, tutorado.getMatricula());
                statement.setString(2, tutorado.getNombre());
                statement.setString(3, tutorado.getApellidoPaterno());
                statement.setString(4, tutorado.getApellidoMaterno());
                statement.setString(5, tutorado.getCorreo());
                statement.setInt(6, tutorado.getIdCarrera());
                statement.setInt(7, tutorado.getSemestre());
                statement.setBoolean(8, tutorado.isActivo());
                if (tutorado.getIdTutor() == 0) {
                    statement.setNull(9, java.sql.Types.INTEGER);
                } else {
                    statement.setInt(9, tutorado.getIdTutor());
                }
                resultado = statement.executeUpdate() > 0;
            }
        }
        return resultado;
    }

    public boolean updateTutorado(Tutorado tutorado) throws SQLException {
        boolean resultado = false;
        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_UPDATE);
                statement.setString(1, tutorado.getNombre());
                statement.setString(2, tutorado.getApellidoPaterno());
                statement.setString(3, tutorado.getApellidoMaterno());
                statement.setString(4, tutorado.getCorreo());
                statement.setInt(5, tutorado.getIdCarrera());
                statement.setInt(6, tutorado.getSemestre());
                statement.setBoolean(7, tutorado.isActivo());
                statement.setInt(8, tutorado.getIdTutor());
                statement.setString(9, tutorado.getMatricula());
                resultado = statement.executeUpdate() > 0;
            }
        }
        return resultado;
    }

    public boolean deleteTutorado(int idTutorado) throws SQLException {
        boolean resultado = false;
        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_DELETE);
                statement.setInt(1, idTutorado);
                resultado = statement.executeUpdate() > 0;
            }
        }
        return resultado;
    }

    public Tutorado searchTutoradoByMatricula(String matricula) throws SQLException {
        Tutorado tutorado = null;
        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_SELECT_BY_MATRICULA);
                statement.setString(1, matricula);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    tutorado = mapResultSetToTutorado(resultSet);
                }
            }
        }
        return tutorado;
    }

    public List<Tutorado> obtenerTutoradosSinTutor() throws SQLException {
        List<Tutorado> tutorados = new ArrayList<>();

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_SELECT_ALL_POR_ASIGNAR);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Tutorado tutorado = mapResultSetToTutorado(resultSet);
                    tutorados.add(tutorado);
                }
            }
        }
        return tutorados;
    }

    public boolean darBajaTutorado(String matricula) throws SQLException {
        boolean resultado = false;
        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_BAJA);
                statement.setString(1, matricula);
                resultado = statement.executeUpdate() > 0;
            }
        }
        return resultado;
    }

    public boolean asignarTutor(int idTutorado, int idTutor) throws SQLException {
        boolean resultado = false;

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_UPDATE_ASIGNAR_TUTOR);
                statement.setInt(1, idTutor);
                statement.setInt(2, idTutorado);
                resultado = statement.executeUpdate() > 0;
            }
        }
        return resultado;
    }

    private Tutorado mapResultSetToTutorado(ResultSet resultSet) throws SQLException {
        Tutorado tutorado = new Tutorado();
        tutorado.setIdTutorado(resultSet.getInt("idTutorado"));
        tutorado.setMatricula(resultSet.getString("matricula"));
        tutorado.setNombre(resultSet.getString("nombre"));
        tutorado.setApellidoPaterno(resultSet.getString("apellidoPaterno"));
        tutorado.setApellidoMaterno(resultSet.getString("apellidoMaterno"));
        tutorado.setCorreo(resultSet.getString("correo"));
        tutorado.setIdCarrera(resultSet.getInt("idCarrera"));
        tutorado.setSemestre(resultSet.getInt("semestre"));
        tutorado.setActivo(resultSet.getBoolean("esActivo"));
        tutorado.setIdTutor(resultSet.getInt("idTutor"));
        return tutorado;
    }

    public List<Tutorado> obtenerTutoradosPorTutor(int idTutor) throws SQLException {
        List<Tutorado> tutorados = new ArrayList<>();
        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_SELECT_POR_TUTOR);
                statement.setInt(1, idTutor);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    tutorados.add(mapResultSetToTutorado(resultSet));
                }
            }
        }
        return tutorados;
    }

    public List<Tutorado> getAllTutorados() throws SQLException {
        List<Tutorado> tutorados = new ArrayList<>();
        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_SELECT_ALL);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Tutorado tutorado = mapResultSetToTutorado(resultSet);
                    tutorados.add(tutorado);
                }
            }
        }
        return tutorados;
    }
}
