package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Avion;

public class AvionDAO {
	public List<Avion> findAll() {
		List<Avion> avioane = new ArrayList<>();
		String sql = "SELECT IdAvion, Model, Capacitate FROM Avioane ORDER BY IdAvion";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				avioane.add(map(resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la incarcarea avioanelor.", exception);
		}
		return avioane;
	}

	public Avion findById(int idAvion) {
		String sql = "SELECT IdAvion, Model, Capacitate FROM Avioane WHERE IdAvion = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idAvion);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? map(resultSet) : null;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea avionului.", exception);
		}
	}

	public int create(Avion avion) {
		String sql = "INSERT INTO Avioane (Model, Capacitate) VALUES (?, ?)";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, avion.getModel());
			statement.setInt(2, avion.getCapacitate());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la salvarea avionului.", exception);
		}
	}

	public boolean update(Avion avion) {
		String sql = "UPDATE Avioane SET Model = ?, Capacitate = ? WHERE IdAvion = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, avion.getModel());
			statement.setInt(2, avion.getCapacitate());
			statement.setInt(3, avion.getIdAvion());
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la actualizarea avionului.", exception);
		}
	}

	public boolean delete(int idAvion) {
		String sql = "DELETE FROM Avioane WHERE IdAvion = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idAvion);
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la stergerea avionului.", exception);
		}
	}

	private Avion map(ResultSet resultSet) throws SQLException {
		return new Avion(
				resultSet.getInt("IdAvion"),
				resultSet.getString("Model"),
				resultSet.getInt("Capacitate"));
	}
}
