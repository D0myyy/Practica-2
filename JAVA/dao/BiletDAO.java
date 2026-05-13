package dao;

import database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Bilet;
import model.Loc;
import model.Pasager;
import model.Rezervare;
import model.Zbor;
import model.enums.StatusBilet;

public class BiletDAO {
	private final RezervareDAO rezervareDAO = new RezervareDAO();
	private final PasagerDAO pasagerDAO = new PasagerDAO();
	private final ZborDAO zborDAO = new ZborDAO();
	private final LocDAO locDAO = new LocDAO();

	public List<Bilet> findAll() {
		List<Bilet> bilete = new ArrayList<>();
		String sql = "SELECT IdBilet, IdRezervare, IdPasager, IdZbor, IdLoc, Pret, Status FROM Bilete ORDER BY IdBilet";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				bilete.add(map(resultSet));
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la incarcarea biletelor.", exception);
		}
		return bilete;
	}

	public Bilet findById(int idBilet) {
		String sql = "SELECT IdBilet, IdRezervare, IdPasager, IdZbor, IdLoc, Pret, Status FROM Bilete WHERE IdBilet = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idBilet);
			try (ResultSet resultSet = statement.executeQuery()) {
				return resultSet.next() ? map(resultSet) : null;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea biletului.", exception);
		}
	}

	public List<Bilet> findByRezervareId(int idRezervare) {
		List<Bilet> bilete = new ArrayList<>();
		String sql = "SELECT IdBilet, IdRezervare, IdPasager, IdZbor, IdLoc, Pret, Status FROM Bilete WHERE IdRezervare = ? ORDER BY IdBilet";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idRezervare);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					bilete.add(map(resultSet));
				}
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la cautarea biletelor pentru rezervare.", exception);
		}
		return bilete;
	}

	public int create(Bilet bilet) {
		String sql = "INSERT INTO Bilete (IdRezervare, IdPasager, IdZbor, IdLoc, Pret, Status) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			statement.setInt(1, bilet.getRezervare().getIdRezervare());
			statement.setInt(2, bilet.getPasager().getIdPasager());
			statement.setInt(3, bilet.getZbor().getIdZbor());
			statement.setInt(4, bilet.getLoc().getIdLoc());
			statement.setDouble(5, bilet.getPret());
			statement.setString(6, toDbValue(bilet.getStatus()));
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : 0;
			}
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la salvarea biletului.", exception);
		}
	}

	public boolean update(Bilet bilet) {
		String sql = "UPDATE Bilete SET IdRezervare = ?, IdPasager = ?, IdZbor = ?, IdLoc = ?, Pret = ?, Status = ? WHERE IdBilet = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, bilet.getRezervare().getIdRezervare());
			statement.setInt(2, bilet.getPasager().getIdPasager());
			statement.setInt(3, bilet.getZbor().getIdZbor());
			statement.setInt(4, bilet.getLoc().getIdLoc());
			statement.setDouble(5, bilet.getPret());
			statement.setString(6, toDbValue(bilet.getStatus()));
			statement.setInt(7, bilet.getIdBilet());
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la actualizarea biletului.", exception);
		}
	}

	public boolean delete(int idBilet) {
		String sql = "DELETE FROM Bilete WHERE IdBilet = ?";
		try (Connection connection = DatabaseConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, idBilet);
			return statement.executeUpdate() > 0;
		} catch (SQLException exception) {
			throw new IllegalStateException("Eroare la stergerea biletului.", exception);
		}
	}

	private Bilet map(ResultSet resultSet) throws SQLException {
		Rezervare rezervare = rezervareDAO.findById(resultSet.getInt("IdRezervare"));
		Pasager pasager = pasagerDAO.findById(resultSet.getInt("IdPasager"));
		Zbor zbor = zborDAO.findById(resultSet.getInt("IdZbor"));
		Loc loc = locDAO.findById(resultSet.getInt("IdLoc"));
		Bilet bilet = new Bilet(
				resultSet.getInt("IdBilet"),
				rezervare,
				pasager,
				zbor,
				loc,
				resultSet.getDouble("Pret"),
				StatusBilet.valueOf(resultSet.getString("Status").trim().toUpperCase()));
		return bilet;
	}

	private String toDbValue(StatusBilet statusBilet) {
		return switch (statusBilet) {
			case CONFIRMAT -> "Confirmat";
			case ANULAT -> "Anulat";
			case IN_ASTEPTARE -> "In asteptare";
		};
	}
}
