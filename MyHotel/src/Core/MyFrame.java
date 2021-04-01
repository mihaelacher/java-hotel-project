package Core;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Panels.GuestsPanel;
import Panels.PaymentsPanel;
import Panels.ReservationsPanel;
import Panels.RoomTypesPanel;
import Panels.RoomsPanel;

public class MyFrame extends JFrame{
	
	GuestsPanel guestsPanel=new GuestsPanel();
	RoomTypesPanel roomTypesPanel = new RoomTypesPanel();
	RoomsPanel roomsPanel=new RoomsPanel();
	ReservationsPanel reservationsPanel=new ReservationsPanel();
	PaymentsPanel paymentPanel=new PaymentsPanel();
	
	JTabbedPane tab=new JTabbedPane();
	
	public MyFrame(){
		this.setSize(400, 600);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		tab.add("Гости",guestsPanel);
		tab.add("Типове стаи", roomTypesPanel);
		tab.add("Стаи",roomsPanel);
		tab.add("Резервации",reservationsPanel);
		tab.add("Плащания",paymentPanel);
		
		tab.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				switch (tab.getSelectedIndex()) {
				case 0:
					guestsPanel.setVisible(true);
					break;
				case 1:
					roomTypesPanel.setVisible(true);
					break;
				case 2:
					roomsPanel.setVisible(true);
					break;
				case 3:
					reservationsPanel.setVisible(true);
					break;
				case 4:
					paymentPanel.setVisible(true);
					break;
				}
				
			}
			
		});
		
		this.add(tab);
		this.setVisible(true);
	}
}