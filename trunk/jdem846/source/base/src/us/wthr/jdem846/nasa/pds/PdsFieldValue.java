package us.wthr.jdem846.nasa.pds;

public class PdsFieldValue<T>
{
	
	private T fieldValue;
	
	public PdsFieldValue(T fieldValue)
	{
		this.fieldValue = fieldValue;
	}
	
	public T getFieldValue()
	{
		return fieldValue;
	}
	
	@Override
	public String toString()
	{
		if (fieldValue != null) {
			return fieldValue.toString();
		} else {
			return null;
		}
	}

	@Override
	public int hashCode()
	{
		String v = toString();
		return (v != null) ? v.hashCode() : 0;
	}
	
}
