package Panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import Core.ComboItem;
import Core.DBConnection;
import Core.MyModel;



public class ReservationsPanel extends JPanel{
	
	
	Connection conn=null;
	PreparedStatement state=null;
	ResultSet result;
	int id=-1;
	
	// DEFINE RESERVATIONS OBJECTS
	
		// Define reservation panels
	
	JPanel reservationsUpPanel= new JPanel();
	JPanel reservationsMidPanel=new JPanel();
	JPanel reservationsDounPanel=new JPanel();
	
	// Define reservation type labels
	JLabel guestsLabel=new JLabel("Гости:");
	JLabel roomNumberLabel=new JLabel("Номер на стая:");
	JLabel dateOfDepartureLabel=new JLabel("Дата на настаняване:");
	JLabel dateOfAccommodationLabel=new JLabel("Дата на напускане:");
	
	// Define reservation fields
	JTextField guestsTF=new JTextField();
	JTextField roomNumberTF=new JTextField();
	JTextField dateOfDepartureTF=new JTextField();
	JTextField dateOfAccommodationTF=new JTextField();
	
	JComboBox<ComboItem> guestsCombo=new JComboBox<ComboItem>();
	JComboBox<ComboItem> roomNumberCombo=new JComboBox<ComboItem>();
	
	
	
	// Define reservation buttons
		JButton reservationsAddBt=new JButton("Добавяне");
		JButton reservationsDeleteBt=new JButton("Изтриване");
		JButton reservationsEditBt=new JButton("Промяна");
		JButton reservationsSearchBt=new JButton("Търсене по име");
		JButton reservationsRefreshBt=new JButton("Обнови");
		
		JTable reservationTable = new JTable();
		JScrollPane myScroll = new JScrollPane(reservationTable);
	
		public ReservationsPanel() {
			
			this.setLayout(new GridLayout(3,1));
			
			
			reservationsUpPanel.setLayout(new GridLayout(6,2));
			
			reservationsUpPanel.add(guestsLabel);
			reservationsUpPanel.add(guestsTF);
			reservationsUpPanel.add(roomNumberLabel);
			reservationsUpPanel.add(roomNumberTF);
			reservationsUpPanel.add(dateOfDepartureLabel);
			reservationsUpPanel.add(dateOfDepartureTF);
			reservationsUpPanel.add(dateOfAccommodationLabel);
			reservationsUpPanel.add(dateOfAccommodationTF);
			
			this.add(reservationsUpPanel);
			
			//-----------------------------------------------------------
			
			reservationsMidPanel.setLayout(new FlowLayout());
			reservationsMidPanel.add(reservationsAddBt);
			reservationsMidPanel.add(reservationsDeleteBt);
			reservationsMidPanel.add(reservationsEditBt);
			reservationsMidPanel.add(reservationsSearchBt);
			reservationsMidPanel.add(reservationsRefreshBt);
			
			
			this.add(reservationsMidPanel);
			
			
			myScroll.setPreferredSize(new Dimension(350, 150));
			reservationsDounPanel.add(myScroll);
			
			this.add(reservationsDounPanel);
			
			//--------------------------------------------------------------
			
			
			reservationsAddBt.addActionListener(new AddReservationAction());
			reservationsDeleteBt.addActionListener(new DeleteReservationAction());
			reservationTable.addMouseListener(new MouseAction());
			
			//reservationsSearchBt.addActionListener(new SearchReservationAction());
			//reservationsRefreshBt.addActionListener(new RefreshReservationAction());
			
	
		    refreshReservationTable(reservationTable, "reservation");
			
			this.setVisible(true);
			
			setGuestsComboValues();
		}
		
		public void refreshReservationTable(JTable reservationTable, String tabDB) {
			conn=DBConnection.getConnection();
			
			try {
				state=conn.prepareStatement("select * from" + tabDB);
				result=state.executeQuery();
				reservationTable.setModel(new MyModel(result));
				
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
			
			
		}

		public void setGuestsComboValues() {
			guestsCombo.removeAllItems();
			conn = DBConnection.getConnection();
			String sql = "select id, first_name, last_name from guests";
			
			try {
				state = conn.prepareStatement(sql);
				result = state.executeQuery();
				
				String Item="";
				
				while (result.next()) {
					Item=result.getObject(2).toString()+" "+result.getObject(3).toString();
					guestsCombo.addItem(new ComboItem(result.getObject(1).toString(), Item));
				}
			} catch (SQLException e) {
				 // TODO Auto-generated catch block 
				e.printStackTrace();
			}
		}
		
		class AddReservationAction implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				conn = DBConnection.getConnection();
				String sql="insert into reservations values(null, ?,?,?,?,?)";
				
				
				try {
				state=conn.prepareStatement(sql);
				state.setInt(1, Integer.parseInt(((ComboItem) guestsCombo.getSelectedItem()).getValue()));
				state.setInt(2, Integer.parseInt(((ComboItem) roomNumberCombo.getSelectedItem()).getValue()));
				state.setDate(3, (Date) new SimpleDateFormat("dd.MM.yyyy").parse(dateOfDepartureTF.getText()));
				state.setDate(4, (Date) new SimpleDateFormat("dd.MM.yyyy").parse(dateOfAccommodationTF.getText()));
				
				state.execute();
				clearReservationForm();
				refreshReservationTable(reservationTable, "reservation");
               
				
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				class DeleteReservationAction implements ActionListener{

					@Override
					public void actionPerformed(ActionEvent arg0) {
						conn = DBConnection.getConnection();
						String sql="delete from reservation where id=?";
						
						setGuestsComboValues();
						setRoomNumberComboValues();
					    dateOfDepartureTF.setText("");
					    dateOfAccommodationTF.setText("");
						
					}
					
				}
				
				class MouseAction implements MouseListener{

					@Override
					public void mouseClicked(MouseEvent e) {
						int row=reservationTable.getSelectedRow();
						id=Integer.parseInt(reservationTable.getValueAt(row, 0).toString());
						if(e.getClickCount()>1) {
							dateOfDepartureTF.setText(reservationTable.getValueAt(row, 3).toString());
							dateOfAccommodationTF.setText(reservationTable.getValueAt(row, 4).toString());
							
							String guests = reservationTable.getValueAt(row, 1).toString();
							String roomNumber = reservationTable.getValueAt(row, 2).toString();
							
							int guestsCount = guestsCombo.getItemCount();
							
							for(int i = 0; i < guestsCount; i++) {
								ComboItem current = guestsCombo.getItemAt(i);
								if (current.getLabel().equals(guests)) {
									guestsCombo.setSelectedIndex(i);
									break;
								}
							}
							
							int roomNumberCount = roomNumberCombo.getItemCount();
							
							for(int i = 0; i < roomNumberCount; i++) {
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
			
			
		}

			
			
		
		
		
		}
		
		
			
			
	
}
	


