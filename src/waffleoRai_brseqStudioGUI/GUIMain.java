package waffleoRai_brseqStudioGUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import waffleoRai_Utils.FileBuffer;

public class GUIMain {
	
	private static String lastOpened;
	private static String lastMidiExport;
	
	public static void writeLastPaths()
	{
		try 
		{
			String dir = FileBuffer.getTempDir();
			String path = dir + File.separator + "~brseqviewerpaths.tmp";
			FileBuffer temp = new FileBuffer(1024);
			temp.printASCIIToFile(lastOpened + "\n"); 
			temp.printASCIIToFile(lastMidiExport + "\n"); 
			temp.writeFile(path);
			//System.out.println("Last opened: " + lastOpened);
		} 
		catch (IOException e) {
			e.printStackTrace();
			System.out.println("Path saving failed...");
		}
	}
	
	public static void readLastPaths()
	{
		try 
		{
			String dir = FileBuffer.getTempDir();
			String path = dir + File.separator + "~brseqviewerpaths.tmp";
			
			//Files.deleteIfExists(Paths.get(path));
			//return;
			
			FileBuffer temp = new FileBuffer(path);
			long cPos = 0;
			lastOpened = temp.getASCII_string(cPos, '\n'); cPos += lastOpened.length() + 1;
			if (lastOpened.equals("null")) lastOpened = null;
			System.out.println("Last opened: " + lastOpened);
			lastMidiExport = temp.getASCII_string(cPos, '\n'); cPos += lastMidiExport.length() + 1;
			if (lastMidiExport.equals("null")) lastMidiExport = null;
			System.out.println("Last MIDI export: " + lastMidiExport);
		} 
		catch (IOException e) {
			//e.printStackTrace();
			System.out.println("Temp path file does not exist yet or path reading failed.");
		}
	}	

	public static void main(String[] args) 
	{
		readLastPaths();
        SwingUtilities.invokeLater(new Runnable() 
        {
            public void run() 
            {
        		MainForm myGUI = new MainForm();
        		myGUI.setLastPath(MainForm.PATH_OPTION_OPEN, lastOpened);
        		myGUI.setLastPath(MainForm.PATH_OPTION_MIDISAVE, lastMidiExport);
        		myGUI.render();
        		myGUI.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                    	lastOpened = myGUI.getLastPath(MainForm.PATH_OPTION_OPEN);
                    	lastMidiExport = myGUI.getLastPath(MainForm.PATH_OPTION_MIDISAVE);
                    	writeLastPaths();
                    	System.exit(0);
                    }
                });
            }
        });
	}

}
