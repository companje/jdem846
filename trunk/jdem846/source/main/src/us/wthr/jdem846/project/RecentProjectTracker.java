package us.wthr.jdem846.project;

import java.util.LinkedList;
import java.util.List;

import us.wthr.jdem846.JDem846Properties;

public class RecentProjectTracker
{
	protected static List<ProjectListListener> projectListListeners = new LinkedList<ProjectListListener>();
	
	
	
	
	
	protected static int getMaxProjectCount()
	{
		return JDem846Properties.getIntProperty("us.wthr.jdem846.general.ui.recentProjectCount");
	}
	
	
	public static List<String> getProjectList()
	{
		List<String> projectList = new LinkedList<String>();
		
		String projectListProperty = JDem846Properties.getProperty("us.wthr.jdem846.state.ui.recentProjects");
		if (projectListProperty != null) {
			
			String[] projectPaths = projectListProperty.split(",");
			
			for (String projectPath : projectPaths) {
				if (projectPath.length() > 0 && !projectList.contains(projectPath)) {
					projectList.add(projectPath);
				}
			}
			
		}
		
		return projectList;
	}
	
	
	protected static void saveProjectList(List<String> projectList)
	{
		
		String projectProperty = "";
		
		for (String projectPath : projectList) {
			projectProperty += projectPath + ",";
		}
		
		JDem846Properties.setProperty("us.wthr.jdem846.state.ui.recentProjects", projectProperty);
	}
	
	public static void addProject(String projectPath)
	{
		
		List<String> oldProjectList = getProjectList();
		
		int maxProjects = getMaxProjectCount();
		
		List<String> newProjectList = new LinkedList<String>();
		newProjectList.add(projectPath);
		
		int count = 1;
		for (String oldProjectPath : oldProjectList) {
			if (!newProjectList.contains(oldProjectPath)) {
				newProjectList.add(oldProjectPath);
				count++;
				if (count >= maxProjects) {
					break;
				}
			}
		}
		
		saveProjectList(newProjectList);
		
		fireProjectListListeners(newProjectList);
		
		
		
	}
	
	protected static void fireProjectListListeners(List<String> projectList)
	{
		for (ProjectListListener listener : RecentProjectTracker.projectListListeners) {
			listener.onRecentProjectListChanged(projectList);
		}
	}
	
	public static void addProjectListListener(ProjectListListener listener)
	{
		synchronized(RecentProjectTracker.projectListListeners) {
			RecentProjectTracker.projectListListeners.add(listener);
		}
	}
	
	
	public static boolean removeProjectListListener(ProjectListListener listener)
	{
		synchronized(RecentProjectTracker.projectListListeners) {
			return RecentProjectTracker.projectListListeners.remove(listener);
		}
	}
	
	public interface ProjectListListener
	{
		public void onRecentProjectListChanged(List<String> projectList);
	}
}
