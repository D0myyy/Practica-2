package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Aeroport;

public class AeroportDAO {
	public List<Aeroport> findAll() {
		List<Aeroport> aeroporturi = new ArrayList<>();
		String sql = "SELECT IdAeroport, Nume, Oras, Tara, CodIATA FROM Aeroporturi ORDER BY IdAeroport";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				aeroporturi.add(map(resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la incarcarea aeroporturilor.", exception);
		}
		return aeroporturi;
	}

	public Aeroport findById(int idAeroport) {
		String sql = "SELECT IdAeroport, Nume, Oras, Tara, CodIATA FROM Aeroporturi WHERE IdAeroport = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idAeroport);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? map(resultSet) : null;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea aeroportului.", exception);
		}
	}

	public int create(Aeroport aeroport) {
		String sql = "INSERT INTO Aeroporturi (Nume, Oras, Tara, CodIATA) VALUES (?, ?, ?, ?)";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, aeroport.getNume());
			statement.setString(2, aeroport.getOras());
			statement.setString(3, aeroport.getTara());
			statement.setString(4, aeroport.getCodIata());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la salvarea aeroportului.", exception);
		}
	}

	public boolean update(Aeroport aeroport) {
		String sql = "UPDATE Aeroporturi SET Nume = ?, Oras = ?, Tara = ?, CodIATA = ? WHERE IdAeroport = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, aeroport.getNume());
			statement.setString(2, aeroport.getOras());
			statement.setString(3, aeroport.getTara());
			statement.setString(4, aeroport.getCodIata());
			statement.setInt(5, aeroport.getIdAeroport());
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la actualizarea aeroportului.", exception);
		}
	}

	public boolean delete(int idAeroport) {
		String sql = "DELETE FROM Aeroporturi WHERE IdAeroport = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idAeroport);
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la stergerea aeroportului.", exception);
		}
	}

	private Aeroport map(ResultSet resultSet) throws SQLException {
		return new Aeroport(
				resultSet.getInt("IdAeroport"),
				resultSet.getString("Nume"),
				resultSet.getString("Oras"),
				resultSet.getString("Tara"),
				resultSet.getString("CodIATA"));
	}
}
