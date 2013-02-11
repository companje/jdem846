package us.wthr.jdem846ui.editors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import us.wthr.jdem846ui.project.ProjectContext;

public class ScriptStorage implements IStorage
{

	public ScriptStorage()
	{
		
	}
	
	@Override
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	@Override
	public InputStream getContents() throws CoreException
	{
		String script = ProjectContext.getInstance().getScriptingContext().getUserScript();
		if (script != null) {
			return new ByteArrayInputStream(script.getBytes());
		} else {
			return new ByteArrayInputStream(" ".getBytes());
		}
	}

	@Override
	public IPath getFullPath()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "Script";
	}

	@Override
	public boolean isReadOnly()
	{
		return false;
	}

}
