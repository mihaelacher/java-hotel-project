package Panels;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

public class ReferencePanel extends JPanel {

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result;
	int id = -1;
	
	JPanel referencesUpPanel = new JPanel();
	JPanel referencesMidPanel = new JPanel();
	JPanel referencesDownPanel = new JPanel();
	
	JLabel checkInDateLabel = new JLabel("Дата на настаняване:");
	JTextField checkInDateTF = new JTextField();
	
	JLabel roomNumberLabel = new JLabel("Номер на стая:");
	JTextField roomNumberTF = new JTextField();
	
	JButton searchBt = new JButton("Резултати");
	
	JTable table = new JTable();
	JScrollPane myScroll = new JScrollPane(table);
	
	public ReferencePanel() {
		this.setLayout(new GridLayout(3, 1));
		referencesUpPanel.setLayout(new GridLayout(6, 2));
		referencesUpPanel.add(checkInDateLabel);
		referencesUpPanel.add(checkInDateTF);
		referencesUpPanel.add(roomNumberLabel);
		referencesUpPanel.add(roomNumberTF);
		this.add(referencesUpPanel);
		
		referencesMidPanel.add(searchBt);
		this.add(referencesMidPanel);
		
		myScroll.setPreferredSize(new Dimension(550, 350));
		referencesDownPanel.add(myScroll);
		this.add(referencesDownPanel);
		
		searchBt.addActionListener(new SearchAction());
	}
	
	class SearchAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn = DBConnection.getConnection();
			try {
				state = conn.prepareStatement("select CONCAT(guests.first_name, ' ', guests.last_name) as full_name, rooms.room_number, reservations.check_in_date, "
						+ "reservations.check_out_date, reservations.total_amount, "
						+ "CASE WHEN payments.is_paid = 1 THEN 'Платено' ELSE 'Неплатено' END as is_paid "
						+ "from reservations "
						+ "join guests on reservations.guest_id = guests.id "
						+ "join rooms on rooms.id = reservations.room_id "
						+ "join payments on payments.reservation_id = reservations.id "
						+ "where reservations.check_in_date = ? "
						+ "and rooms.room_number = ?");
				
				if(checkInDateTF.getText().isEmpty() || roomNumberTF.getText().isEmpty()) {
					JOptionPane.showMessageDialog(new JFrame(), "Невалидни данни!");
					return;
				}
				
				state.setDate(1, toMysqlDateStr(checkInDateTF.getText()));
				state.setInt(2, Integer.parseInt(roomNumberTF.getText()));
				
				result = state.executeQuery();
				table.setModel(new MyModel(result));
			} catch (Exception e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}
		}
	}
	
	public static java.sql.Date toMysqlDateStr(String date) throws ParseException {
		Date Date = new SimpleDateFormat("dd.MM.yyyy").parse(date);
		String dateToString = new SimpleDateFormat("yyyy-MM-dd").format(Date);
		return java.sql.Date.valueOf(dateToString);
	}
}
