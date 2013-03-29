package us.wthr.jdem846ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import us.wthr.jdem846.shapefile.ShapeBase;

public class ShapeBaseEditorInput implements IEditorInput
{

	private ShapeBase shapeBase;
	
	public ShapeBaseEditorInput(ShapeBase shapeBase)
	{
		this.shapeBase = shapeBase;
	}
	
	

	@Override
	public Object getAdapter(Class arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		return shapeBase.getShapeFileReference().getPath();
	}

	@Override
	public IPersistableElement getPersistable()
	{
		return null;
	}

	@Override
	public String getToolTipText()
	{
		return getName();
	}

	
	public ShapeBase getShapeBase()
	{
		return this.shapeBase;
	}
	
}
