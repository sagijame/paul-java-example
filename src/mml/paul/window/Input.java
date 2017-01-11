package mml.paul.window;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mml.paul.connector.Connector;
import mml.paul.connector.thread.Sender;

@SuppressWarnings("serial")
public class Input extends TextField implements ActionListener {
	
	private MainFrame mFrame;

	public Input(MainFrame frame) {
		super();
		mFrame = frame;
		addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		selectAll();
		String str = e.getActionCommand();
		
		if ( str.equals("stop") ) {
			mFrame.stop();
			setText("");
			return;
		} else if ( str.equals("start") ) {
			mFrame.start();
			setText("");
			return;
		}
		
		if ( Connector.getType().equals(Connector.TYPE_CLIENT) ) {
			Sender.send(Connector.getSocketChannel(), str);
		}
		if ( Connector.getType().equals(Connector.TYPE_SERVER) ) {
			Sender.broadcast(null, str);
		}
		setText("");
	}

}
