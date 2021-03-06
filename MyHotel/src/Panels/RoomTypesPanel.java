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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import Core.DBConnection;
import Core.MyModel;

public class RoomTypesPanel extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result;
	int id = -1;

	// DEFINE ROOM TYPES OBJECTS

	// Define room type panels
	JPanel roomTypesUpPanel = new JPanel();
	JPanel roomTypesMidPanel = new JPanel();
	JPanel roomTypesDownPanel = new JPanel();

	// Define room type labels
	JLabel nameLabel = new JLabel("Наименование");
	JLabel priceLabel = new JLabel("Цена за нощувка");

	// Define room type fields
	JTextField nameTF = new JTextField();
	JTextField priceTF = new JTextField();

	// Define room type buttons
	JButton roomTypesAddBt = new JButton("Добавяне");
	JButton roomTypesDeleteBt = new JButton("Изтриване");
	JButton roomTypesEditBt = new JButton("Промяна");
	JButton roomTypesSearchBt = new JButton("Търсене по име");
	JButton roomTypesRefreshBt = new JButton("Обнови");

	// Define room types table
	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);

	public RoomTypesPanel() {
		// First set layout for the main panel
		this.setLayout(new GridLayout(3, 1));

		// Set layout for input fields
		roomTypesUpPanel.setLayout(new GridLayout(6, 2));
		// Add all input fields
		roomTypesUpPanel.add(nameLabel);
		roomTypesUpPanel.add(nameTF);
		roomTypesUpPanel.add(priceLabel);
		roomTypesUpPanel.add(priceTF);

		this.add(roomTypesUpPanel);

		// Set layout for action buttons in the middle section
		roomTypesMidPanel.setLayout(new FlowLayout());
		// Add all buttons
		roomTypesMidPanel.add(roomTypesAddBt);
		roomTypesMidPanel.add(roomTypesDeleteBt);
		roomTypesMidPanel.add(roomTypesEditBt);
		roomTypesMidPanel.add(roomTypesSearchBt);
		roomTypesMidPanel.add(roomTypesRefreshBt);

		this.add(roomTypesMidPanel);

		// Create table in order to display DB data
		myScroll.setPreferredSize(new Dimension(550, 350));
		roomTypesDownPanel.add(myScroll);
		this.add(roomTypesDownPanel);
		// Attach event listeners
		roomTypesAddBt.addActionListener(new AddRoomTypeAction());
		roomTypesDeleteBt.addActionListener(new DeleteRoomTypeAction());
		roomTypesSearchBt.addActionListener(new SearchRoomTypeAction());
		roomTypesRefreshBt.addActionListener(new RefreshRoomTypeAction());
		roomTypesEditBt.addActionListener(new EditRoomTypeAction());

		table.addMouseListener(new MouseAction());

		// Refresh table data from DB
		refreshTable(table, "room_types");
		// Clear input fields
		clearRoomTypesForm();
	}
	
	public void refreshData()
	{
		refreshTable(table, "room_types");
	}

	class EditRoomTypeAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "update room_types set name = ?, price_per_day = ? where id = ?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, nameTF.getText());
				state.setDouble(2, Double.parseDouble(priceTF.getText()));
				state.setInt(3, id);

				state.execute();
				// Clear input fields
				clearRoomTypesForm();
				// Refresh from DB with new data
				refreshTable(table, "room_types");

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	public void refreshTable(JTable table, String tabDB) {
		conn = DBConnection.getConnection();

		try {
			state = conn.prepareStatement("select * from " + tabDB);
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

	public void clearRoomTypesForm() {
		nameTF.setText("");
		priceTF.setText("");
	}

	class AddRoomTypeAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "insert into room_types values(null, ?,?)";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, nameTF.getText());
				state.setDouble(2, Double.parseDouble(priceTF.getText()));

				state.execute();
				// Clear input fields
				clearRoomTypesForm();
				// Refresh from DB with new data
				refreshTable(table, "room_types");

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	class DeleteRoomTypeAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			conn = DBConnection.getConnection();
			String sql = "delete from room_types where id=?";

			try {
				String check = "select * from room_types join rooms on rooms.room_type_id = room_types.id where room_types.id = ?";
				PreparedStatement checkState = conn.prepareStatement(check);
				checkState.setInt(1, id);
				ResultSet checkResult = checkState.executeQuery();
				
				if (checkResult.first()) {
					JOptionPane.showMessageDialog(new JFrame(), "Невъзможно изтриване! Има данни за този тип стая!");
					return;
				}
				
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();

				clearRoomTypesForm();

				refreshTable(table, "room_types");
				id = -1;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class SearchRoomTypeAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			conn = DBConnection.getConnection();
			String sql = "select * from room_types where name=?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, nameTF.getText());
				result = state.executeQuery();

				table.setModel(new MyModel(result));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class RefreshRoomTypeAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			refreshTable(table, "room_types");
		}

	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = table.getSelectedRow();
			id = Integer.parseInt(table.getValueAt(row, 0).toString());
			if (e.getClickCount() > 1) {
				nameTF.setText(table.getValueAt(row, 1).toString());
				priceTF.setText(table.getValueAt(row, 2).toString());
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

}
