package waffleoRai_brseqStudioGUI;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import waffleoRai_GUITools.ComponentGroup;
import waffleoRai_SeqSound.ninseq.NSCommand;
import waffleoRai_SeqSound.ninseq.NSEvent;
import waffleoRai_SeqSound.ninseq.NinSeqDataSource;
import waffleoRai_brseqStudio.EventTypeLister;

import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.ListSelectionModel;
import java.awt.Font;

public class FindCmdForm extends JDialog{

	private static final long serialVersionUID = 719603997872681499L;
	
	public static final int WIDTH = 650;
	public static final int HEIGHT = 350;
	
	private ComponentGroup disableable;
	
	private JTable table;
	private JList<EventTypeLister> list;
	
	private NinSeqDataSource data;
	
	private Map<Long, NSEvent> matches;

	public FindCmdForm(MainForm parent)
	{
		super(parent, true);
		data = parent.getSeq();
		disableable = new ComponentGroup();
		matches = new HashMap<Long, NSEvent>();
		initGUI();
	}
	
	private void initGUI()
	{
		setResizable(false);
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		
		setTitle("Find Command");
		getContentPane().setLayout(null);
		
		JScrollPane spList = new JScrollPane();
		spList.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		spList.setBounds(10, 26, 172, 151);
		getContentPane().add(spList);
		disableable.addComponent("spList", spList);
		
		list = new JList<EventTypeLister>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		spList.setViewportView(list);
		disableable.addComponent("list", list);
		
		JButton btnFind = new JButton("Find...");
		btnFind.setBounds(93, 188, 89, 23);
		getContentPane().add(btnFind);
		disableable.addComponent("btnFind", btnFind);
		btnFind.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				findButtonAction();
			}
			
		});
		
		JScrollPane spTable = new JScrollPane();
		spTable.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		spTable.setBounds(214, 26, 410, 265);
		getContentPane().add(spTable);
		disableable.addComponent("spTable", spTable);
		
		table = new JTable();
		table.setFont(new Font("Courier New", Font.PLAIN, 10));
		table.setFillsViewportHeight(true);
		spTable.setViewportView(table);
		disableable.addComponent("table", table);
		
		populateCommandSelectList();
	}

	public void setWait()
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		disableable.setEnabling(false);
	}
	
	public void unsetWait()
	{
		setCursor(null);
		disableable.setEnabling(true);
	}
	
	public void populateCommandSelectList()
	{
		int player_mode = data.getPlayerMode();
		DefaultListModel<EventTypeLister> model = new DefaultListModel<EventTypeLister>();
		
		//Play notes
		EventTypeLister o = new EventTypeLister((byte)0x00, (byte)0x7F, NSCommand.PLAY_NOTE);
		model.addElement(o);
		
		//Other
		for(int i = 0x80; i <= 0xFF; i++)
		{
			NSCommand cmd = NSCommand.getCommand(player_mode, (byte)i);
			if(cmd != null)
			{
				o = new EventTypeLister(cmd);
				model.addElement(o);
			}
		}
		
		//Set model
		list.setModel(model);
		
		//Repaint
		disableable.repaint();
		
	}
	
	private void findButtonAction()
	{
		SwingWorker<Void, Void> myTask = new SwingWorker<Void, Void>(){

			protected Void doInBackground() throws Exception {
				setWait();
				if (!findMatches())
				{
					showNotice("Sorry! No matches found!");
				}
				updateTable();
				
				return null;
			}
			
			public void done()
			{
				unsetWait();
			}
			
		};
		
		myTask.execute();
	}
	
	public boolean findMatches()
	{
		matches.clear();
		EventTypeLister o = list.getSelectedValue();
		if(o == null)
		{
			showNotice("Please select a command!");
			return false;
		}
		
		Map<Long, NSEvent> allevents = data.getParsedEventMap();
		for(Long k : allevents.keySet())
		{
			NSEvent e = allevents.get(k);
			if(e == null) continue;
			if(e.getCommand() == o.getCommandEnum()) matches.put(k, e);
		}
		
		return (!matches.isEmpty());
	}
	
	private String[] getColumnHeaders()
	{
		String[] arr = {"Offset", "Event", "Event (Hex)"};
		return arr;
	}
	
	private String[][] generateTableData()
	{
		if(matches.isEmpty())
		{
			String[][] tbl = new String[1][3];
			for(int j = 0; j < 3; j++) tbl[0][j] = "-";
			return tbl;
		}
		
		List<Long> addrlist = new ArrayList<Long>(matches.size() + 1);
		addrlist.addAll(matches.keySet());
		Collections.sort(addrlist);
		
		int count = addrlist.size();
		String[][] tbl = new String[count][3];
		for(int i = 0; i < count; i++)
		{
			long addr = addrlist.get(i);
			NSEvent ev = matches.get(addr);
			tbl[i][0] = "0x" + Long.toHexString(addr);
			tbl[i][1] = ev.toString();
			byte[] ser = ev.serializeEvent(data.isBigEndian());
			StringBuilder sb = new StringBuilder(ser.length * 3);
			for(byte b : ser) sb.append(String.format("%02x ", b));
			tbl[i][2] = sb.toString();
		}
		
		return tbl;
	}
	
	public void updateTable()
	{
		DefaultTableModel model = new DefaultTableModel(generateTableData(), getColumnHeaders());
		table.setModel(model);
		table.repaint();
	}
	
	public void showNotice(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Message", JOptionPane.WARNING_MESSAGE);
	}
	
	
}
