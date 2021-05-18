package Panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

public class RoomsPanel extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result;
	int id = -1;

	// DEFINE ROOM OBJECTS

	// Define room panels
	JPanel roomsUpPanel = new JPanel();
	JPanel roomsMidPanel = new JPanel();
	JPanel roomsDownPanel = new JPanel();

	// Define room type labels
	JLabel roomNumberLabel = new JLabel("Номер на стаята");
	JLabel roomTypeLabel = new JLabel("Тип на стаята");
	JLabel roomStatusLabel = new JLabel("Статус на стаята");

	// Define room fields
	JTextField roomNumberTF = new JTextField();
	JComboBox<ComboItem> roomStatusCombo = new JComboBox<ComboItem>();
	JComboBox<ComboItem> roomTypeCombo = new JComboBox<ComboItem>();

	// Define room buttons
	JButton roomsAddBt = new JButton("Добавяне");
	JButton roomsDeleteBt = new JButton("Изтриване");
	JButton roomsEditBt = new JButton("Промяна");
	JButton roomsSearchBt = new JButton("Търсене по име");
	JButton roomsRefreshBt = new JButton("Обнови");

	// Define room table
	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);

	public RoomsPanel() {
		// First set panel layout
		this.setLayout(new GridLayout(3, 1));

		// Then set layout for the upper section
		roomsUpPanel.setLayout(new GridLayout(6, 2));

		// Add input fields and dropdown menus
		roomsUpPanel.add(roomNumberLabel);
		roomsUpPanel.add(roomNumberTF);
		roomsUpPanel.add(roomTypeLabel);
		roomsUpPanel.add(roomTypeCombo);
		roomsUpPanel.add(roomStatusLabel);
		roomsUpPanel.add(roomStatusCombo);

		this.add(roomsUpPanel);

		// Set layout for middle section
		roomsMidPanel.setLayout(new FlowLayout());
		// Add action buttons
		roomsMidPanel.add(roomsAddBt);
		roomsMidPanel.add(roomsDeleteBt);
		roomsMidPanel.add(roomsEditBt);
		roomsMidPanel.add(roomsSearchBt);
		roomsMidPanel.add(roomsRefreshBt);

		this.add(roomsMidPanel);

		// Define table
		myScroll.setPreferredSize(new Dimension(550, 350));
		roomsDownPanel.add(myScroll);
		this.add(roomsDownPanel);

		// Attach event listeners to each button
		roomsAddBt.addActionListener(new AddRoomAction());
		roomsDeleteBt.addActionListener(new DeleteRoomAction());
		roomsSearchBt.addActionListener(new SearchRoomAction());
		roomsRefreshBt.addActionListener(new RefreshRoomAction());
		roomsEditBt.addActionListener(new EditRoomAction());

		// Attach event listener for the table
		table.addMouseListener(new MouseAction());

		// Refresh table data
		refreshTable(table, "rooms");
		// Clear inputs
		clearRoomsForm();
	}
	

	public void refreshData()
	{
		refreshTable(table, "rooms");
		setRoomTypeComboValues();
		setRoomStatusComboValues();
	}

	class EditRoomAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// Create sql update room statement for execution
			String sql = "update rooms set room_number = ?, room_status_id = ?, room_type_id = ? where id = ?";

			try {
				conn = DBConnection.getConnection();
				state = conn.prepareStatement(sql);
				// Set important query data
				state.setInt(1, Integer.parseInt(roomNumberTF.getText()));
				state.setInt(2, Integer.parseInt(((ComboItem) roomStatusCombo.getSelectedItem()).getId()));
				state.setInt(3, Integer.parseInt(((ComboItem) roomTypeCombo.getSelectedItem()).getId()));
				state.setInt(4, id);

				state.execute();
				// Refresh all input fields
				clearRoomsForm();
				// Refresh table data with updated item
				refreshTable(table, "rooms");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	public void refreshTable(JTable table, String tabDB) {
		conn = DBConnection.getConnection();

		try {
			// Get table data with help of joins in order to avoid using id values from
			// referenced tables
			state = conn.prepareStatement(
					"select rooms.id, rooms.room_number, room_types.name, room_status.status from rooms join room_types on room_types.id = rooms.room_type_id join room_status on room_status.id = rooms.room_status_id;");
			result = state.executeQuery();
			table.setModel(new MyModel(result));

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clearRoomsForm() {
		roomNumberTF.setText("");
		setRoomTypeComboValues();
		setRoomStatusComboValues();
	}

	public void setRoomStatusComboValues() {
		// Remove items in order to load new data in case in the meantime something has
		// changed in DB
		roomStatusCombo.removeAllItems();
		// Add default value
		roomStatusCombo.addItem(new ComboItem("0", "Изберете статус"));

		conn = DBConnection.getConnection();
		String sql = "select id, status from room_status";

		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();

			while (result.next()) {
				roomStatusCombo.addItem(new ComboItem(result.getObject(1).toString(), result.getObject(2).toString()));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setRoomTypeComboValues() {
		roomTypeCombo.removeAllItems();
		// Add default value
		roomTypeCombo.addItem(new ComboItem("0", "Изберете тип стая"));

		conn = DBConnection.getConnection();
		String sql = "select id, name from room_types";

		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();

			while (result.next()) {
				roomTypeCombo.addItem(new ComboItem(result.getObject(1).toString(), result.getObject(2).toString()));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class AddRoomAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// Prepare query for insert
			String sql = "insert into rooms values(null, ?, ?, ?)";

			try {
				state = conn.prepareStatement(sql);

				state.setInt(1, Integer.parseInt(roomNumberTF.getText()));
				state.setInt(2, Integer.parseInt(((ComboItem) roomTypeCombo.getSelectedItem()).getId()));
				state.setInt(3, Integer.parseInt(((ComboItem) roomStatusCombo.getSelectedItem()).getId()));

				state.execute();
				// Remove input data
				clearRoomsForm();
				// Refresh table with new data
				refreshTable(table, "rooms");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	class DeleteRoomAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			conn = DBConnection.getConnection();
			String sql = "delete from rooms where id=?";

			try {
				
				String check = "select * from reservations join rooms on rooms.id = reservations.room_id where rooms.id = ?";
				PreparedStatement checkState = conn.prepareStatement(check);
				checkState.setInt(1, id);
				ResultSet checkResult = checkState.executeQuery();
				
				if (checkResult.first()) {
					JOptionPane.showMessageDialog(new JFrame(), "Невъзможно изтриване! Има резервации за тази стая!");
					return;
				}
				
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();

				clearRoomsForm();

				refreshTable(table, "rooms");
				id = -1;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class SearchRoomAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			conn = DBConnection.getConnection();
			String sql = "select * from rooms where room_number=?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, roomNumberTF.getText());
				result = state.executeQuery();

				table.setModel(new MyModel(result));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = table.getSelectedRow();
			id = Integer.parseInt(table.getValueAt(row, 0).toString());
			if (e.getClickCount() > 1) {
				roomNumberTF.setText(table.getValueAt(row, 1).toString());

				String roomType = table.getValueAt(row, 2).toString();
				String roomStatus = table.getValueAt(row, 3).toString();

				int roomTypesCount = roomTypeCombo.getItemCount();

				// Loop through all combo items, when match is found set combo value and break
				// from the loop

				for (int i = 0; i < roomTypesCount; i++) {
					ComboItem current = roomTypeCombo.getItemAt(i);
					if (current.getLabel().equals(roomType)) {
						roomTypeCombo.setSelectedIndex(i);
						break;
					}
				}

				int roomStatusesCount = roomStatusCombo.getItemCount();

				for (int i = 0; i < roomStatusesCount; i++) {
					ComboItem current = roomStatusCombo.getItemAt(i);
					if (current.getLabel().equals(roomStatus)) {
						roomStatusCombo.setSelectedIndex(i);
						break;
					}
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class RefreshRoomAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			refreshTable(table, "rooms");
		}

	}
}
