package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Loc;
import model.enums.ClasaLoc;

public class LocDAO {
	public List<Loc> findAll() {
		List<Loc> locuri = new ArrayList<>();
		String sql = "SELECT IdLoc, IdAvion, NumarLoc, Clasa FROM Locuri ORDER BY IdLoc";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				locuri.add(map(resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la incarcarea locurilor.", exception);
		}
		return locuri;
	}

	public Loc findById(int idLoc) {
		String sql = "SELECT IdLoc, IdAvion, NumarLoc, Clasa FROM Locuri WHERE IdLoc = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idLoc);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? map(resultSet) : null;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea locului.", exception);
		}
	}

	public List<Loc> findByAvionId(int idAvion) {
		List<Loc> locuri = new ArrayList<>();
		String sql = "SELECT IdLoc, IdAvion, NumarLoc, Clasa FROM Locuri WHERE IdAvion = ? ORDER BY IdLoc";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idAvion);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					locuri.add(map(resultSet));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea locurilor pentru avion.", exception);
		}
		return locuri;
	}

	public int create(Loc loc) {
		String sql = "INSERT INTO Locuri (IdAvion, NumarLoc, Clasa) VALUES (?, ?, ?)";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, loc.getIdAvion());
			statement.setString(2, loc.getNumarLoc());
			statement.setString(3, loc.getClasaLoc().name().substring(0, 1) + loc.getClasaLoc().name().substring(1).toLowerCase());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la salvarea locului.", exception);
		}
	}

	public boolean update(Loc loc) {
		String sql = "UPDATE Locuri SET IdAvion = ?, NumarLoc = ?, Clasa = ? WHERE IdLoc = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, loc.getIdAvion());
			statement.setString(2, loc.getNumarLoc());
			statement.setString(3, loc.getClasaLoc().name().substring(0, 1) + loc.getClasaLoc().name().substring(1).toLowerCase());
			statement.setInt(4, loc.getIdLoc());
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la actualizarea locului.", exception);
		}
	}

	public boolean delete(int idLoc) {
		String sql = "DELETE FROM Locuri WHERE IdLoc = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idLoc);
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la stergerea locului.", exception);
		}
	}

	private Loc map(ResultSet resultSet) throws SQLException {
		String clasa = resultSet.getString("Clasa").trim().toUpperCase();
		return new Loc(
				resultSet.getInt("IdLoc"),
				resultSet.getInt("IdAvion"),
				resultSet.getString("NumarLoc"),
				ClasaLoc.valueOf(clasa));
	}
}
