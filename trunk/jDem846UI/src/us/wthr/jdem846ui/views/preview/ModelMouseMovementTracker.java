package us.wthr.jdem846ui.views.preview;

import java.util.Map;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;

import us.wthr.jdem846.graphics.ExamineView;
import us.wthr.jdem846.graphics.ExamineView.ModelViewChangeListener;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Matrix;
import us.wthr.jdem846.math.Quaternion;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.SimpleNumberListMapSerializer;
import us.wthr.jdem846.model.ViewerPosition;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.project.context.ProjectContext;

public class ModelMouseMovementTracker implements MouseListener, MouseMoveListener, MouseWheelListener, MouseTrackListener, KeyListener
{
	private static Log log = Logging.getLog(ModelMouseMovementTracker.class);

	private double radius = .5;
	private double rotateSpeed = 1.5;
	private double zoomSpeed = 0.05;
	
	private boolean mouse1Down = false;
	private boolean mouse2Down = false;
	private boolean mouse3Down = false;
	
	private int lastX = -1;
	private int lastY = -1;

	private ExamineView examineView = new ExamineView();
	
	public ModelMouseMovementTracker()
	{
		examineView.setMinDistance(.52);
		examineView.setMaxDistance(20);
		examineView.setDistance(2);
		
		examineView.setModelRadius(.5);
		examineView.setMaxScale((examineView.getDistance() - radius) / radius);
		
		examineView.addModelViewChangeListener(new ModelViewChangeListener() {
			public void onModelViewChanged(Matrix modelView)
			{
				updateToViewerPosition();
			}
		});
		
	}

	public void dispose()
	{

	}

	protected ViewerPosition getViewer()
	{
		ViewerPosition viewer = null;

		try {
			viewer = (ViewerPosition) ProjectContext.getInstance().getOptionModelContainer(GlobalOptionModel.class).getPropertyValueById("us.wthr.jdem846.model.GlobalOptionModel.viewerPosition");
		} catch (ModelContainerException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		if (viewer == null) {
			viewer = new ViewerPosition();
		}

		return viewer;

	}

	protected void setViewer(ViewerPosition viewer)
	{

		try {
			ProjectContext.getInstance().getOptionModelContainer(GlobalOptionModel.class).setPropertyValueById("us.wthr.jdem846.model.GlobalOptionModel.viewerPosition", viewer, false);
		} catch (ModelContainerException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

	}
	
	public void updateFromViewerPosition()
	{
		ViewerPosition viewer = getViewer();
		
		Map<String, double[]> values = SimpleNumberListMapSerializer.parseDoubleListString(viewer.toString());
		
		double[] quarternion = values.get("quarternion");
		double[] pitch = values.get("pitch");
		double[] roll = values.get("roll");
		double[] yaw = values.get("yaw");
		double[] distance = values.get("distance");
		double[] scale = values.get("scale");

		examineView.setPitch(pitch[0]);
		examineView.setRoll(roll[0]);
		examineView.setYaw(yaw[0]);
		examineView.setDistance(distance[0]);
		examineView.scale(scale[0]);
		
		Quaternion orientation = new Quaternion();
		orientation.set(quarternion[0], quarternion[1], quarternion[2], quarternion[3]);
		examineView.setOrientation(orientation);
	}
	
	protected void updateToViewerPosition()
	{
		String s = examineView.toString();
		ViewerPosition viewer = ViewerPosition.fromString(s);
		setViewer(viewer);
	}

	// /////////////////////////////////////
	// MouseTrackListener
	// /////////////////////////////////////

	@Override
	public void mouseEnter(MouseEvent event)
	{

	}

	@Override
	public void mouseExit(MouseEvent event)
	{

	}

	@Override
	public void mouseHover(MouseEvent event)
	{

	}

	// /////////////////////////////////////
	// MouseWheelListener
	// /////////////////////////////////////

	@Override
	public void mouseScrolled(MouseEvent event)
	{
		
		int c = event.count;
		examineView.setDistance(examineView.getDistance() + (c * zoomSpeed));
		examineView.setMaxScale((examineView.getDistance() - 0.5) / 0.5);

	}

	// /////////////////////////////////////
	// MouseMoveListener
	// /////////////////////////////////////

	@Override
	public void mouseMove(MouseEvent event)
	{
		if (mouse1Down) {
			onMouseDraggedLeftButton(event);
		} else if (mouse2Down) {
			onMouseDraggedMiddleButton(event);
		} else if (mouse3Down) {
			onMouseDraggedRightButton(event);
		}
	}

	// /////////////////////////////////////
	// MouseListener
	// /////////////////////////////////////

	@Override
	public void mouseDoubleClick(MouseEvent event)
	{

	}

	@Override
	public void mouseDown(MouseEvent event)
	{
		switch (event.button) {
		case 1:
			mouse1Down = true;
			break;
		case 2:
			mouse2Down = true;
			break;
		case 3: 
			mouse3Down = true;
		}

		lastX = event.x;
		lastY = event.y;
	}

	@Override
	public void mouseUp(MouseEvent event)
	{
		switch (event.button) {
		case 1:
			mouse1Down = false;
			break;
		case 2:
			mouse2Down = false;
			break;
		case 3: 
			mouse3Down = false;
		}
		
		lastX = -1;
		lastY = -1;
	}
	
	
	////////////////////////////////////////
	// KeyListener
	////////////////////////////////////////
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.character) {
		case 'i':
			
			break;
		case 'o':
			
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}

	protected void onMouseDraggedRightButton(MouseEvent e)
	{
		int x = e.x;
		int y = e.y;
		
		if (lastX != -1 && lastY != -1) {

			int deltaX = x - lastX;
			int deltaY = y - lastY;

			examineView.setPitch(examineView.getPitch() + deltaY * rotateSpeed);
			examineView.setRoll(examineView.getRoll() + deltaX * rotateSpeed);

		}

		lastX = x;
		lastY = y;
	}

	protected void onMouseDraggedMiddleButton(MouseEvent e)
	{

	}

	protected void onMouseDraggedLeftButton(MouseEvent e)
	{
		int x = e.x;
		int y = e.y;
		
		if (lastX != -1 && lastY != -1) {

			int deltaX = x - lastX;
			int deltaY = y - lastY;

			examineView.rotate(deltaY * rotateSpeed, deltaX * rotateSpeed);

		}

		lastX = x;
		lastY = y;
	}
	

	
}
