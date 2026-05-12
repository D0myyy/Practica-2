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
import model.Aeroport;
import model.Avion;
import model.Zbor;

public class ZborDAO {
	private final AeroportDAO aeroportDAO = new AeroportDAO();
	private final AvionDAO avionDAO = new AvionDAO();

	public List<Zbor> findAll() {
		List<Zbor> zboruri = new ArrayList<>();
		String sql = "SELECT IdZbor, NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire FROM Zboruri ORDER BY IdZbor";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				zboruri.add(map(resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la incarcarea zborurilor.", exception);
		}
		return zboruri;
	}

	public Zbor findById(int idZbor) {
		String sql = "SELECT IdZbor, NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire FROM Zboruri WHERE IdZbor = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idZbor);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? map(resultSet) : null;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea zborului.", exception);
		}
	}

	public int create(Zbor zbor) {
		String sql = "INSERT INTO Zboruri (NumarZbor, IdAeroportPlecare, IdAeroportSosire, IdAvion, DataPlecare, DataSosire) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, zbor.getNumarZbor());
			statement.setInt(2, zbor.getPlecare().getIdAeroport());
			statement.setInt(3, zbor.getSosire().getIdAeroport());
			statement.setInt(4, zbor.getAvion().getIdAvion());
			statement.setTimestamp(5, Timestamp.valueOf(zbor.getDataPlecare()));
			statement.setTimestamp(6, Timestamp.valueOf(zbor.getDataSosire()));
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la salvarea zborului.", exception);
		}
	}

	public boolean update(Zbor zbor) {
		String sql = "UPDATE Zboruri SET NumarZbor = ?, IdAeroportPlecare = ?, IdAeroportSosire = ?, IdAvion = ?, DataPlecare = ?, DataSosire = ? WHERE IdZbor = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, zbor.getNumarZbor());
			statement.setInt(2, zbor.getPlecare().getIdAeroport());
			statement.setInt(3, zbor.getSosire().getIdAeroport());
			statement.setInt(4, zbor.getAvion().getIdAvion());
			statement.setTimestamp(5, Timestamp.valueOf(zbor.getDataPlecare()));
			statement.setTimestamp(6, Timestamp.valueOf(zbor.getDataSosire()));
			statement.setInt(7, zbor.getIdZbor());
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la actualizarea zborului.", exception);
		}
	}

	public boolean delete(int idZbor) {
		String sql = "DELETE FROM Zboruri WHERE IdZbor = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idZbor);
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la stergerea zborului.", exception);
		}
	}

	private Zbor map(ResultSet resultSet) throws SQLException {
		Aeroport plecare = aeroportDAO.findById(resultSet.getInt("IdAeroportPlecare"));
		Aeroport sosire = aeroportDAO.findById(resultSet.getInt("IdAeroportSosire"));
		Avion avion = avionDAO.findById(resultSet.getInt("IdAvion"));
		return new Zbor(
				resultSet.getInt("IdZbor"),
				resultSet.getString("NumarZbor"),
				plecare,
				sosire,
				avion,
				resultSet.getTimestamp("DataPlecare").toLocalDateTime(),
				resultSet.getTimestamp("DataSosire").toLocalDateTime());
	}
}
