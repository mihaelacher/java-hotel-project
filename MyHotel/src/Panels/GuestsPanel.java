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

import Core.DBConnection;
import Core.MyModel;

public class GuestsPanel extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result;
	int id = -1;
	String tableName = "guests";

	JPanel upPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel dounPanel = new JPanel();
	// ---------------------------------------------------------
	JLabel fLabel = new JLabel("Име:");
	JLabel lLabel = new JLabel("Фамилия:");
	JLabel mailLabel = new JLabel("Мейл адрес:");
	JLabel phoneLabel = new JLabel("Телефонен номер:");

	JTextField fTF = new JTextField();
	JTextField lTF = new JTextField();
	JTextField mailTF = new JTextField();
	JTextField phoneTF = new JTextField();

	// -------------------------------------------------------------

	JButton addBt = new JButton("Добавяне");
	JButton deleteBt = new JButton("Изтриване");
	JButton editBt = new JButton("Промяна");
	JButton searchBt = new JButton("Търсене по име");
	JButton refreshBt = new JButton("Обнови");
	// --------------------------------------------------------------

	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);

	public GuestsPanel() {

		this.setLayout(new GridLayout(3, 1));

		upPanel.setLayout(new GridLayout(6, 2));
		upPanel.add(fLabel);
		upPanel.add(fTF);
		upPanel.add(lLabel);
		upPanel.add(lTF);
		upPanel.add(mailLabel);
		upPanel.add(mailTF);
		upPanel.add(phoneLabel);
		upPanel.add(phoneTF);

		this.add(upPanel);
		// -----------------------------------------------------------

		midPanel.setLayout(new FlowLayout());
		midPanel.add(addBt);
		midPanel.add(deleteBt);
		midPanel.add(editBt);
		midPanel.add(searchBt);
		midPanel.add(refreshBt);

		this.add(midPanel);
		// ------------------------------------------------------------
		myScroll.setPreferredSize(new Dimension(550, 350));
		dounPanel.add(myScroll);

		this.add(dounPanel);

		// --------------------------------------------------------------

		addBt.addActionListener(new AddAction());
		deleteBt.addActionListener(new DeleteAction());
		searchBt.addActionListener(new SearchAction());
		refreshBt.addActionListener(new RefreshAction());
		editBt.addActionListener(new EditAction());

		table.addMouseListener(new MouseAction());

		refreshTable(table, tableName);

		this.setVisible(true);
	}
	
	public void refreshData()
	{
		refreshTable(table, tableName);
	}

	class EditAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "update guests set first_name = ?, last_name = ?, email = ?, phone = ? where id = ?";
			try {
				String firstName = fTF.getText();
				String lastName = fTF.getText();
				if (firstName.isEmpty() || lastName.isEmpty()) {
					JOptionPane.showMessageDialog(new JFrame(), "Невалидни данни!");
					return;
				}
				state = conn.prepareStatement(sql);
				state.setString(1, firstName);
				state.setString(2, lastName);
				state.setString(3, mailTF.getText());
				state.setString(4, phoneTF.getText());
				state.setInt(5, id);

				state.execute();
				clearForm();
				refreshTable(table, tableName);

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

	public void clearForm() {
		fTF.setText("");
		lTF.setText("");
		mailTF.setText("");
		phoneTF.setText("");
	}

	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "insert into " + tableName + " values(null, ?,?,?,?)";
			try {
				String firstName = fTF.getText();
				String lastName = fTF.getText();
				if (firstName.isEmpty() || lastName.isEmpty()) {
					JOptionPane.showMessageDialog(new JFrame(), "Невалидни данни!");
					return;
				}
				state = conn.prepareStatement(sql);
				state.setString(1, firstName);
				state.setString(2, lastName);
				state.setString(3, mailTF.getText());
				state.setString(4, phoneTF.getText());

				state.execute();
				clearForm();
				refreshTable(table, tableName);

			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			conn = DBConnection.getConnection();
			String sql = "delete from " + tableName + " where id=?";

			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();
				refreshTable(table, tableName);
				clearForm();
				id = -1;
			} catch (SQLException e) {
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
				fTF.setText(table.getValueAt(row, 1).toString());
				lTF.setText(table.getValueAt(row, 2).toString());
				mailTF.setText(table.getValueAt(row, 3).toString());
				phoneTF.setText(table.getValueAt(row, 4).toString());
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

	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			conn = DBConnection.getConnection();
			String sql = "select * from " + tableName + " where first_name=?";

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, fTF.getText());
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

	}

	class RefreshAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			refreshTable(table, "guests");
		}

	}
}
