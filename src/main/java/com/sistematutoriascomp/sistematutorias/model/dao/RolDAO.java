/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 3.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;
import com.sistematutoriascomp.sistematutorias.model.pojo.Rol;

public class RolDAO {
    private static final String SQL_SELECT_ALL = "SELECT * FROM rol";
    private static final String SQL_SELECT_ROLENAME_BY_ID = "SELECT nombre FROM rol WHERE idRol = ?";

    public String obtenerRolPorId(int idRol) throws SQLException {
        String rol = null;

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ROLENAME_BY_ID);
                statement.setInt(1, idRol);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        rol = resultSet.getString("nombre");
                    }
                }
            }
        }

        return rol;
    }

    public List<Rol> obtenerTodosRoles() throws SQLException {
        List<Rol> roles = new ArrayList<>();

        try (Connection connection = ConexionBaseDatos.abrirConexionBD()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(SQL_SELECT_ALL);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        roles.add(mapResultSetToRol(resultSet));
                    }
                }
            }
        }

        return roles;
    }

    private Rol mapResultSetToRol(ResultSet resultSet) throws SQLException {
        Rol rol = new Rol();
        rol.setIdRol(resultSet.getInt("idRol"));
        rol.setNombreRol(resultSet.getString("nombre"));
        return rol;
    }
}
