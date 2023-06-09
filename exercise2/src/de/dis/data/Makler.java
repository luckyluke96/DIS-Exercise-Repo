package de.dis.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Makler-Bean
 * 
 * Beispiel-Tabelle:
 * CREATE TABLE makler (
 * name varchar(255),
 * address varchar(255),
 * login varchar(40) UNIQUE,
 * password varchar(40),
 * id serial primary key);
 */
public class Makler {
	private int id = -1;
	private String name;
	private String address;
	private String login;
	private String password;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Lädt einen Makler aus der Datenbank
	 * 
	 * @param id ID des zu ladenden Maklers
	 * @return Makler-Instanz
	 */
	public static Makler load(int id) {
		try {
			// Hole Verbindung
			Connection con = DbConnectionManager.getInstance().getConnection();

			// Erzeuge Anfrage
			String selectSQL = "SELECT * FROM makler WHERE id = ?";
			PreparedStatement pstmt = con.prepareStatement(selectSQL);
			pstmt.setInt(1, id);

			// Führe Anfrage aus
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Makler ts = new Makler();
				ts.setId(id);
				ts.setName(rs.getString("name"));
				ts.setAddress(rs.getString("address"));
				ts.setLogin(rs.getString("login"));
				ts.setPassword(rs.getString("password"));

				rs.close();
				pstmt.close();
				return ts;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Speichert den Makler in der Datenbank. Ist noch keine ID vergeben
	 * worden, wird die generierte Id von der DB geholt und dem Model übergeben.
	 */
	public void save() {
		// Hole Verbindung
		Connection con = DbConnectionManager.getInstance().getConnection();

		try {
			// FC<ge neues Element hinzu, wenn das Objekt noch keine ID hat.
			if (getId() == -1) {
				// Achtung, hier wird noch ein Parameter mitgegeben,
				// damit spC$ter generierte IDs zurC<ckgeliefert werden!
				String insertSQL = "INSERT INTO estate_agent(name, address, login, password) VALUES (?, ?, ?, ?)";

				PreparedStatement pstmt = con.prepareStatement(insertSQL,
						Statement.RETURN_GENERATED_KEYS);

				// Setze Anfrageparameter und fC<hre Anfrage aus
				pstmt.setString(1, getName());
				pstmt.setString(2, getAddress());
				pstmt.setString(3, getLogin());
				pstmt.setString(4, getPassword());
				pstmt.executeUpdate();

				// Hole die Id des engefC<gten Datensatzes
				ResultSet rs = pstmt.getGeneratedKeys();
				if (rs.next()) {
					setId(rs.getInt(1));
				}

				rs.close();
				pstmt.close();
			} else {
				// Falls schon eine ID vorhanden ist, mache ein Update...
				String updateSQL = "UPDATE estate_agent SET name = ?, address = ?, login = ?, password = ? WHERE id = ?";
				PreparedStatement pstmt = con.prepareStatement(updateSQL);

				// Setze Anfrage Parameter
				pstmt.setString(1, getName());
				pstmt.setString(2, getAddress());
				pstmt.setString(3, getLogin());
				pstmt.setString(4, getPassword());
				pstmt.setInt(5, getId());
				pstmt.executeUpdate();

				pstmt.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update() {
		// Hole Verbindung
		Connection con = DbConnectionManager.getInstance().getConnection();

		try {
			String updateSQL = "UPDATE estate_agent SET name = ?, address = ? WHERE login = ? AND password = ?";
			PreparedStatement pstmt = con.prepareStatement(updateSQL);

			// Setze Anfrage Parameter
			pstmt.setString(1, getName());
			pstmt.setString(2, getAddress());
			pstmt.setString(3, getLogin());
			pstmt.setString(4, getPassword());
			pstmt.executeUpdate();

			pstmt.close();
		} catch (SQLException e) {
			System.out.println("Die Login-Daten sind nicht korrekt.");
			e.printStackTrace();
		}
	}

	public void delete() {
		// Hole Verbindung
		Connection con = DbConnectionManager.getInstance().getConnection();

		try {

			String deleteSQL = "DELETE FROM estate_agent WHERE login = ? AND password = ?";

			PreparedStatement pstmt = con.prepareStatement(deleteSQL,
					Statement.RETURN_GENERATED_KEYS);

			// Setze Anfrageparameter und fC<hre Anfrage aus
			pstmt.setString(1, getLogin());
			pstmt.setString(2, getPassword());

			pstmt.executeUpdate();

			pstmt.close();

		} catch (SQLException e) {
			System.out.println("Exception");
			e.printStackTrace();
		}

	}

	public void login() {
		// Hole Verbindung
		Connection con = DbConnectionManager.getInstance().getConnection();

		try {

			String loginData = getLogin();
			String pw = getPassword();
			String loginSQL = "SELECT * FROM estate_agent WHERE login = '" + loginData +
					"' AND password = '" + pw + "'";

			// PreparedStatement pstmt = con.prepareStatement(loginSQL,
			// Statement.RETURN_GENERATED_KEYS);

			// pstmt.setString(1, getLogin());
			// pstmt.setString(2, getPassword());

			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(loginSQL);

			if (rs.next()) {
				String name = rs.getString("name");

				setId(rs.getInt(1));

				System.out.println("Hallo " + name + " mit ID " + this.id);
			} else {
				System.out.println("Passwort und Login stimmen nicht überein.");
				return;
			}

			rs.close();

			stmt.close();

		} catch (SQLException e) {
			System.out.println("Exception");
			e.printStackTrace();
		}

	}
}
