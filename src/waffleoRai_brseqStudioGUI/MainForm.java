package waffleoRai_brseqStudioGUI;

import javax.sound.midi.Sequence;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.GridBagLayout;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;

import waffleoRai_GUITools.ComponentGroup;
import waffleoRai_SeqSound.MIDI;
import waffleoRai_SeqSound.ninseq.DSSeq;
import waffleoRai_SeqSound.ninseq.LabelSeq;
import waffleoRai_SeqSound.ninseq.NinSeqDataSource;
import waffleoRai_SeqSound.ninseq.NinSeqMidiConverter;
import waffleoRai_SeqSound.ninseq.RevSeq;
import waffleoRai_Utils.FileBuffer;
import waffleoRai_Utils.FileBuffer.UnsupportedFileTypeException;
import waffleoRai_brseqStudio.LabelWrapper;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainForm extends JFrame{

	/* ----- Constants ----- */
	
	private static final long serialVersionUID = 5970775256641812083L;
	
	public static final int PATH_OPTION_OPEN = 1;
	public static final int PATH_OPTION_MIDISAVE = 2;
	
	/* ----- Instance Variables ----- */
	
	private LabelPanel lblPanel;
	private FullSeqPanel fullSeqPanel;
	
	private String lastOpenPath;
	private String lastMidiPath;
	
	private NinSeqDataSource sourceSeq;
	private Map<Long, LabelSeq> labelMap;
	
	private ComponentGroup always;
	private ComponentGroup cg_ifloaded;
	
	private boolean ignore_jumps;
	private int if_branch_mode;
	private int var_use_mode;
	private int rand_use_mode;
	private short[] var_init_values;
	
	/* ----- Initialization ----- */
	
	public MainForm()
	{
		always = new ComponentGroup();
		cg_ifloaded = new ComponentGroup();
		ignore_jumps = false;
		if_branch_mode = NinSeqMidiConverter.MIDI_IF_MODE_CHECK;
		var_use_mode = NinSeqMidiConverter.MIDI_VAR_MODE_USEVAR;
		rand_use_mode = NinSeqMidiConverter.MIDI_RANDOM_MODE_USEDEFO;
		var_init_values = new short[255];
		initGUI();
		cg_ifloaded.setEnabling(false);
	}
	
	private void initGUI()
	{
		setTitle("NinSeq Viewer");
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		always.addComponent("mnFile", mnFile);
		
		JMenuItem mntmOpen = new JMenuItem("Open...");
		mnFile.add(mntmOpen);
		always.addComponent("mntmOpen", mntmOpen);
		mntmOpen.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				openSEQ();
			}
			
		});
		
		JMenuItem mntmSearchCommands = new JMenuItem("Search for Commands...");
		mnFile.add(mntmSearchCommands);
		cg_ifloaded.addComponent("mntmSearchCommands", mntmSearchCommands);
		mntmSearchCommands.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				openCommandSearchDialog();
			}
			
		});
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JMenu mnPlay = new JMenu("Play");
		menuBar.add(mnPlay);
		always.addComponent("mnPlay", mnPlay);
		
		JMenu mnMidi = new JMenu("MIDI");
		menuBar.add(mnMidi);
		always.addComponent("mnMidi", mnMidi);
		
		JMenuItem mntmMidiConversionSettings = new JMenuItem("MIDI Conversion Settings...");
		mnMidi.add(mntmMidiConversionSettings);
		cg_ifloaded.addComponent("mntmMidiConversionSettings", mntmMidiConversionSettings);
		mntmMidiConversionSettings.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				openMIDISettingsDialog();
			}
			
		});
		
		JMenuItem mntmSaveAsMidi = new JMenuItem("Export to MIDI...");
		mnMidi.add(mntmSaveAsMidi);
		cg_ifloaded.addComponent("mntmSaveAsMidi", mntmSaveAsMidi);
		mntmSaveAsMidi.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsMIDI();
			}
			
		});
		
		
		lblPanel = new LabelPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.weightx = 0.75;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 0;
		getContentPane().add(lblPanel, gbc_panel_1);
		
		fullSeqPanel = new FullSeqPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weightx = 1.5;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 0;
		getContentPane().add(fullSeqPanel, gbc_panel);
		
		lblPanel.addButtonListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				setWait();
				LabelWrapper lw = lblPanel.getSelectedLabel();
				if(lw == null){unsetWait(); return;}
				fullSeqPanel.gotoAddress(lw.getAddress());
				unsetWait();
			}
			
		});
	}
	
	public void render()
	{
		pack();
		Point p = getCenteringCoordinates();
		setLocation(p);
		setVisible(true);
	}
	
	public Point getCenteringCoordinates()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double sWidth = screenSize.getWidth();
		double sHeight = screenSize.getHeight();
		
		int centerX = (int)(sWidth / 2.0);
		int centerY = (int)(sHeight / 2.0);
		
		int X = centerX - (this.getWidth() / 2);
		int Y = centerY - (this.getHeight() / 2);
		
		return new Point(X, Y);
	}
	
	/* ----- GUI Functions ----- */
	
	public void disableAll()
	{
		always.setEnabling(false);
		lblPanel.disableAll();
		fullSeqPanel.disableAll();
		cg_ifloaded.setEnabling(false);
	}
	
	public void updateEnabled()
	{
		always.setEnabling(true);
		always.repaint();
		
		lblPanel.reEnable();
		fullSeqPanel.reEnable();
		
		cg_ifloaded.setEnabling(sourceSeq != null);
	}
	
	public void setWait()
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		disableAll();
	}
	
	public void unsetWait()
	{
		setCursor(null);
		updateEnabled();
	}
	
	/* ----- Getters ----- */
	
	protected boolean getJumpIgnore()
	{
		return this.ignore_jumps;
	}
	
	protected int getIfMode()
	{
		return this.if_branch_mode;
	}
	
	protected int getRandMode()
	{
		return this.rand_use_mode;
	}
	
	protected int getVarMode()
	{
		return this.var_use_mode;
	}
	
	protected short getVariable(int vidx)
	{
		return this.var_init_values[vidx];
	}
	
	public NinSeqDataSource getSeq()
	{
		return this.sourceSeq;
	}
	
	/* ----- Setters ----- */
	
	protected void setInitVar(int vidx, int value)
	{
		var_init_values[vidx] = (short)value;
	}
	
	protected void setJumpIgnore(boolean b)
	{
		this.ignore_jumps = b;
	}
	
	protected void setIfMode(int mode)
	{
		this.if_branch_mode = mode;
	}
	
	protected void setRandomMode(int mode)
	{
		this.rand_use_mode = mode;
	}
	
	protected void setVarMode(int mode)
	{
		this.var_use_mode = mode;
	}
	
	/* ----- Loading ----- */
	
	private void loadRSEQ(String fpath) throws IOException, UnsupportedFileTypeException
	{
		FileBuffer file = FileBuffer.createBuffer(fpath, true);
		//WiiBRSEQ wiiseq = WiiBRSEQ.readRSEQ(file, 0);
		RevSeq wiiseq = RevSeq.readRSEQ(file);
		
		labelMap = new HashMap<Long, LabelSeq>();
		//sourceSeq = wiiseq.getFullSequence();
		sourceSeq = wiiseq.getSequenceData();
		List<String> llist = wiiseq.getLabels();
		for(String l : llist)
		{
			LabelSeq ls = wiiseq.getRawLabelInfo(l);
			labelMap.put(ls.getRawOffset(), ls);
		}
		
		loadPanels();
	}
	
	private void loadSSEQ(String fpath) throws UnsupportedFileTypeException, IOException
	{
		//System.err.println("-DEBUG- SSEQ detected!");
		FileBuffer file = FileBuffer.createBuffer(fpath, true);
		//System.err.println("File loaded!");
		//DSSeq dsseq = DSSeq.readSSEQ(file, 0);
		DSSeq dsseq = DSSeq.readSSEQ(file);
		//System.err.println("Parsing complete!");
		
		labelMap = null;
		sourceSeq = dsseq.getSequenceData();
		//System.err.println("Sequence retrieved!");
		
		loadPanels();
	}
	
	private void loadPanels()
	{
		if(labelMap != null)
		{
			List<LabelSeq> lslist = new LinkedList<LabelSeq>();
			lslist.addAll(labelMap.values());
			lblPanel.loadList(lslist);
		}
		else lblPanel.clearList();
		//System.err.println("-DEBUG- Label panel updated!");
		
		fullSeqPanel.loadSeq(sourceSeq);
		//System.err.println("-DEBUG- Seq panel updated!");
	}
	
	/* ----- Action ----- */
	
	public void openSEQ()
	{
		JFileChooser fc = new JFileChooser(lastOpenPath);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.addChoosableFileFilter(new FileFilter(){

			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				String n = f.getName();
				int dot = n.lastIndexOf('.');
				if (dot < 0) return false;
				n = n.substring(dot + 1);
				if (n.equalsIgnoreCase("brseq") | n.equalsIgnoreCase("rseq")) return true;	
				return false;
			}

			public String getDescription() {
				return "Nintendo Wii Binary Revolution Sequence (.brseq, .rseq)";
			}
			
		});
		fc.addChoosableFileFilter(new FileFilter(){

			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				String n = f.getName();
				int dot = n.lastIndexOf('.');
				if (dot < 0) return false;
				n = n.substring(dot + 1);
				if (n.equalsIgnoreCase("sseq")) return true;	
				return false;
			}

			public String getDescription() {
				return "Nintendo DS Sequence (.sseq)";
			}
			
		});
		int retVal = fc.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION)
		{
			File f = fc.getSelectedFile();
			String p = f.getAbsolutePath();
			SwingWorker<Void, Void> myTask = new SwingWorker<Void, Void>(){

				protected Void doInBackground() throws Exception {
					setWait();
					if (p == null)
					{
						showError("Path provided was null!");
						return null;
					}
					if (p.isEmpty())
					{
						showError("Path provided was empty!");
						return null;
					}
					//Open the beginning of the file to determine which it is...
					//Open it and load it
					try
					{
						FileBuffer filestart = new FileBuffer(p, 0, 0x10, true);
						String magic = filestart.getASCII_string(0, 4);
						if(magic.equals("RSEQ")) loadRSEQ(p);
						else if(magic.equals("SSEQ")) loadSSEQ(p);
						else
						{
							showError("ERROR! File type not recognized!");
							return null;
						}
					}
					catch(IOException e)
					{
						showError("ERROR! Input file could not be found!");
						e.printStackTrace();
						return null;
					}
					catch(UnsupportedFileTypeException e)
					{
						showError("ERROR! Input file could not be parsed!");
						e.printStackTrace();
						return null;
					}
					catch(Exception e)
					{
						showError("ERROR! Unknown exception caught! \n"
								+ "See stderr for details.");
						e.printStackTrace();
						return null;
					}
					
					return null;
				}
				
				public void done()
				{
					setTitle("NinSeq Viewer - " + p);
					unsetWait();
				}
				
			};
			lastOpenPath = p;
			myTask.execute();
		}
	}
	
	public void openMIDISettingsDialog()
	{
		MidiSettingsForm mydialog = new MidiSettingsForm(this);
		mydialog.setLocationRelativeTo(this);
		mydialog.setVisible(true);
		
		mydialog.dispose();
	}
	
	public void saveAsMIDI()
	{
		if(sourceSeq == null)
		{
			showError("There is no sequence loaded to export!");
			return;
		}
		
		JFileChooser fc = new JFileChooser(lastMidiPath);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.addChoosableFileFilter(new FileFilter(){

			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				String n = f.getName();
				int dot = n.lastIndexOf('.');
				if (dot < 0) return false;
				n = n.substring(dot + 1);
				if (n.equalsIgnoreCase("mid") | n.equalsIgnoreCase("midi")) return true;	
				return false;
			}

			public String getDescription() {
				return "MIDI Music Sequence (.mid)";
			}
			
		});
		int retVal = fc.showSaveDialog(this);
		
		if (retVal == JFileChooser.APPROVE_OPTION)
		{
			LabelWrapper lw = lblPanel.getSelectedLabel();
			long addr = 0;
			String opmsg = "";
			if(lw == null)
			{
				opmsg = "No label is selected. The entire sequence will be exported. Is that okay?";
			}
			else
			{
				addr = lw.getAddress();
				opmsg = "The sequence will be exported starting at label \"" + lw.getName() + "\"\n"
						+ "Address: 0x" + Long.toHexString(addr) + "\n"
						+ "Is that okay?";
			}
			int rval = JOptionPane.showOptionDialog(this, opmsg, "Save As MIDI", JOptionPane.YES_NO_OPTION, 
					JOptionPane.WARNING_MESSAGE, null, null, null);
			if(rval == JOptionPane.YES_OPTION)
			{
				File f = fc.getSelectedFile();
				String p = f.getAbsolutePath();
				long start = addr;
				SwingWorker<Void, Void> myTask = new SwingWorker<Void, Void>(){

					protected Void doInBackground() throws Exception {
						setWait();
						if (p == null)
						{
							showError("Path provided was null!");
							return null;
						}
						if (p.isEmpty())
						{
							showError("Path provided was empty!");
							return null;
						}
						//Open the beginning of the file to determine which it is...
						//Open it and load it
						try
						{
							NinSeqMidiConverter converter = new NinSeqMidiConverter(sourceSeq, start);
							converter.setMidiJumpIgnore(ignore_jumps);
							converter.setMidiIfMode(if_branch_mode);
							converter.setMidiRandomMode(rand_use_mode);
							converter.setMidiVarMode(var_use_mode);
							for(int i = 0; i < 255; i++) converter.setVariableValue(i, var_init_values[i]);
							
							converter.rewind();
							
							//Play and wait for it to finish...
							converter.play();
							
							while(converter.isPlaying())
							{
								try 
								{
									Thread.sleep(5);
								} 
								catch (InterruptedException e) 
								{
									//Assumed to be a cancel signal
									System.err.println("Cancel signal received. MIDI conversion cancelled!");
									converter.pause();
									return null;
								}
							}
							
							//Print errors found, if verbose
	
							Collection<Exception> errors = converter.getErrors();
							System.err.println("Conversion finished with " + errors.size() + " errors!");
							int i = 0;
							for(Exception e : errors)
							{
								System.err.println("------> ERROR " + i);
								e.printStackTrace();
								i++;
							}
							
							Sequence s = converter.getOutput();
							MIDI m = new MIDI(s);
							m.writeMIDI(p);
							
						}
						catch(IOException e)
						{
							showError("ERROR! File could not be written! (IOException)");
							e.printStackTrace();
							return null;
						}
						catch(Exception e)
						{
							showError("ERROR! Unknown exception caught! \n"
									+ "See stderr for details.");
							e.printStackTrace();
							return null;
						}
						
						return null;
					}
					
					public void done()
					{
						unsetWait();
					}
					
				};
				lastMidiPath = p;
				myTask.execute();
			}
			else return;
		}

	}
	
	public void openCommandSearchDialog()
	{
		FindCmdForm dialog = new FindCmdForm(this);
		dialog.setLocationRelativeTo(this);
		dialog.setVisible(true);
		
		dialog.dispose();
	}
	
	/* ----- Paths ----- */
	
	public String getLastPath(int pathOption)
	{
		switch(pathOption)
		{
		case PATH_OPTION_OPEN: return lastOpenPath;
		case PATH_OPTION_MIDISAVE: return lastMidiPath;
		}
		return null;
	}
	
	public void setLastPath(int pathOption, String path)
	{
		switch(pathOption)
		{
		case PATH_OPTION_OPEN: lastOpenPath = path;
		case PATH_OPTION_MIDISAVE: lastMidiPath = path;
		}
	}

	/* ----- Errors ----- */
	
	public void showError(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
}
