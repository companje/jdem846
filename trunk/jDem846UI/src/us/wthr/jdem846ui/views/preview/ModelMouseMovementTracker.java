package us.wthr.jdem846ui.views.preview;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.model.GlobalOptionModel;
import us.wthr.jdem846.model.ViewPerspective;
import us.wthr.jdem846.model.exceptions.ModelContainerException;
import us.wthr.jdem846.project.context.ProjectContext;

public class ModelMouseMovementTracker implements MouseListener, MouseMoveListener, MouseWheelListener, MouseTrackListener
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

	protected ViewPerspective getViewPerspective()
	{
		ViewPerspective viewPerspective = null;

		try {
			viewPerspective = (ViewPerspective) ProjectContext.getInstance().getOptionModelContainer(GlobalOptionModel.class).getPropertyValueById("us.wthr.jdem846.model.GlobalOptionModel.viewAngle");
		} catch (ModelContainerException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		if (viewPerspective == null) {
			viewPerspective = new ViewPerspective();
		}

		return viewPerspective;

	}

	protected void setViewPerspective(ViewPerspective viewPerspective)
	{

		try {
			ProjectContext.getInstance().getOptionModelContainer(GlobalOptionModel.class).setPropertyValueById("us.wthr.jdem846.model.GlobalOptionModel.viewAngle", viewPerspective);
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
		ViewPerspective view = getViewPerspective();
		view.setZoom(view.getZoom() + (event.count * -0.1));
		setViewPerspective(view);
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

		// ignoreUpdate = true;
		// fireProjectionChangeListeners();
		// ignoreUpdate = false;
	}

	protected void onMouseDraggedRightButton(MouseEvent e)
	{
		ViewPerspective view = getViewPerspective();
		int x = e.x;
		int y = e.y;

		if (lastX != -1 && lastY != -1) {
			int deltaY = y - lastY;
			view.setShiftZ(view.getShiftZ() + (deltaY * 0.01));
		}

		lastX = x;
		lastY = y;

		setViewPerspective(view);
	}

	protected void onMouseDraggedMiddleButton(MouseEvent e)
	{
		ViewPerspective view = getViewPerspective();
		int x = e.x;
		int y = e.y;

		if (lastX != -1 && lastY != -1) {

			int deltaX = x - lastX;
			int deltaY = y - lastY;

			view.setShiftX(view.getShiftX() - (deltaX * 0.01));
			view.setShiftY(view.getShiftY() - (deltaY * 0.01));
		}

		lastX = x;
		lastY = y;

		setViewPerspective(view);
	}

	protected void onMouseDraggedLeftButton(MouseEvent e)
	{
		ViewPerspective view = getViewPerspective();
		int x = e.x;
		int y = e.y;

		if (lastX != -1 && lastY != -1) {

			int deltaX = x - lastX;
			int deltaY = y - lastY;

			view.setRotateX(view.getRotateX() + (deltaY / view.getZoom()));
			view.setRotateY(view.getRotateY() + (deltaX / view.getZoom()));
		}

		lastX = x;
		lastY = y;

		setViewPerspective(view);
	}
}
