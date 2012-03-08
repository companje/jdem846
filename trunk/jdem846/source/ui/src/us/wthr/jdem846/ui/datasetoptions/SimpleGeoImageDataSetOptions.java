package us.wthr.jdem846.ui.datasetoptions;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.image.SimpleGeoImage;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.ui.TitledRoundedPanel;
import us.wthr.jdem846.ui.base.Label;
import us.wthr.jdem846.ui.base.TextField;
import us.wthr.jdem846.ui.panels.FlexGridPanel;

@SuppressWarnings("serial")
public class SimpleGeoImageDataSetOptions extends TitledRoundedPanel
{
	private static Log log = Logging.getLog(SimpleGeoImageDataSetOptions.class);
	
	private SimpleGeoImage image;
	
	private TextField txtLimitNorth;
	private TextField txtLimitSouth;
	private TextField txtLimitEast;
	private TextField txtLimitWest;
	
	public SimpleGeoImageDataSetOptions(SimpleGeoImage image)
	{
		super(I18N.get("us.wthr.jdem846.ui.datasetoptions.simpleGeoImage.title"));
		
		this.image = image;
		
		// Create components
		txtLimitNorth = new TextField();
		txtLimitSouth = new TextField();
		txtLimitEast = new TextField();
		txtLimitWest = new TextField();
		
		FlexGridPanel controlGrid = new FlexGridPanel(2);
		
		// Add listeners
		
		FocusListener focusListener = new FocusListener() {
			public void focusGained(FocusEvent arg0) { }
			public void focusLost(FocusEvent arg0) {
				updateModelValues();
			}
		};
		txtLimitNorth.addFocusListener(focusListener);
		txtLimitSouth.addFocusListener(focusListener);
		txtLimitEast.addFocusListener(focusListener);
		txtLimitWest.addFocusListener(focusListener);
		
		
		// Set layout
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.north")));
		controlGrid.add(txtLimitNorth);
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.south")));
		controlGrid.add(txtLimitSouth);
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.east")));
		controlGrid.add(txtLimitEast);
		
		controlGrid.add(new Label(I18N.get("us.wthr.jdem846.ui.west")));
		controlGrid.add(txtLimitWest);
		
		
		setLayout(new BorderLayout());
		add(controlGrid, BorderLayout.CENTER);
		
		
		updateControlValues();
	}
	
	
	protected void updateControlValues()
	{
		txtLimitNorth.setText(""+image.getNorth());
		txtLimitSouth.setText(""+image.getSouth());
		txtLimitEast.setText(""+image.getEast());
		txtLimitWest.setText(""+image.getWest());
		
	}
	
	protected void updateModelValues()
	{
		// Needs input validation!!!!!
		image.setNorth(Double.parseDouble(txtLimitNorth.getText()));
		image.setSouth(Double.parseDouble(txtLimitSouth.getText()));
		image.setEast(Double.parseDouble(txtLimitEast.getText()));
		image.setWest(Double.parseDouble(txtLimitWest.getText()));
		
		log.info("Updated image bounds to N/S/E/W: " + image.getNorth() + "/" + image.getSouth() + "/" + image.getEast() + "/" + image.getWest());
	}
	
}
