package us.wthr.jdem846ui.views.preview;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.math.Vector;
import us.wthr.jdem846.math.Vectors;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ViewerPosition;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.project.context.ProjectContext;

public class ModelMouseMovementTracker implements MouseListener, MouseMoveListener, MouseWheelListener, MouseTrackListener, KeyListener
{
	private static Log log = Logging.getLog(ModelMouseMovementTracker.class);

	private int buttonDown = -1;
	private int downX = -1;
	private int downY = -1;
	private int lastX = -1;
	private int lastY = -1;

	public ModelMouseMovementTracker()
	{

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
			ProjectContext.getInstance().getOptionModelContainer(GlobalOptionModel.class).setPropertyValueById("us.wthr.jdem846.model.GlobalOptionModel.viewerPosition", viewer);
		} catch (ModelContainerException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

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
		moveViewForward(event.count);	
	}

	// /////////////////////////////////////
	// MouseMoveListener
	// /////////////////////////////////////

	@Override
	public void mouseMove(MouseEvent event)
	{
		if (buttonDown == 1) {
			onMouseDraggedLeftButton(event);
		} else if (buttonDown == 2) {
			onMouseDraggedMiddleButton(event);
		} else if (buttonDown == 3) {
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

		buttonDown = event.button;
		downX = event.x;
		downY = event.y;
	}

	@Override
	public void mouseUp(MouseEvent event)
	{
		buttonDown = -1;
		lastX = -1;
		lastY = -1;
		downX = -1;
		downY = -1;
	}
	
	
	////////////////////////////////////////
	// KeyListener
	////////////////////////////////////////
	
	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.character) {
		case 'i':
			moveViewForward(1);
			break;
		case 'o':
			moveViewForward(-1);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}

	protected void onMouseDraggedRightButton(MouseEvent e)
	{
		rotateViewInStaticPosition(e.x, e.y);
	}

	protected void onMouseDraggedMiddleButton(MouseEvent e)
	{
		/*
		//ViewPerspective view = getViewPerspective();
		int x = e.x;
		int y = e.y;

		if (lastX != -1 && lastY != -1) {

			int deltaX = x - lastX;
			int deltaY = y - lastY;

		//	view.setShiftX(view.getShiftX() - (deltaX * 0.01));
		//	view.setShiftY(view.getShiftY() - (deltaY * 0.01));
		}

		lastX = x;
		lastY = y;

		//setViewPerspective(view);
		 */
		 
	}

	protected void onMouseDraggedLeftButton(MouseEvent e)
	{
		rotateViewAroundOrigin(e.x, e.y);
	}
	
	
	protected void moveViewForward(int count)
	{
		Vector moveVector = new Vector(0, 0, ((double)count * -0.01));
		
		ViewerPosition viewer = getViewer();
		
		Vectors.rotateY(viewer.getYaw(), moveVector);
		viewer.getPosition().z = viewer.getPosition().z + moveVector.z;
		viewer.getPosition().x = viewer.getPosition().x + moveVector.x;
		
		setViewer(viewer);
	}
	
	protected void rotateViewAroundOrigin(int mouseX, int mouseY)
	{
		ViewerPosition viewer = getViewer();

		if (lastX != -1 && lastY != -1) {

			int deltaX = mouseX - lastX;
			int deltaY = mouseY - lastY;
			
			viewer.setPitch(viewer.getPitch() + deltaY * 0.5);
			viewer.setYaw(viewer.getYaw() - deltaX * 0.5);
			Vectors.rotate(-deltaY * 0.5, -deltaX * 0.5, 0.0, viewer.getPosition());
			Vectors.rotate(-deltaY * 0.5, -deltaX * 0.5, 0.0, viewer.getFocalPoint());
			
		}

		lastX = mouseX;
		lastY = mouseY;
		
		setViewer(viewer);
	}
	
	protected void rotateViewInStaticPosition(int mouseX, int mouseY)
	{
		ViewerPosition viewer = getViewer();

		if (lastX != -1 && lastY != -1) {

			int deltaX = mouseX - lastX;
			int deltaY = mouseY - lastY;
			
			viewer.setPitch(viewer.getPitch() + deltaY * 0.3);
			viewer.setYaw(viewer.getYaw() - deltaX * 0.3);
			Vectors.rotate(deltaY * 0.3, deltaX * 0.3, 0.0, viewer.getFocalPoint());
		}

		lastX = mouseX;
		lastY = mouseY;
		
		setViewer(viewer);
	}
	
}
