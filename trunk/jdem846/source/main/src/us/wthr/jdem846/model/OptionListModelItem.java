package us.wthr.jdem846.model;

public class OptionListModelItem<E>
{
	private String label;
	private E value;
	
	public OptionListModelItem(String label, E value)
	{
		this.label = label;
		this.value = value;
	}
	
	public String toString()
	{
		return label;
	}

	public String getLabel() 
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public E getValue()
	{
		return value;
	}

	public void setValue(E value) 
	{
		this.value = value;
	}
	
	
}
