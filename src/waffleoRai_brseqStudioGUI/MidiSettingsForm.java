package waffleoRai_brseqStudioGUI;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import waffleoRai_GUITools.RadioButtonGroup;
import waffleoRai_SeqSound.ninseq.NinSeqMidiConverter;

import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class MidiSettingsForm extends JDialog{

	private static final long serialVersionUID = -4598062688172071692L;
	
	public static final int WIDTH = 355;
	public static final int HEIGHT = 385;
	
	private MainForm parent;
	
	private RadioButtonGroup rbg_jump;
	private RadioButtonGroup rbg_if;
	private RadioButtonGroup rbg_rand;
	private RadioButtonGroup rbg_var;
	
	private JSpinner spnVidx;
	private JTextField txtVval; 
	
	public MidiSettingsForm(MainForm parent)
	{
		super(parent, true);
		this.parent = parent;
		rbg_jump = new RadioButtonGroup(2);
		rbg_if = new RadioButtonGroup(3);
		rbg_rand = new RadioButtonGroup(2);
		rbg_var = new RadioButtonGroup(2);
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		initGUI();
	}
	
	private void initGUI()
	{
		setResizable(false);
		setTitle("MIDI Conversion Settings");
		getContentPane().setLayout(null);
		
		JLabel lblJumpMode = new JLabel("Jump Mode");
		lblJumpMode.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblJumpMode.setBounds(10, 11, 66, 14);
		getContentPane().add(lblJumpMode);
		
		JRadioButton rbJumpOn = new JRadioButton("Allow Jumps & Calls");
		rbJumpOn.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbJumpOn.setBounds(20, 32, 128, 23);
		getContentPane().add(rbJumpOn);
		rbg_jump.addButton(rbJumpOn, 0);
		rbJumpOn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_jump.select(0);
			}
			
		});
		
		JRadioButton rbJumpOff = new JRadioButton("Disallow Jumps & Calls");
		rbJumpOff.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbJumpOff.setBounds(160, 32, 140, 23);
		getContentPane().add(rbJumpOff);
		rbg_jump.addButton(rbJumpOff, 1);
		rbJumpOff.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_jump.select(1);
			}
			
		});
		
		if(parent.getJumpIgnore()) rbg_jump.select(1);
		else rbg_jump.select(0);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 62, 330, 2);
		getContentPane().add(separator);
		
		JLabel lblIfMode = new JLabel("If Mode");
		lblIfMode.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblIfMode.setBounds(10, 74, 46, 14);
		getContentPane().add(lblIfMode);
		
		JRadioButton rbIfCheck = new JRadioButton("Check");
		rbIfCheck.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbIfCheck.setBounds(20, 95, 55, 23);
		getContentPane().add(rbIfCheck);
		rbg_if.addButton(rbIfCheck, 0);
		rbIfCheck.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_if.select(0);
			}
			
		});
		
		JRadioButton rbIfTrue = new JRadioButton("Always True");
		rbIfTrue.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbIfTrue.setBounds(94, 95, 93, 23);
		getContentPane().add(rbIfTrue);
		rbg_if.addButton(rbIfTrue, 1);
		rbIfTrue.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_if.select(1);
			}
			
		});
		
		JRadioButton rbIfFalse = new JRadioButton("Always False");
		rbIfFalse.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbIfFalse.setBounds(189, 95, 93, 23);
		getContentPane().add(rbIfFalse);
		rbg_if.addButton(rbIfFalse, 2);
		rbIfFalse.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_if.select(2);
			}
			
		});
		
		int sel = parent.getIfMode();
		switch(sel)
		{
		case NinSeqMidiConverter.MIDI_IF_MODE_CHECK: rbg_if.select(0); break;
		case NinSeqMidiConverter.MIDI_IF_MODE_ALWAYS_TRUE: rbg_if.select(1); break;
		case NinSeqMidiConverter.MIDI_IF_MODE_ALWAYS_FALSE: rbg_if.select(2); break;
		}
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 125, 330, 2);
		getContentPane().add(separator_1);
		
		JLabel lblRandomMode = new JLabel("Random Mode");
		lblRandomMode.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblRandomMode.setBounds(10, 133, 81, 14);
		getContentPane().add(lblRandomMode);
		
		JRadioButton rbRandomRandom = new JRadioButton("Random");
		rbRandomRandom.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbRandomRandom.setBounds(20, 154, 71, 23);
		getContentPane().add(rbRandomRandom);
		rbg_rand.addButton(rbRandomRandom, 0);
		rbRandomRandom.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_rand.select(0);
			}
			
		});
		
		JRadioButton rbRandomDefo = new JRadioButton("Default Value");
		rbRandomDefo.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbRandomDefo.setBounds(98, 154, 89, 23);
		getContentPane().add(rbRandomDefo);
		rbg_rand.addButton(rbRandomDefo, 1);
		rbRandomDefo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_rand.select(1);
			}
			
		});
		
		sel = parent.getRandMode();
		switch(sel)
		{
		case NinSeqMidiConverter.MIDI_RANDOM_MODE_RANDOM: rbg_rand.select(0); break;
		case NinSeqMidiConverter.MIDI_RANDOM_MODE_USEDEFO: rbg_rand.select(1); break;
		}
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 184, 330, 2);
		getContentPane().add(separator_2);
		
		JLabel lblVariableMode = new JLabel("Variable Mode");
		lblVariableMode.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblVariableMode.setBounds(10, 193, 81, 14);
		getContentPane().add(lblVariableMode);
		
		JRadioButton rbVarVar = new JRadioButton("Variable");
		rbVarVar.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbVarVar.setBounds(20, 214, 71, 23);
		getContentPane().add(rbVarVar);
		rbg_var.addButton(rbVarVar, 0);
		rbVarVar.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_var.select(0);
			}
			
		});
		
		JRadioButton rbVarDefo = new JRadioButton("Default Value");
		rbVarDefo.setFont(new Font("Tahoma", Font.PLAIN, 11));
		rbVarDefo.setBounds(98, 214, 89, 23);
		getContentPane().add(rbVarDefo);
		rbg_var.addButton(rbVarDefo, 1);
		rbVarDefo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				rbg_var.select(1);
			}
			
		});
		
		sel = parent.getVarMode();
		switch(sel)
		{
		case NinSeqMidiConverter.MIDI_VAR_MODE_USEVAR: rbg_var.select(0); break;
		case NinSeqMidiConverter.MIDI_VAR_MODE_USEDEFO: rbg_var.select(1); break;
		}
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setBounds(10, 244, 330, 2);
		getContentPane().add(separator_3);
		
		JLabel lblIntialVariableValues = new JLabel("Intial Variable Values");
		lblIntialVariableValues.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblIntialVariableValues.setBounds(10, 255, 119, 14);
		getContentPane().add(lblIntialVariableValues);
		
		spnVidx = new JSpinner();
		spnVidx.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		spnVidx.setBounds(20, 277, 71, 20);
		getContentPane().add(spnVidx);
		spnVidx.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) 
			{
				short value = parent.getVariable((int)spnVidx.getValue());
				txtVval.setText(Short.toString(value));
			}
			
		});
		
		txtVval = new JTextField();
		txtVval.setBounds(111, 277, 71, 20);
		getContentPane().add(txtVval);
		txtVval.setColumns(10);
		
		short value = parent.getVariable((int)spnVidx.getValue());
		txtVval.setText(Short.toString(value));
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(192, 276, 89, 23);
		getContentPane().add(btnSave);
		btnSave.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(txtVval.getText());
					parent.setInitVar((int)spnVidx.getValue(), val);
				}
				catch(NumberFormatException x)
				{
					showError("Please enter valid integer value!");
				}
			}
			
		});
		
		JButton btnConfirm = new JButton("Confirm");
		btnConfirm.setBounds(137, 323, 89, 23);
		getContentPane().add(btnConfirm);
		btnConfirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				int selected = rbg_jump.getSelectedIndex();
				switch(selected)
				{
				case 0: parent.setJumpIgnore(false); break;
				case 1: parent.setJumpIgnore(true); break;
				}
				
				selected = rbg_if.getSelectedIndex();
				switch(selected)
				{
				case 0: parent.setIfMode(NinSeqMidiConverter.MIDI_IF_MODE_CHECK); break;
				case 1: parent.setIfMode(NinSeqMidiConverter.MIDI_IF_MODE_ALWAYS_TRUE); break;
				case 2: parent.setIfMode(NinSeqMidiConverter.MIDI_IF_MODE_ALWAYS_FALSE); break;
				}
				
				selected = rbg_rand.getSelectedIndex();
				switch(selected)
				{
				case 0: parent.setRandomMode(NinSeqMidiConverter.MIDI_RANDOM_MODE_RANDOM); break;
				case 1: parent.setRandomMode(NinSeqMidiConverter.MIDI_RANDOM_MODE_USEDEFO); break;
				}
				
				selected = rbg_var.getSelectedIndex();
				switch(selected)
				{
				case 0: parent.setVarMode(NinSeqMidiConverter.MIDI_VAR_MODE_USEVAR); break;
				case 1: parent.setVarMode(NinSeqMidiConverter.MIDI_VAR_MODE_USEDEFO); break;
				}
				setVisible(false);
			}
			
		});
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.setBounds(236, 323, 89, 23);
		getContentPane().add(btnCancel);
		btnCancel.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				setVisible(false);
			}
			
		});
		
		JSeparator separator_4 = new JSeparator();
		separator_4.setBounds(10, 310, 330, 2);
		getContentPane().add(separator_4);
	}
	
	public void showError(String message)
	{
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
}
