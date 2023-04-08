package com.alura.jdbc.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alura.jdbc.factory.ConnectionFactory;

public class ProductoController {

	public int modificar(String nombre, String descripcion, Integer id) throws SQLException  {
		final Connection conn = new ConnectionFactory().recuperaConexion();
		try(conn){
			final PreparedStatement statement = conn.prepareStatement("UPDATE producto SET nombre=?, description=? WHERE id=?");
			try(statement){
				statement.setString(1, nombre);
				statement.setString(2, descripcion);
				statement.setInt(3, id);
				statement.execute();
				int updateCount = statement.getUpdateCount();
				return updateCount;							
			}
		}
	}

	public int eliminar(Integer id) throws SQLException {
		final Connection conn = new ConnectionFactory().recuperaConexion();
		try(conn){
			PreparedStatement statement = conn.prepareStatement("DELETE FROM producto WHERE id=?");
			statement.setInt(1, id);
			statement.execute();
			return statement.getUpdateCount();			
		}
	}

	public List<Map<String,String>> listar() throws SQLException {
		final Connection conn = new ConnectionFactory().recuperaConexion();
		try(conn){
			final PreparedStatement statement = conn.prepareStatement("SELECT id, nombre, description, cantidad FROM producto");
			try(statement){
				statement.execute();
				ResultSet resultSet = statement.getResultSet();
				List<Map<String, String>> resultado = new ArrayList<>();
				
				while(resultSet.next()) {
					Map<String, String> fila = new HashMap<>(); 
					fila.put("id", String.valueOf(resultSet.getInt("id")));
					fila.put("nombre", resultSet.getString("nombre"));
					fila.put("description", resultSet.getString("description"));
					fila.put("cantidad", String.valueOf(resultSet.getInt("cantidad")));
					resultado.add(fila);
				}
				return resultado;
			}
		}		
	}

    public void guardar(Map<String,String> producto) throws SQLException {
    	String nombre = producto.get("nombre");
    	String descripcion = producto.get("description");
    	Integer cantidad = Integer.valueOf(producto.get("cantidad"));
    	Integer maximoCantidad = 50;
    	
		final Connection conn = new ConnectionFactory().recuperaConexion();
		
		try(conn){
			conn.setAutoCommit(false);
			final PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO producto(nombre, description, cantidad) VALUES (?,?,?)",
					Statement.RETURN_GENERATED_KEYS
					);
			try(statement){
				do {
					int cantidadParaGuardar = Math.min(cantidad, maximoCantidad);
					ejecutarRegistro(nombre, descripcion, cantidadParaGuardar, statement);			
					cantidad -= maximoCantidad;
				} while(cantidad > 0);
				conn.commit();
			} catch (Exception e) {
				conn.rollback();
			}			
		}
    }

	private void ejecutarRegistro(String nombre, String descripcion, Integer cantidad, PreparedStatement statement)
			throws SQLException {
		
		statement.setString(1, nombre);
		statement.setString(2, descripcion);
		statement.setInt(3, cantidad);
		statement.execute();
		
		final ResultSet resultSet = statement.getGeneratedKeys();
		try(resultSet) {
			while(resultSet.next()) {
				System.out.println(String.format("Fue insertado el producto de ID: %d", resultSet.getInt(1)));
			}
		}
	}
}
