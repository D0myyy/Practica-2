package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Rezervare;

public class RezervareDAO {
	public List<Rezervare> findAll() {
		List<Rezervare> rezervari = new ArrayList<>();
		String sql = "SELECT IdRezervare, CodRezervare, DataRezervare FROM Rezervari ORDER BY IdRezervare";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				rezervari.add(map(resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la incarcarea rezervarilor.", exception);
		}
		return rezervari;
	}

	public Rezervare findById(int idRezervare) {
		String sql = "SELECT IdRezervare, CodRezervare, DataRezervare FROM Rezervari WHERE IdRezervare = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idRezervare);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? map(resultSet) : null;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea rezervarii.", exception);
		}
	}

	public int create(Rezervare rezervare) {
		String sql = "INSERT INTO Rezervari (CodRezervare, DataRezervare) VALUES (?, ?)";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, rezervare.getCodRezervare());
			statement.setTimestamp(2, Timestamp.valueOf(rezervare.getDataRezervare()));
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la salvarea rezervarii.", exception);
		}
	}

	public boolean update(Rezervare rezervare) {
		String sql = "UPDATE Rezervari SET CodRezervare = ?, DataRezervare = ? WHERE IdRezervare = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, rezervare.getCodRezervare());
			statement.setTimestamp(2, Timestamp.valueOf(rezervare.getDataRezervare()));
			statement.setInt(3, rezervare.getIdRezervare());
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la actualizarea rezervarii.", exception);
		}
	}

	public boolean delete(int idRezervare) {
		String sql = "DELETE FROM Rezervari WHERE IdRezervare = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idRezervare);
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la stergerea rezervarii.", exception);
		}
	}

	private Rezervare map(ResultSet resultSet) throws SQLException {
		return new Rezervare(
				resultSet.getInt("IdRezervare"),
				resultSet.getString("CodRezervare"),
				resultSet.getTimestamp("DataRezervare").toLocalDateTime());
	}
}
