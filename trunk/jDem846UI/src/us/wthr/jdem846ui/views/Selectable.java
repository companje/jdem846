package us.wthr.jdem846ui.views;

import us.wthr.jdem846.IDataObject;

public class Selectable<E extends IDataObject> {
	
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
