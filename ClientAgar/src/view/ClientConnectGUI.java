package view;



import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import control.ClientManager;


@SuppressWarnings("serial")
public class ClientConnectGUI extends JFrame implements ActionListener{

	private JPanel panel;
	private JLabel status;
	private JButton startGame;
	private ClientManager manager;
	private JTextField username;
	
	public ClientConnectGUI(ClientManager manager) {
		this.manager  = manager;
		setSize(300, 200);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, 
				    dim.height/2-this.getSize().height/2);
		
		this.panel = new JPanel();
		this.status = new JLabel("Username", JLabel.CENTER);
		
		this.username = new JTextField(15);
		this.startGame = new JButton("Connect to Server");
		this.startGame.addActionListener(this);
		
		panel.setLayout( new FlowLayout());
		
		panel.add(this.status);
		panel.add(this.username);
		panel.add(startGame);
		
		this.getContentPane().add( panel );
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if( evt.getSource() == startGame) {
			this.startGame.setEnabled(false);
			this.dispose();
			this.manager.connectToServer(this.getUserName());
		}
	}

	public String getUserName() {
		return this.username.getText();
	}

}