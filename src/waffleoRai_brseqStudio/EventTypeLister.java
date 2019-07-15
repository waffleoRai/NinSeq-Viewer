package waffleoRai_brseqStudio;

import waffleoRai_SeqSound.ninseq.NSCommand;

public class EventTypeLister {
	
	private byte minCmd;
	private byte maxCmd;
	
	private NSCommand cmd_enum;
	
	public EventTypeLister(byte min, byte max, NSCommand cmd)
	{
		minCmd = min;
		maxCmd = max;
		cmd_enum = cmd;
	}
	
	public EventTypeLister(NSCommand cmd)
	{
		cmd_enum = cmd;
		minCmd = cmd_enum.getCommandByte();
		maxCmd = minCmd;
	}
	
	public byte getMinCommandByte()
	{
		return minCmd;
	}
	
	public byte getMaxCommandByte()
	{
		return maxCmd;
	}
	
	public NSCommand getCommandEnum()
	{
		return cmd_enum;
	}
	
	public String toString()
	{
		String s = "";
		if(minCmd != maxCmd)
		{
			s += String.format("[%02x", minCmd);
			s += " - ";
			s += String.format("%02x]", maxCmd);
		}
		else s += String.format("[%02x]", minCmd);
		s += " " + cmd_enum.name();
		return s;
	}

}
