/*
 * Autor: Soria Vazquez Mariana
 * Ultima modificación hecha por: Soria Vazquez Mariana
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;

import java.sql.Connection;
import java.sql.SQLException;

public class AsignacionTutorDAO {
    private static final String SQL_INSERT = "INSERT INTO asignaciontutor (idTutor, idTutorado, idPeriodo) VALUES (?, ?, ?)";

    public static boolean registrarAsignacion(int idTutor, int idTutorado, int idPeriodo) throws SQLException {
        boolean resultado = false;

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                var statement = connection.prepareStatement(SQL_INSERT);
                statement.setInt(1, idTutor);
                statement.setInt(2, idTutorado);
                statement.setInt(3, idPeriodo);
                resultado = (statement.executeUpdate() > 0);
            }
        }

        return resultado;
    }
}
