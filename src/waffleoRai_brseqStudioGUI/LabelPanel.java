package waffleoRai_brseqStudioGUI;

import javax.swing.JPanel;
import java.awt.GridBagLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import javax.swing.JRadioButton;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JList;
import javax.swing.border.BevelBorder;

import waffleoRai_GUITools.RadioButtonGroup;
import waffleoRai_SeqSound.ninseq.LabelSeq;
import waffleoRai_brseqStudio.LabelWrapper;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;

public class LabelPanel extends JPanel{
	
	private static final long serialVersionUID = -6347339403422934736L;
	
	private LabelWrapper selectedLabel;
	
	private JList<LabelWrapper> list;
	private RadioButtonGroup radioButtons;
	private JButton btnUpdate;
	private JScrollPane scrollPane;
	
	public LabelPanel() 
	{
		radioButtons = new RadioButtonGroup(2);
		initGUI();
	}

	private void initGUI()
	{
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JRadioButton rbOff = new JRadioButton("No Label");
		GridBagConstraints gbc_rbOff = new GridBagConstraints();
		gbc_rbOff.anchor = GridBagConstraints.WEST;
		gbc_rbOff.insets = new Insets(5, 5, 5, 0);
		gbc_rbOff.gridx = 0;
		gbc_rbOff.gridy = 0;
		add(rbOff, gbc_rbOff);
		radioButtons.addButton(rbOff, 0);
		rbOff.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				radioButtons.select(0);
				list.setEnabled(false);
			}
			
		});
		
		JRadioButton rbOn = new JRadioButton("Set Label...");
		GridBagConstraints gbc_rbOn = new GridBagConstraints();
		gbc_rbOn.anchor = GridBagConstraints.WEST;
		gbc_rbOn.insets = new Insets(0, 5, 5, 0);
		gbc_rbOn.gridx = 0;
		gbc_rbOn.gridy = 1;
		add(rbOn, gbc_rbOn);
		radioButtons.addButton(rbOn, 1);
		rbOn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				radioButtons.select(1);
				list.setEnabled(true);
			}
			
		});
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 8;
		gbc_scrollPane.insets = new Insets(5, 5, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		add(scrollPane, gbc_scrollPane);
		
		list = new JList<LabelWrapper>();
		scrollPane.setViewportView(list);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		list.setEnabled(false);
		
		btnUpdate = new JButton("Update");
		GridBagConstraints gbc_btnUpdate = new GridBagConstraints();
		gbc_btnUpdate.insets = new Insets(5, 5, 5, 0);
		gbc_btnUpdate.gridx = 0;
		gbc_btnUpdate.gridy = 10;
		add(btnUpdate, gbc_btnUpdate);
		btnUpdate.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if(radioButtons.getSelectedIndex() == 0) selectedLabel = null;
				else selectedLabel = list.getSelectedValue();
			}
			
		});
		
		radioButtons.select(0);
		rbOn.setEnabled(false);
		//btnUpdate.setEnabled(false);
	}

	public void loadList(List<LabelSeq> labellist)
	{
		clearList();
		if(labellist == null) return;
		if(labellist.isEmpty()) return;
		List<LabelWrapper> lwlist = new LinkedList<LabelWrapper>();
		DefaultListModel<LabelWrapper> model = new DefaultListModel<LabelWrapper>();
		int i = 0;
		for(LabelSeq ls : labellist)
		{
			lwlist.add(new LabelWrapper(ls.getName(), ls.getRawOffset(), i));
			i++;
		}
		LabelWrapper.setSortOrder(LabelWrapper.SORT_ORDER_ADDRESS);
		Collections.sort(lwlist);
		for(LabelWrapper lw : lwlist) model.addElement(lw);
		list.setModel(model);
		list.setSelectedIndex(0);
		radioButtons.setEnabledAll(true);
		list.setEnabled(false);
		radioButtons.repaintAll();
		list.repaint();
	}
	
	public void clearList()
	{
		radioButtons.setEnabledAll(false);
		list.setEnabled(false);
		list.setModel(new DefaultListModel<LabelWrapper>());
		radioButtons.enable(0);
		radioButtons.select(0);
		radioButtons.repaintAll();
		list.repaint();
	}

	public LabelWrapper getSelectedLabel(){return selectedLabel;}
	
	public void addButtonListener(ActionListener l)
	{
		btnUpdate.addActionListener(l);
	}

	public void disableAll()
	{
		radioButtons.setEnabledAll(false);
		list.setEnabled(false);
		btnUpdate.setEnabled(false);
	}
	
	public void reEnable()
	{
		btnUpdate.setEnabled(true);
		int lsz = list.getModel().getSize();
		if(lsz > 0)
		{
			radioButtons.enableAll();
			if(radioButtons.getSelectedIndex() != 0) list.setEnabled(true);
		}
		else radioButtons.enable(0);
		repaintAll();
	}

	public void repaintAll()
	{
		radioButtons.repaintAll();
		list.repaint();
	}
	
}
