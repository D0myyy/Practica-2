package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Pasager;

public class PasagerDAO {
	public List<Pasager> findAll() {
		List<Pasager> pasageri = new ArrayList<>();
		String sql = "SELECT IdPasager, Nume, Prenume, Email, Telefon FROM Pasageri ORDER BY IdPasager";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				pasageri.add(map(resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la incarcarea pasagerilor.", exception);
		}
		return pasageri;
	}

	public Pasager findById(int idPasager) {
		String sql = "SELECT IdPasager, Nume, Prenume, Email, Telefon FROM Pasageri WHERE IdPasager = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idPasager);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? map(resultSet) : null;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea pasagerului.", exception);
		}
	}

	public List<Pasager> searchByName(String query) {
		List<Pasager> pasageri = new ArrayList<>();
		String sql = "SELECT IdPasager, Nume, Prenume, Email, Telefon FROM Pasageri WHERE Nume LIKE ? OR Prenume LIKE ? ORDER BY IdPasager";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			String value = "%" + query + "%";
			statement.setString(1, value);
			statement.setString(2, value);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					pasageri.add(map(resultSet));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea pasagerilor.", exception);
		}
		return pasageri;
	}

	public int create(Pasager pasager) {
		String sql = "INSERT INTO Pasageri (Nume, Prenume, Email, Telefon) VALUES (?, ?, ?, ?)";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, pasager.getNume());
			statement.setString(2, pasager.getPrenume());
			statement.setString(3, pasager.getEmail());
			statement.setString(4, pasager.getTelefon());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la salvarea pasagerului.", exception);
		}
	}

	public boolean update(Pasager pasager) {
		String sql = "UPDATE Pasageri SET Nume = ?, Prenume = ?, Email = ?, Telefon = ? WHERE IdPasager = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, pasager.getNume());
			statement.setString(2, pasager.getPrenume());
			statement.setString(3, pasager.getEmail());
			statement.setString(4, pasager.getTelefon());
			statement.setInt(5, pasager.getIdPasager());
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la actualizarea pasagerului.", exception);
		}
	}

	public boolean delete(int idPasager) {
		String sql = "DELETE FROM Pasageri WHERE IdPasager = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idPasager);
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la stergerea pasagerului.", exception);
		}
	}

	private Pasager map(ResultSet resultSet) throws SQLException {
		return new Pasager(
				resultSet.getInt("IdPasager"),
				resultSet.getString("Nume"),
				resultSet.getString("Prenume"),
				resultSet.getString("Email"),
				resultSet.getString("Telefon"));
	}
}
