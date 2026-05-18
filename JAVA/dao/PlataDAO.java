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
import model.Plata;
import model.Rezervare;
import model.enums.MetodaPlata;
import model.enums.StatusPlata;

public class PlataDAO {
	private final RezervareDAO rezervareDAO = new RezervareDAO();

	public List<Plata> findAll() {
		List<Plata> plati = new ArrayList<>();
		String sql = "SELECT IdPlata, IdRezervare, Suma, Metoda, Status, DataPlata FROM Plati ORDER BY IdPlata";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				plati.add(map(resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la incarcarea platilor.", exception);
		}
		return plati;
	}

	public Plata findById(int idPlata) {
		String sql = "SELECT IdPlata, IdRezervare, Suma, Metoda, Status, DataPlata FROM Plati WHERE IdPlata = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idPlata);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? map(resultSet) : null;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea platii.", exception);
		}
	}

	public List<Plata> findByRezervareId(int idRezervare) {
		List<Plata> plati = new ArrayList<>();
		String sql = "SELECT IdPlata, IdRezervare, Suma, Metoda, Status, DataPlata FROM Plati WHERE IdRezervare = ? ORDER BY IdPlata";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idRezervare);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					plati.add(map(resultSet));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea platilor pentru rezervare.", exception);
		}
		return plati;
	}

	public int create(Plata plata) {
		String sql = "INSERT INTO Plati (IdRezervare, Suma, Metoda, Status, DataPlata) VALUES (?, ?, ?, ?, ?)";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, plata.getRezervare().getIdRezervare());
			statement.setDouble(2, plata.getSuma());
			statement.setString(3, toDbValue(plata.getMetoda()));
			statement.setString(4, toDbValue(plata.getStatus()));
			statement.setTimestamp(5, Timestamp.valueOf(plata.getDataPlata()));
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la salvarea platii.", exception);
		}
	}

	public boolean update(Plata plata) {
		String sql = "UPDATE Plati SET IdRezervare = ?, Suma = ?, Metoda = ?, Status = ?, DataPlata = ? WHERE IdPlata = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, plata.getRezervare().getIdRezervare());
			statement.setDouble(2, plata.getSuma());
			statement.setString(3, toDbValue(plata.getMetoda()));
			statement.setString(4, toDbValue(plata.getStatus()));
			statement.setTimestamp(5, Timestamp.valueOf(plata.getDataPlata()));
			statement.setInt(6, plata.getIdPlata());
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la actualizarea platii.", exception);
		}
	}

	public boolean delete(int idPlata) {
		String sql = "DELETE FROM Plati WHERE IdPlata = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idPlata);
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la stergerea platii.", exception);
		}
	}

	private Plata map(ResultSet resultSet) throws SQLException {
		Rezervare rezervare = rezervareDAO.findById(resultSet.getInt("IdRezervare"));
		Plata plata = new Plata(
				resultSet.getInt("IdPlata"),
				rezervare,
				resultSet.getDouble("Suma"),
				MetodaPlata.valueOf(resultSet.getString("Metoda").trim().toUpperCase()),
				StatusPlata.valueOf(resultSet.getString("Status").trim().toUpperCase().replace(' ', '_')),
				resultSet.getTimestamp("DataPlata").toLocalDateTime());
		return plata;
	}

	private String toDbValue(MetodaPlata metodaPlata) {
		switch (metodaPlata) {
			case CARD:
				return "Card";
			case CASH:
				return "Cash";
			case TRANSFER:
				return "Transfer";
			default:
				return "Card";
		}
	}

	private String toDbValue(StatusPlata statusPlata) {
		switch (statusPlata) {
			case PLATIT:
				return "Platit";
			case REFUZAT:
				return "Refuzat";
			case IN_ASTEPTARE:
				return "In asteptare";
			default:
				return "In asteptare";
		}
	}
}
