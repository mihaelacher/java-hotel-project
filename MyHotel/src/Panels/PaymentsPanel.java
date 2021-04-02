package Panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import Core.ComboItem;
import Panels.ReservationsPanel.AddReservationAction;
import Panels.ReservationsPanel.DeleteReservationAction;
import Panels.ReservationsPanel.EditReservationAction;
import Panels.ReservationsPanel.MouseAction;
import Panels.ReservationsPanel.RefreshReservationAction;
import Panels.ReservationsPanel.SearchReservationAction;

public class PaymentsPanel extends JPanel{

	
	Connection conn=null;
	PreparedStatement state=null;
	ResultSet result;
	int id=-1;
	
	
	// DEFINE PAYMENTS OBJECTS
	
			// Define payment panels
	
	JPanel paymentsUpPanel= new JPanel();
	JPanel paymentsMidPanel=new JPanel();
	JPanel paymentsDounPanel=new JPanel();
	
	// Define payment type labels
	
		JLabel guestLabel=new JLabel("Гост:");
		JLabel reservationLabel=new JLabel("Резервация:");
		
		
		// Define payment fields
		
		JTextField guestTF=new JTextField();
		JTextField reservationTF=new JTextField();
		
		
		
		JComboBox<ComboItem> guestCombo=new JComboBox<ComboItem>();
		JComboBox<ComboItem> reservationCombo=new JComboBox<ComboItem>();
		
		// Define payment buttons
				JButton paymentsAddBt=new JButton("Добавяне");
				JButton paymentsDeleteBt=new JButton("Изтриване");
				JButton paymentsEditBt=new JButton("Промяна");
				JButton paymentsSearchBt=new JButton("Търсене по име");
				JButton paymentsRefreshBt=new JButton("Обнови");
				
				JTable paymentsTable = new JTable();
				JScrollPane myScroll = new JScrollPane(paymentsTable);
				
				public PaymentsPanel() {
					
					this.setLayout(new GridLayout(3,1));
					
					
					paymentsUpPanel.setLayout(new GridLayout(6,2));
					
					paymentsUpPanel.add(guestLabel);
					paymentsUpPanel.add(guestCombo);
					paymentsUpPanel.add(reservationLabel);
					paymentsUpPanel.add(reservationCombo);
					
					
					this.add(paymentsUpPanel);
					//-----------------------------------------------------------
					
					paymentsMidPanel.setLayout(new FlowLayout());
					paymentsMidPanel.add(paymentsAddBt);
					paymentsMidPanel.add(paymentsDeleteBt);
					paymentsMidPanel.add(paymentsEditBt);
					paymentsMidPanel.add(paymentsSearchBt);
					paymentsMidPanel.add(paymentsRefreshBt);
					
					
					this.add(paymentsMidPanel);
	
					myScroll.setPreferredSize(new Dimension(350, 150));
					paymentsDounPanel.add(myScroll);
					
					this.add(paymentsDounPanel);
					
					//--------------------------------------------------------------
	
					paymentsAddBt.addActionListener(new AddPaymentAction());
					paymentsDeleteBt.addActionListener(new DeletePaymentAction());
					paymentsTable.addMouseListener(new MouseAction());
					paymentsSearchBt.addActionListener(new SearchPaymentAction());
					paymentsRefreshBt.addActionListener(new RefreshPaymentAction());
					paymentsEditBt.addActionListener(new EditPaymentAction());
	
				
				}
					
				
					
}
}
