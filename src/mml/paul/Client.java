package mml.paul;

import mml.paul.window.MainFrame;

public class Client {		
	
	@SuppressWarnings("unused")
	private static MainFrame mFrame;
	
	public static void main(String args[]) {		
		
		mFrame = new MainFrame(MainFrame.TYPE_CLIENT);
	}
}
