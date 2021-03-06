package handelers;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.IOException;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;



import server.Server;
import controler.MainControler;
import gui.MainGui;

public class MainGuiHandeler {
	MainGui gui;
	MainControler con;
	public MainGuiHandeler(MainControler con, MainGui gui){
		this.gui=gui;
		this.con=con;
		standaardHandelers();
	}
	
	public void standaardHandelers(){
		gui.getFrame().addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				if (con.getServer()!=null){
					try {
						// alle clients sluiten
						con.getServer().closeClients();
					} catch (IOException e1) {}
				}else if(con.getClient()!=null){
					try {
						// aan de server melden dat client gaat sluiten
						con.getClient().exitClient();
					} catch (IOException e1) {}
				}
			}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
	}
	
	public void addMirrorHandelers() {
		gui.getFrame().addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent arg0) {}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				if (con.getServer()!=null && con.getMode()==MainControler.modeMiror){
					con.getServer().sendSize(gui.getFrame().getHeight(), gui.getFrame().getWidth());
				}				
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
				if (con.getServer()!=null && con.getMode()==MainControler.modeMiror){
					con.getServer().sendCordinates(((Double) gui.getFrame().getLocation().getX()).intValue() , ((Double) gui.getFrame().getLocation().getY()).intValue());
				}				
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {}
		});
		
		gui.getFrame().addWindowStateListener(new WindowStateListener() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				if (con.getServer()!=null&& con.getMode()==MainControler.modeMiror){
					con.getServer().sendState(e.getNewState());
				}
				
			}
		});
		
		gui.getTxtMirrorField().getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				changeTekst();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				changeTekst();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				changeTekst();				
			}
			
			private void changeTekst(){
				if (con.getServer()!=null && con.getMode()==MainControler.modeMiror){
					con.getServer().sendMirrorField(gui.getTxtMirrorField().getText().toString());
				}
			}
		});
	}
	
	public void addExtendHandelers() {
		class MyDispatcher implements KeyEventDispatcher {
	        @Override
			public boolean dispatchKeyEvent(KeyEvent e) {
	        	int keyCode = e.getKeyCode();
				switch (keyCode) {
				case KeyEvent.VK_ALT:
					System.out.println("alt");
					con.setKeyCommand(1);	
					break;
				case KeyEvent.VK_UP:
					if (con.getKeyCommand()==1){
						System.out.println("alt+omhoog");
						con.setKeyCommand(0);
						con.getServer();
						if (!Server.clients.isEmpty()){
							con.getServer().sendScreenToClient(gui);
							gui.getFrame().setVisible(false);
						}
					}
					break;
				case KeyEvent.VK_DOWN:
					if (con.getKeyCommand()==1){
						System.out.println("alt+down");
						con.setKeyCommand(0);
						con.getServer();
						if (!Server.clients.isEmpty()){
							con.getServer().sendScreenToClient(gui);
							gui.getFrame().setVisible(false);
						}
					}
					break;
				case KeyEvent.VK_LEFT:
					if (con.getKeyCommand()==1){
						System.out.println("alt+left");	
						con.setKeyCommand(0);
						con.getServer();
						if (!Server.clients.isEmpty()){
							con.getServer().sendScreenToClient(gui);
							gui.getFrame().setVisible(false);
						}
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (con.getKeyCommand()==1){
						System.out.println("alt+right");	
						con.setKeyCommand(0);
						con.getServer();
						if (!Server.clients.isEmpty()){
							con.getServer().sendScreenToClient(gui);
							gui.getFrame().setVisible(false);
						}
					}
					break;
				default:
					con.setKeyCommand(0);
					break;
				}			
	            return false;
	        }
	    }

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new MyDispatcher());
	}
	
	public void addButtonHandelers() {
		gui.getBtnVerbinden().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// eerst controleren of er een geldig ip en poort is ingevuld
				try{
					Integer poort = Integer.parseInt(gui.getTxtPoort().getText());
					String ip = gui.getTxtIP().getText();
					// ***.***.***.*** <- 15 tekens
					if (ip.length()<=15){
						//ipv4
						// er zitten 3 punten in
						if (ip.replace(".", "").length()==ip.length()-3){
							connect(ip,poort);
							//gui.getBtnVerbinden().setEnabled(false);
						}
						else{
							MainGui.getTxtAreaLog().append("Het IP adres is niet juist\n");	
						}
					}
					else if(ip.length()<=39){
						//ipv6
						// er zitten 7 dubble punt in in
						if (ip.replace(":", "").length()==ip.length()-7){
							connect(ip,poort);
						}
						else{
							MainGui.getTxtAreaLog().append("Het IP adres is niet juist\n");	
						}
					}
					
					else{
						MainGui.getTxtAreaLog().append("Het IP adres is te lang\n");
					}
				}
				catch(NumberFormatException ex){
					MainGui.getTxtAreaLog().append("De poort is onjuist, probeer alleen cijfers in te voeren\n");
				}
				
			}
			
			private void connect(String ip, int poort){
				if (con.searchOrCreate(ip,poort).equals("Server Online")){
					MainGui.getTxtAreaLog().append("Server is online\n");
					gui.getFrame().setTitle("Client Side");
					// server online
				}
				else{
					MainGui.getTxtAreaLog().append("Kan de server niet vinden\n");
					MainGui.getTxtAreaLog().append("Er is een nieuwe server aangemaakt\n");
					gui.getFrame().setTitle("Server Side");
					gui.setModeChoice();
					// server offline
				}
			}
		});
		
		gui.getRDbtnExtendMode().addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (con.getServer()!=null){
					if (e.getStateChange()==ItemEvent.SELECTED){
						con.setMode(MainControler.modeExtend);
						con.getServer().sendMode(MainControler.modeExtend);
					}
				}
			}
		}); 
		gui.getRDbtnMirrorMode().addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (con.getServer()!=null){
					if (e.getStateChange()==ItemEvent.SELECTED){
						con.setMode(MainControler.modeMiror);
						con.getServer().sendMode(MainControler.modeMiror);
					}
				}
			}
		});
	}
}
