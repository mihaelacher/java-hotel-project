package Panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import Core.ComboItem;
import Core.DBConnection;
import Core.MyModel;

public class ReservationsPanel extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result;
	int id = -1;

	// DEFINE RESERVATIONS OBJECTS

	// Define reservation panels

	JPanel reservationsUpPanel = new JPanel();
	JPanel reservationsMidPanel = new JPanel();
	JPanel reservationsDounPanel = new JPanel();

	// Define reservation type labels
	JLabel guestsLabel = new JLabel("Гости:");
	JLabel roomNumberLabel = new JLabel("Номер на стая:");
	JLabel dateOfDepartureLabel = new JLabel("Дата на настаняване:");
	JLabel dateOfAccommodationLabel = new JLabel("Дата на напускане:");

	// Define reservation fields
	JTextField guestsTF = new JTextField();
	JTextField roomNumberTF = new JTextField();
	// DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
	// JFormattedTextField dateOfDepartureTF = new JFormattedTextField(format);

	JTextField dateOfDepartureTF = new JTextField();
	// JFormattedTextField dateOfAccommodationTF = new JFormattedTextField(format);
	JTextField dateOfAccommodationTF = new JTextField();

	JComboBox<ComboItem> guestsCombo = new JComboBox<ComboItem>();
	JComboBox<ComboItem> roomNumberCombo = new JComboBox<ComboItem>();

	// Define reservation buttons
	JButton reservationsAddBt = new JButton("Добавяне");
	JButton reservationsDeleteBt = new JButton("Изтриване");
	JButton reservationsEditBt = new JButton("Промяна");
	JButton reservationsSearchBt = new JButton("Търсене по име");
	JButton reservationsRefreshBt = new JButton("Обнови");

	JTable reservationsTable = new JTable();
	JScrollPane myScroll = new JScrollPane(reservationsTable);

	public ReservationsPanel() {

		this.setLayout(new GridLayout(3, 1));

		reservationsUpPanel.setLayout(new GridLayout(6, 2));

		reservationsUpPanel.add(guestsLabel);
		reservationsUpPanel.add(guestsCombo);
		reservationsUpPanel.add(roomNumberLabel);
		reservationsUpPanel.add(roomNumberCombo);
		reservationsUpPanel.add(dateOfDepartureLabel);
		reservationsUpPanel.add(dateOfDepartureTF);
		reservationsUpPanel.add(dateOfAccommodationLabel);
		reservationsUpPanel.add(dateOfAccommodationTF);

		this.add(reservationsUpPanel);

		// -----------------------------------------------------------

		reservationsMidPanel.setLayout(new FlowLayout());
		reservationsMidPanel.add(reservationsAddBt);
		reservationsMidPanel.add(reservationsDeleteBt);
		reservationsMidPanel.add(reservationsEditBt);
		reservationsMidPanel.add(reservationsSearchBt);
		reservationsMidPanel.add(reservationsRefreshBt);

		this.add(reservationsMidPanel);

		myScroll.setPreferredSize(new Dimension(550, 350));
		reservationsDounPanel.add(myScroll);

		this.add(reservationsDounPanel);

		// --------------------------------------------------------------

		reservationsAddBt.addActionListener(new AddReservationAction());
		reservationsDeleteBt.addActionListener(new DeleteReservationAction());
		reservationsTable.addMouseListener(new MouseAction());
		reservationsSearchBt.addActionListener(new SearchReservationAction());
		reservationsRefreshBt.addActionListener(new RefreshReservationAction());
		reservationsEditBt.addActionListener(new EditReservationAction());

		reservationsTable.addMouseListener(new MouseAction());

		refreshReservationsTable(reservationsTable, "reservations");

		this.setVisible(true);

		setGuestsComboValues();
		setRoomNumberComboValues();
		clearReservationForm();
	}
	
	public void refreshData()
	{
		refreshReservationsTable(reservationsTable, "reservations");
		setGuestsComboValues();
		setRoomNumberComboValues();
	}

	public void refreshReservationsTable(JTable reservationsTable, String tabDB) {
		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("select reservations.id, CONCAT(guests.first_name, ' ', guests.last_name) as full_name,"
					+ " rooms.room_number, reservations.created_at, reservations.check_in_date, reservations.check_out_date from " + tabDB
					+ " join guests on guests.id = reservations.guest_id"
					+ " join rooms on rooms.id = reservations.room_id");
			result = state.executeQuery();
			reservationsTable.setModel(new MyModel(result));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clearReservationForm() {

		setGuestsComboValues();
		setRoomNumberComboValues();
		dateOfDepartureTF.setText("");
		dateOfAccommodationTF.setText("");

	}

	public void setRoomNumberComboValues() {
		roomNumberCombo.removeAllItems();
		roomNumberCombo.addItem(new ComboItem("0", "Изберете номер на стая"));

		conn = DBConnection.getConnection();
		String sql = "select id, room_number from rooms";

		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();

			while (result.next()) {
				roomNumberCombo.addItem(new ComboItem(result.getObject(1).toString(), result.getObject(2).toString()));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setGuestsComboValues() {
		guestsCombo.removeAllItems();
		guestsCombo.addItem(new ComboItem("0", "Изберете гост"));

		conn = DBConnection.getConnection();
		String sql = "select id, first_name, last_name from guests";

		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();

			while (result.next()) {
				guestsCombo.addItem(new ComboItem(result.getObject(1).toString(),
						result.getObject(2).toString() + " " + result.getObject(3).toString()));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static java.sql.Date toMysqlDateStr(String date) throws ParseException {
		Date Date = new SimpleDateFormat("dd.MM.yyyy").parse(date);
		String dateToString = new SimpleDateFormat("yyyy-MM-dd").format(Date);
		return java.sql.Date.valueOf(dateToString);
	}
	
	public static String toUtilDateStr(String date) throws ParseException {
		Date Date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		return new SimpleDateFormat("dd.MM.yyyy").format(Date);
	}

	class AddReservationAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "insert into reservations values(null, ?,?,?,?,?,?)";
			String generatedColumns[] = { "id" };
			long reservationId = -1;

			try {
				state = conn.prepareStatement(sql, generatedColumns);
				int roomId = Integer.parseInt(((ComboItem) roomNumberCombo.getSelectedItem()).getId());
				int guestId = Integer.parseInt(((ComboItem) guestsCombo.getSelectedItem()).getId());
				
				if (dateOfAccommodationTF.getText().isEmpty() || dateOfDepartureTF.getText().isEmpty() || guestId == 0 || roomId == 0) {
					JOptionPane.showMessageDialog(new JFrame(), "Невалидни данни!");
					return;
				}
				// First parse string date from text field to appropriate format for sql DB
				java.sql.Date dateOfAccomodation = toMysqlDateStr(dateOfAccommodationTF.getText());
				java.sql.Date dateOfDeparture = toMysqlDateStr(dateOfDepartureTF.getText());
				
				state.setInt(1, guestId);
				state.setInt(2, roomId);
				state.setDate(3, new java.sql.Date(Calendar.getInstance().getTime().getTime()));

				state.setDate(4, dateOfDeparture);
				state.setDate(5, dateOfAccomodation);

				state.setDouble(6, getReservationTotalPrice(dateOfDeparture, dateOfAccomodation, roomId));
				state.execute();

				try (ResultSet generatedKeys = state.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						reservationId = generatedKeys.getLong(1);
					} else {
						throw new SQLException("Creating user failed, no ID obtained.");
					}
				}

				insertPaymentInDB(guestId, reservationId);

				clearReservationForm();
				refreshReservationsTable(reservationsTable, "reservations");
				setRoomNumberComboValues();
				setGuestsComboValues();

			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void insertPaymentInDB(int guestId, long reservationId) throws SQLException {
		conn = DBConnection.getConnection();
		String sql = "insert into payments values(null, ?, ?, 0)";

		PreparedStatement paymentState = conn.prepareStatement(sql);
		paymentState.setInt(1, guestId);
		paymentState.setLong(2, reservationId);

		paymentState.execute();
	}

	public double getReservationTotalPrice(java.sql.Date dateOfDeparture, java.sql.Date dateOfAccomodation, int roomId)
			throws SQLException {
		// Find out for how many days is the reservation to calculate total price
		int diffInDays = (int) ((dateOfAccomodation.getTime() - dateOfDeparture.getTime()) / (1000 * 60 * 60 * 24));

		// Use DB to retrieve day price for the selected room
		PreparedStatement roomsState = conn.prepareStatement(
				"select price_per_day from room_types join rooms on room_types.id = rooms.room_type_id where rooms.id = ?");
		roomsState.setInt(1, roomId);
		ResultSet roomsResult = roomsState.executeQuery();
		String roomsPricePerDay = null;
		while (roomsResult.next()) {
			roomsPricePerDay = roomsResult.getString(1);
		}

		// Calculate total price
		return Double.parseDouble(roomsPricePerDay) * diffInDays;
	}

	class DeleteReservationAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			conn = DBConnection.getConnection();
			String sql = "delete from reservations where id=?";

			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();
				refreshReservationsTable(reservationsTable, "reservations");
				clearReservationForm();
				id = -1;
				setRoomNumberComboValues();
				setGuestsComboValues();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = reservationsTable.getSelectedRow();
			id = Integer.parseInt(reservationsTable.getValueAt(row, 0).toString());
			if (e.getClickCount() > 1) {
				
				try {
					dateOfDepartureTF.setText(toUtilDateStr(reservationsTable.getValueAt(row, 4).toString()));
					dateOfAccommodationTF.setText(toUtilDateStr(reservationsTable.getValueAt(row, 5).toString()));
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				String guestFullName = reservationsTable.getValueAt(row, 1).toString();
				String roomNumber = reservationsTable.getValueAt(row, 2).toString();

				int guestsCount = guestsCombo.getItemCount();

				for (int i = 0; i < guestsCount; i++) {
					ComboItem current = guestsCombo.getItemAt(i);
					if (current.getLabel().equals(guestFullName)) {
						guestsCombo.setSelectedIndex(i);
						break;
					}
				}

				int roomNumberCount = roomNumberCombo.getItemCount();

				for (int i = 0; i < roomNumberCount; i++) {
					ComboItem current = roomNumberCombo.getItemAt(i);
					if (current.getLabel().equals(roomNumber)) {
						roomNumberCombo.setSelectedIndex(i);
						break;
					}
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class SearchReservationAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnection.getConnection();
			String sql = "select reservations.id, CONCAT(guests.first_name, ' ', guests.last_name) as full_name,"
					+ " rooms.room_number, reservations.created_at, reservations.check_in_date, reservations.check_out_date from reservations"
					+ "	join guests on guests.id = reservations.guest_id"
					+ " join rooms on rooms.id = reservations.room_id"
					+ " where guests.id = ?";

			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, Integer.parseInt(((ComboItem) guestsCombo.getSelectedItem()).getId()));
				result = state.executeQuery();
				reservationsTable.setModel(new MyModel(result));

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	class EditReservationAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "update reservations set guest_id = ?, room_id = ?, check_in_date = ?, check_out_date = ?, total_amount = ?  where id = ?";

			try {
				int roomId = Integer.parseInt(((ComboItem) roomNumberCombo.getSelectedItem()).getId());
				int guestId = Integer.parseInt(((ComboItem) guestsCombo.getSelectedItem()).getId());
				if (dateOfAccommodationTF.getText().isEmpty() || dateOfDepartureTF.getText().isEmpty() || guestId == 0 || roomId == 0) {
					JOptionPane.showMessageDialog(new JFrame(), "Невалидни данни!");
					return;
				}
				state = conn.prepareStatement(sql);
				state.setInt(1, guestId);
				state.setInt(2, roomId);

				// First parse string date from text field to appropriate format for sql DB
				java.sql.Date dateOfAccomodation = toMysqlDateStr(dateOfAccommodationTF.getText());
				java.sql.Date dateOfDeparture = toMysqlDateStr(dateOfDepartureTF.getText());

				state.setDate(3, dateOfDeparture);
				state.setDate(4, dateOfAccomodation);

				state.setDouble(5, getReservationTotalPrice(dateOfDeparture, dateOfAccomodation, roomId));
				state.setInt(6, id);
				state.execute();

				clearReservationForm();
				refreshReservationsTable(reservationsTable, "reservations");
				setRoomNumberComboValues();
				setRoomNumberComboValues();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
	}

	class RefreshReservationAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			refreshReservationsTable(reservationsTable, "reservations");

		}

	}

}
