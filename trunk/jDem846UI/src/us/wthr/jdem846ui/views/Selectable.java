package us.wthr.jdem846ui.views;

public class Selectable<E> {
	
	private Class<E> clazz;
	
	public Selectable(Class<E> clazz)
	{
		this.clazz = clazz;
	}
	
	public Class<E> getClazz()
	{
		return this.clazz;
	}
	
}
