package waffleoRai_brseqStudio;

public class LabelWrapper implements Comparable<LabelWrapper>{
	
	public static final int SORT_ORDER_ADDRESS = 0;
	public static final int SORT_ORDER_INDEX = 1;
	public static final int SORT_ORDER_NAME = 2;

	private static int sortBy;
	
	private String labelName;
	private long address;
	private int index;
	
	public LabelWrapper(String name, long addr, int idx)
	{
		if(name == null) name = "";
		labelName = name;
		address = addr;
		index = idx;
	}
	
	public String getName(){return labelName;}
	public long getAddress(){return address;}
	public int getIndex(){return index;}
	
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o == this) return true;
		if(!(o instanceof LabelWrapper)) return false;
		
		LabelWrapper l = (LabelWrapper)o;
		if(this.address != l.address) return false;
		if(this.index != l.index) return false;
		
		return this.labelName.equals(l.labelName);
	}
	
	public int hashCode()
	{
		return labelName.hashCode() ^ index ^ (int)address;
	}
	
	public static void setSortOrder(int so)
	{
		sortBy = so;
	}
	
	@Override
	public int compareTo(LabelWrapper o) 
	{
		if(o == null) return 1;
		switch(sortBy)
		{
		case SORT_ORDER_ADDRESS:
			if(this.address != o.address) return (int)(address - o.address);
			if(this.index != o.index) return (int)(index - o.index);
			return this.labelName.compareTo(o.labelName);
		case SORT_ORDER_INDEX:
			if(this.index != o.index) return (int)(index - o.index);
			int compi = this.labelName.compareTo(o.labelName);
			if(compi != 0) return compi;
			if(this.address != o.address) return (int)(address - o.address);
			break;
		case SORT_ORDER_NAME:
			int compn = this.labelName.compareTo(o.labelName);
			if(compn != 0) return compn;
			if(this.address != o.address) return (int)(address - o.address);
			if(this.index != o.index) return (int)(index - o.index);
			break;
		}
		
		return 0;
	}
	
	public String toString()
	{
		return "[" + index + "] " + labelName + " (0x" + Long.toHexString(address) + ")";
	}
	

}
