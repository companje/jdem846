package us.wthr.jdem846ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import us.wthr.jdem846.ElevationModel;

public class ElevationModelEditorInput implements IEditorInput
{
	
	protected ElevationModel elevationModel = null;
	
	public ElevationModelEditorInput(ElevationModel elevationModel)
	{
		this.elevationModel = elevationModel;
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
		return false;
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
		String modelSubject = elevationModel.getProperty("subject");
		String renderDate = elevationModel.getProperty("render-date");

		String name = renderDate;
		if (modelSubject != null && modelSubject.length() > 0) {
			name = modelSubject + " - " + renderDate;
		}
		return name;
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

	
	public ElevationModel getElevationModel()
	{
		return this.elevationModel;
	}
	
}
