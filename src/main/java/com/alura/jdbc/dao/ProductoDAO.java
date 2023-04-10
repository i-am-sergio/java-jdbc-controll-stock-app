package com.alura.jdbc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alura.jdbc.modelo.Producto;

public class ProductoDAO {
	private Connection conn;
	
	public ProductoDAO(Connection conn) {
		this.conn = conn;
	}
	
	public void guardar(Producto producto)  {
		try {
			//conn.setAutoCommit(false);
			final PreparedStatement statement = conn.prepareStatement(
					"INSERT INTO producto(nombre, description, cantidad, categoria_id) VALUES (?,?,?,?)",
					Statement.RETURN_GENERATED_KEYS
					);
			
			try(statement){
				statement.setString(1, producto.getNombre());
				statement.setString(2, producto.getDescripcion());
				statement.setInt(3, producto.getCantidad());
				statement.setInt(4, producto.getCategoriaId());
				statement.execute();
				
				final ResultSet resultSet = statement.getGeneratedKeys();
				try(resultSet) {
					while(resultSet.next()) {
						producto.setId(resultSet.getInt(1));
						System.out.println(String.format("Fue insertado el producto de ID: %s",producto));
					}
				}
			} 
			
		} catch (SQLException e) {
			throw new RuntimeException(e);			
		}
	}

	public Connection getConn() {
		return conn;
	}

	public List<Producto> listar() {
		List<Producto> resultado = new ArrayList<>();
		
		try {
			final PreparedStatement statement = conn.prepareStatement("SELECT id, nombre, description, cantidad FROM producto");
			
			try(statement){
				statement.execute();
				
				final ResultSet resultSet = statement.getResultSet();
				try(resultSet){
					while(resultSet.next()) {
						Producto fila = new Producto(
								resultSet.getInt("id"),
								resultSet.getString("nombre"),
								resultSet.getString("description"),
								resultSet.getInt("cantidad")
								); 
						resultado.add(fila);
					}					
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return resultado;
	}

	public int eliminar(Integer id) {
		try {
			final PreparedStatement statement = conn.prepareStatement("DELETE FROM producto WHERE id=?");
			try(statement){
				statement.setInt(1, id);
				statement.execute();
				return statement.getUpdateCount();			
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int modificar(String nombre, String descripcion,Integer cantidad, Integer id) {
		try {
			final PreparedStatement statement = conn.prepareStatement("UPDATE producto SET nombre=?, description=?, cantidad=? WHERE id=?");
			try(statement){
				statement.setString(1, nombre);
				statement.setString(2, descripcion);
				statement.setInt(3, cantidad);
				statement.setInt(4, id);
				statement.execute();
				int updateCount = statement.getUpdateCount();
				return updateCount;							
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Producto> listar(Integer categoriaId) {
		List<Producto> resultado = new ArrayList<>();
		
		try {
			var querySelect = "SELECT id, nombre, description, cantidad FROM producto WHERE categoria_id = ?";
			System.out.println(querySelect);
			final PreparedStatement statement = conn.prepareStatement(querySelect);
			
			try(statement){
				statement.setInt(1, categoriaId);
				statement.execute();
				
				final ResultSet resultSet = statement.getResultSet();
				try(resultSet){
					while(resultSet.next()) {
						Producto fila = new Producto(
								resultSet.getInt("id"),
								resultSet.getString("nombre"),
								resultSet.getString("description"),
								resultSet.getInt("cantidad")
								); 
						resultado.add(fila);
					}					
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return resultado;
	}
}
