package waffleoRai_brseqStudioGUI;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import waffleoRai_SeqSound.ninseq.NSCommand;
import waffleoRai_SeqSound.ninseq.NSEvent;
import waffleoRai_SeqSound.ninseq.NinSeqDataSource;
import waffleoRai_SeqSound.ninseq.PrefixEvent;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import java.awt.Font;

public class FullSeqPanel extends JPanel{

	/*
	 * TABLE
	 * Address	Command	RawHex	Markers (Track or Label)
	 */
	
	/* ----- Constants ----- */
	
	private static final long serialVersionUID = -3682743938554760510L;
	
	/* ----- Instance Variables ----- */

	private JScrollPane scrollPane;
	private JTable table;
	
	//private NinSeq setSeq;
	private NinSeqDataSource setSeq;
	private Map<Long, Integer> addressRowMap;
	private Map<Long, List<NSEvent>> eventTargetMap;
	
	/* ----- Initialization ----- */
	
	public FullSeqPanel()
	{
		addressRowMap = new HashMap<Long, Integer>();
		eventTargetMap = new HashMap<Long, List<NSEvent>>();
		initPanel();
	}
	
	private void initPanel()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(10, 10, 10, 10);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);
		
		table = new JTable();
		table.setFont(new Font("Courier New", Font.PLAIN, 11));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
		
		scrollPane.setEnabled(false);
		table.setEnabled(false);
	}
	
	/* ----- Marker Handling ----- */
	
	private static List<NSEvent> sortTargetList(List<NSEvent> in)
	{
		if(in == null) return null;
		if(in.size() < 2) return in;
		
		List<NSEvent> out = new LinkedList<NSEvent>();
		List<NSEvent> ifs = new LinkedList<NSEvent>();
		for(NSEvent e : in)
		{
			if(e.getCommand() == NSCommand.ALLOC_TRACK) {out.add(e); break;}
		}
		for(NSEvent e : in)
		{
			if(e.getCommand() == NSCommand.OPEN_TRACK_DS || e.getCommand() == NSCommand.OPEN_TRACK_WII) out.add(e);
		}
		for(NSEvent e : in)
		{
			if(e.getCommand() == NSCommand.TRACK_END) {out.add(e); break;}
		}
		for(NSEvent e : in)
		{
			if(e.getCommand() == NSCommand.CALL_DS || e.getCommand() == NSCommand.CALL_WII) {out.add(e); break;}
		}
		for(NSEvent e : in)
		{
			if(e.getCommand() == NSCommand.RETURN) {out.add(e); break;}
		}
		for(NSEvent e : in)
		{
			if(e.getCommand() == NSCommand.JUMP_DS || e.getCommand() == NSCommand.JUMP_WII) {out.add(e); break;}
		}
		for(NSEvent e : in)
		{
			if(e.getCommand() == NSCommand.PREFIX_IF)
			{
				//Copy to a temp in and then sort THAT
				ifs.add(((PrefixEvent)e).getSubEvent());
			}
		}
		
		if(!ifs.isEmpty())
		{
			ifs = sortTargetList(ifs);
			for(NSEvent e : ifs)
			{
				out.add(new PrefixEvent(NSCommand.PREFIX_IF, e));
			}
		}
		
		return out;
	}
	
	private void buildTargetMap(Map<Long, NSEvent> seqEvents, List<Long> addrList)
	{
		//For now, we want to look at...
		//	-> Track starts
		//	-> Track ends
		//	-> Loop points (classic)
		//	-> Jump targets
		// 	-> Call targets
		//	-> Returns
		//	-> Track Alloc (Usually marks sequence start)
		
		//Also include if statements
		
		eventTargetMap.clear();
		for(Long addr : addrList)
		{
			NSEvent e = seqEvents.get(addr);
			if(e == null) continue;

			long taddr = getEventTargetAddr(e, addr);
			List<NSEvent> elist = eventTargetMap.get(taddr);
			if(elist == null)
			{
				elist = new LinkedList<NSEvent>();
				eventTargetMap.put(taddr, elist);
			}
			elist.add(e);
		}
		
		for(Long addr : addrList)
		{
			List<NSEvent> tlist = eventTargetMap.get(addr);
			if(tlist != null)
			{
				List<NSEvent> sorted = sortTargetList(tlist);
				eventTargetMap.put(addr, sorted);
			}
		}
		
	}
	
	private long getEventTargetAddr(NSEvent e, long addr)
	{
		if(e == null) return 0;
		NSCommand cmd = e.getCommand();
		if(cmd == null) return 0;
		switch(cmd)
		{
		case OPEN_TRACK_DS:
		case OPEN_TRACK_WII:
			return Integer.toUnsignedLong(e.getParam2());
		case TRACK_END:
		case RETURN:
		case ALLOC_TRACK:
			return addr;
		case JUMP_DS:
		case CALL_DS:
		case JUMP_WII:
		case CALL_WII:
			return Integer.toUnsignedLong(e.getParam1());
		case PREFIX_IF:
			return getEventTargetAddr(((PrefixEvent)e).getSubEvent(), addr);
		default: return 0;
		}
	}
	
	/* ----- Table Rendering ----- */
	
	private static class SeqTableCellRenderer extends DefaultTableCellRenderer
	{
		
		private static final long serialVersionUID = 7018243211096002922L;
		
		private Map<Integer, Color> rowColors;
		
		public SeqTableCellRenderer()
		{
			rowColors = new HashMap<Integer, Color>();
		}
		
		public void addRowColorMapping(int row, Color c)
		{
			rowColors.put(row, c);
		}
		
		public boolean hasColorMap(int row)
		{
			return rowColors.containsKey(row);
		}
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) 
		{
			setValue(value);
			Color c = rowColors.get(row);
			if(c != null) setForeground(c);
			else setForeground(Color.BLACK);
			this.setFont(new Font("Courier New", Font.PLAIN, 11));
			return this;
		}
		
	}
	
	private String[] getColumnHeaders()
	{
		String[] arr = {"Offset", "Event", "Event (Hex)", "Markers"};
		return arr;
	}
	
	public void loadSeq(NinSeqDataSource seq)
	{
		clearSeq();
		if(seq == null) return;
		setSeq = seq;
		updateTable();
	}
	
	private void updateTable()
	{
		Map<Long, NSEvent> seqEvents = setSeq.getParsedEventMap();
		//System.err.println("-DEBUG- Events parsed!");
		if(seqEvents == null) return;
		List<Long> addrList = new ArrayList<Long>(seqEvents.size() + 1);
		addrList.addAll(seqEvents.keySet());
		Collections.sort(addrList);
		
		//Build event target map
		this.buildTargetMap(seqEvents, addrList);
		
		//Initialize array
		int ecount = seqEvents.size();
		String[][] stbl = new String[ecount][4];
		
		//Scan through events and populate rows
		//Map<Integer, Color> rowColors = new HashMap<Integer, Color>();
		SeqTableCellRenderer renderer = new SeqTableCellRenderer();
		int r = 0;
		for(Long addr : addrList)
		{
			addressRowMap.put(addr, r);
			stbl[r][0] = "0x" + Long.toHexString(addr);
			NSEvent e = seqEvents.get(addr);
			if(e != null)
			{
				stbl[r][1] = e.toString();
				byte[] ser = e.serializeEvent(setSeq.isBigEndian());
				StringBuilder sb = new StringBuilder(ser.length * 3);
				for(byte b : ser) sb.append(String.format("%02x ", b));
				stbl[r][2] = sb.toString();
				
				//Markers & Color
				List<NSEvent> targeting = this.eventTargetMap.get(addr);
				if(targeting == null || targeting.isEmpty()) stbl[r][3] = "";
				else
				{
					stbl[r][3] = "";
					//Look for track alloc (Orange)
					//Look for track start (Green)
					//Look for track end (Green)
					//Look for call target (Purple)
					//Look for return (Purple)
					//Look for jump target (Blue)
					for(NSEvent te : targeting)
					{
						NSCommand tcmd = te.getCommand();
						switch(tcmd)
						{
						case ALLOC_TRACK:
							stbl[r][3] += "[Track Reallocation]";
							//rowColors.put(r, Color.ORANGE);
							renderer.addRowColorMapping(r, Color.ORANGE);
							break;
						case OPEN_TRACK_DS:
						case OPEN_TRACK_WII:
							stbl[r][3] += "[START Track " + te.getParam1() + "]";
							//if(!rowColors.containsKey(r)) rowColors.put(r, Color.GREEN);
							if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.GREEN);
							break;
						case TRACK_END:
							stbl[r][3] += "[END Track]";
							//if(!rowColors.containsKey(r)) rowColors.put(r, Color.GREEN);
							if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.GREEN);
							break;
						case CALL_DS:
						case CALL_WII:
							stbl[r][3] += "[START Function]";
							//if(!rowColors.containsKey(r)) rowColors.put(r, Color.MAGENTA);
							if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.MAGENTA);
							break;
						case RETURN:
							stbl[r][3] += "[END Function]";
							//if(!rowColors.containsKey(r)) rowColors.put(r, Color.MAGENTA);
							if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.MAGENTA);
							break;
						case JUMP_DS:
						case JUMP_WII:
							stbl[r][3] += "[Jump Target]";
							//if(!rowColors.containsKey(r)) rowColors.put(r, Color.BLUE);
							if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.BLUE);
							break;
						case PREFIX_IF:
							NSEvent sub = ((PrefixEvent)te).getSubEvent();
							switch(sub.getCommand())
							{
							case ALLOC_TRACK:
								stbl[r][3] += "[Track Reallocation]";
								//rowColors.put(r, Color.ORANGE);
								renderer.addRowColorMapping(r, Color.ORANGE);
								break;
							case OPEN_TRACK_DS:
							case OPEN_TRACK_WII:
								stbl[r][3] += "[Conditional START Track " + sub.getParam1() + "]";
								if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.YELLOW);
								break;
							case TRACK_END:
								stbl[r][3] += "[Conditional END Track]";
								if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.YELLOW);
								break;
							case CALL_DS:
							case CALL_WII:
								stbl[r][3] += "[Conditional START Function]";
								//if(!rowColors.containsKey(r)) rowColors.put(r, Color.MAGENTA);
								if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.PINK);
								break;
							case RETURN:
								stbl[r][3] += "[Conditional END Function]";
								if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.PINK);
								break;
							case JUMP_DS:
							case JUMP_WII:
								stbl[r][3] += "[Conditional Jump Target]";
								if(!renderer.hasColorMap(r)) renderer.addRowColorMapping(r, Color.CYAN);
								break;
							default: break;
							}
							break;
						default: break;
						}
					}
				}
			}
			
			r++;
		}
		
		//Build table model
		DefaultTableModel model = new DefaultTableModel(stbl, getColumnHeaders());
		
		//Set table and row colors
		table.setModel(model);
		table.setDefaultRenderer(Object.class, renderer);
		/*for(Integer row : rowColors.keySet())
		{
			Color c = rowColors.get(row);
			for(int l = 0; l < 4; l++)
			{
				TableCellRenderer tcr = table.getCellRenderer(row, l);	
				tcr.getTableCellRendererComponent(table, stbl[row][l], false, false, row, l).setForeground(c);
			}
		}*/
		
		//Update gui
		scrollPane.setEnabled(true);
		scrollPane.repaint();
		table.setEnabled(true);
		table.repaint();
	}
	
	public void clearSeq()
	{
		addressRowMap.clear();
		eventTargetMap.clear();
		setSeq = null;
		table.setModel(new DefaultTableModel());
		table.setEnabled(false);
		scrollPane.setEnabled(false);
	}
	
	public boolean gotoAddress(long addr)
	{
		System.err.println("gotoAddress Checkpoint 1");
		if(setSeq == null) return false;
		Integer row = addressRowMap.get(addr);
		if(row == null) return false;
		System.err.println("gotoAddress Checkpoint 2");
		table.setRowSelectionInterval(row, row);
		table.repaint();
		System.err.println("gotoAddress Checkpoint 3");
		return true;
	}

	/* ----- GUI Functions ----- */
	
	public void disableAll()
	{
		table.setEnabled(false);
		scrollPane.setEnabled(false);
	}
	
	public void reEnable()
	{
		table.setEnabled(setSeq != null);
		scrollPane.setEnabled(setSeq != null);
		repaintAll();
	}
	
	public void repaintAll()
	{
		scrollPane.repaint();
		table.repaint();
	}
	
}
