package mml.paul.window;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import mml.paul.connector.Connector;

@SuppressWarnings("serial")
public class MainFrame extends Frame implements WindowListener {
	
	public static final String TYPE_SERVER = Connector.TYPE_SERVER;
	public static final String TYPE_CLIENT = Connector.TYPE_CLIENT;
	
	private Connector mConnector;
	
	protected static Output mOutput;
	protected static Input mInput;
	
	public MainFrame(String type) {
		this.setTitle("Paul " + type);
		this.addWindowListener(this);
		
		mOutput = new Output();		
		mInput = new Input(this);		
		add("Center", mOutput);
		add("South", mInput);
		pack();
		setVisible(true);
		
		Loger mLoger = new Loger();
		mLoger.start();
		
		mConnector = new Connector(type);
		mConnector.start();
	}
	
	public static void log(String name, String text) {
		mOutput.log(name, text);
	}
	
	public void start() {
		if ( !mConnector.isRunning() )
			mConnector.start();
	}
	
	public void stop() {
		if ( mConnector.isRunning() )
			mConnector.stop();
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// Event
	
	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		try {
			stop();
		} catch ( Exception ie ) {
			
		}
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}	
	
}
