package Panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import Core.ComboItem;
import Core.DBConnection;
import Core.MyModel;
import Panels.ReservationsPanel.AddReservationAction;
import Panels.ReservationsPanel.DeleteReservationAction;
import Panels.ReservationsPanel.EditReservationAction;
import Panels.ReservationsPanel.MouseAction;
import Panels.ReservationsPanel.RefreshReservationAction;
import Panels.ReservationsPanel.SearchReservationAction;

public class PaymentsPanel extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result;
	int id = -1;

	// DEFINE PAYMENTS OBJECTS

	// Define payment panels

	JPanel paymentsUpPanel = new JPanel();
	JPanel paymentsMidPanel = new JPanel();
	JPanel paymentsDounPanel = new JPanel();

	// Define payment type labels

	JLabel guestLabel = new JLabel("Гост:");

	// Define payment fields
	JTextField guestTF = new JTextField();
	
	JRadioButton isPaidButton = new JRadioButton("paid");
	JRadioButton isNotPaidButton = new JRadioButton("not paid");

	// Define payment buttons
	JButton paymentsSearchBt = new JButton("Търсене по име");
	JButton paymentsRefreshBt = new JButton("Обнови");

	JTable paymentsTable = new JTable();
	JScrollPane myScroll = new JScrollPane(paymentsTable);

	public PaymentsPanel() {

		this.setLayout(new GridLayout(3, 1));

		paymentsUpPanel.setLayout(new GridLayout(6, 2));

		paymentsUpPanel.add(guestLabel);
		paymentsUpPanel.add(guestTF);
		
		isPaidButton.setMnemonic(KeyEvent.VK_B);
		isNotPaidButton.setMnemonic(KeyEvent.VK_B);
		
		paymentsUpPanel.add(isPaidButton);
		paymentsUpPanel.add(isNotPaidButton);

		this.add(paymentsUpPanel);
		// -----------------------------------------------------------

		paymentsMidPanel.setLayout(new FlowLayout());
		paymentsMidPanel.add(paymentsSearchBt);
		paymentsMidPanel.add(paymentsRefreshBt);

		this.add(paymentsMidPanel);

		myScroll.setPreferredSize(new Dimension(550, 350));
		paymentsDounPanel.add(myScroll);

		this.add(paymentsDounPanel);

		paymentsTable.addMouseListener(new MouseAction());
		paymentsRefreshBt.addActionListener(new RefreshPaymentAction());
		isPaidButton.addActionListener(new UpdatePayment());
		isNotPaidButton.addActionListener(new UpdatePayment());
		paymentsSearchBt.addActionListener(new SearchPaymentAction());
		
		clearPaymentsForm();
		refreshTable(paymentsTable);
	}
	
	public void refreshData()
	{
		refreshTable(paymentsTable);
	}
	
	class SearchPaymentAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String sql = "select payments.id, payments.is_paid, guests.first_name, guests.last_name, reservations.total_amount from payments join reservations on payments.reservation_id = reservations.id join guests on guests.id = reservations.guest_id where guests.first_name = ?";

			try {
				String[] input = guestTF.getText().split("\\s+");
				boolean twoNames = input.length > 1;
				if (twoNames) {
					sql += " and guests.last_name = ?";
				}
				state = conn.prepareStatement(sql);
				state.setString(1, input[0]);
				if (twoNames) {
					state.setString(2, input[1]);
				}
				result = state.executeQuery();

				paymentsTable.setModel(new MyModel(result));
			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
		
	}
	
	public void clearPaymentsForm()
	{
		guestTF.setText("");
		isPaidButton.setSelected(false);
		isNotPaidButton.setSelected(false);
	}
	
	class UpdatePayment implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			conn = DBConnection.getConnection();
			String command = e.getActionCommand();
			String sql = "update payments set is_paid = ? where id = ?";
			
			try {
				state = conn.prepareStatement(sql);
				if (command.equals("paid")) {
					state.setInt(1, 1);
					isNotPaidButton.setSelected(false);
				} else if (command.equals("not paid")) {
					state.setInt(1, 0);
					isPaidButton.setSelected(false);
				}
				
				state.setInt(2, id);
				state.execute();
				refreshTable(paymentsTable);
				
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
		}
		
	}
	
	class RefreshPaymentAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			refreshTable(paymentsTable);
			clearPaymentsForm();
		}

	}
	
	public void refreshTable(JTable table) {
		conn = DBConnection.getConnection();

		try {
			// Get table data with help of joins in order to avoid using id values from
			// referenced tables
			state = conn.prepareStatement(
					"select payments.id, CASE WHEN payments.is_paid = 1 THEN 'Платено' ELSE 'Неплатено' END as is_paid, "
					+ "guests.first_name, guests.last_name, reservations.total_amount from payments "
					+ "join reservations on payments.reservation_id = reservations.id "
					+ "join guests on guests.id = reservations.guest_id");
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
	
	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = paymentsTable.getSelectedRow();
			id = Integer.parseInt(paymentsTable.getValueAt(row, 0).toString());
			if (e.getClickCount() > 1) {
				guestTF.setText(paymentsTable.getValueAt(row, 2).toString() + " " + paymentsTable.getValueAt(row, 3).toString());

				int isPaid = Integer.parseInt(paymentsTable.getValueAt(row, 1).toString());
				
				if (isPaid == 1) {
					isPaidButton.setSelected(true);
					isNotPaidButton.setSelected(false);
				} else if (isPaid == 0) {
					isNotPaidButton.setSelected(true); 
					isPaidButton.setSelected(false);
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

}
