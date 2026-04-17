/*
 * Autor: Delgado Santiago Darlington
 * Ultima modificación hecha por: Delgado Santiago Darlington
 * Versión: 2.0
 */
package com.sistematutoriascomp.sistematutorias.model.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.sistematutoriascomp.sistematutorias.model.ConexionBaseDatos;

public abstract class BaseDaoTest {
    protected Connection connection;

    @BeforeEach
    void openConnection() throws SQLException {
        connection = ConexionBaseDatos.abrirConexionBD();
    }

    @AfterEach
    void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        ConexionBaseDatos.cerrarConexionBD();
    }

    protected void execute(String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    protected void reopenConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = ConexionBaseDatos.abrirConexionBD();
        }
    }

    @BeforeAll
    static void setupProperties() {
        System.setProperty("db.url", "jdbc:h2:mem:tutoriatestbd;MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1");
        System.setProperty("db.user", "sa");
        System.setProperty("db.password", "");
        System.setProperty("db.driver", "org.h2.Driver");
        System.setProperty("db.options", "");
    }
}
