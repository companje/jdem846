/*
 * Copyright (C) 2011 Kevin M. Gill
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package us.wthr.jdem846.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import us.wthr.jdem846.DataSetTypes;
import us.wthr.jdem846.JDem846Properties;
import us.wthr.jdem846.i18n.I18N;
import us.wthr.jdem846.input.DataPackage;
import us.wthr.jdem846.input.DataSource;
import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;
import us.wthr.jdem846.shapefile.ShapeFileRequest;
import us.wthr.jdem846.ui.base.Panel;
import us.wthr.jdem846.ui.base.ScrollPane;
import us.wthr.jdem846.ui.base.Tree;
import us.wthr.jdem846.util.ImageIcons;

@SuppressWarnings("serial")
public class DataSetTree extends Panel
{
	private static Log log = Logging.getLog(DataSetTree.class);
	
	private Tree tree;
	private ScrollPane scrollPane;
	private DefaultMutableTreeNode top = new DefaultMutableTreeNode(I18N.get("us.wthr.jdem846.ui.dataSetTree.node.datasets"));
	private DefaultTreeModel treeModel;
	
	private DefaultMutableTreeNode elevationNode;
	private DefaultMutableTreeNode shapeNode;
	private DefaultMutableTreeNode imageryNode;
	
	private DataPackage dataPackage;
	
	private Icon polygonIcon;
	private Icon elevationIcon;
	private Icon polylineIcon;
	private Icon orthoImageryIcon;
	
	private Icon shapesCategoryIcon;
	private Icon elevationCategoryIcon;
	private Icon imageryCategoryIcon;
	
	private List<DatasetSelectionListener> datasetSelectionListeners = new LinkedList<DatasetSelectionListener>();
	
	public DataSetTree(DataPackage dataPackage)
	{
		this.dataPackage = dataPackage;
		
		
		// Load icons
    	try {
    		polygonIcon = ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/node-icon-shape-polygon.png");
    		elevationIcon = ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/node-icon-elevation.png");
    		polylineIcon = ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/node-icon-shape-line.png");
    		orthoImageryIcon = ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/node-icon-orthoimagery.png");
    		
    		elevationCategoryIcon = ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/node-icon-category-elevation.png");
    		shapesCategoryIcon = ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/node-icon-category-shapes.png");
    		imageryCategoryIcon = ImageIcons.loadImageIcon(JDem846Properties.getProperty("us.wthr.jdem846.icons.16x16") + "/node-icon-category-imagery.png");
    	} catch (IOException ex) {
			ex.printStackTrace();
			log.warn("Failed to load image icon for tree node: " + ex.getMessage(), ex);
		}
		
		
		// Create components
    	
    	treeModel = new DefaultTreeModel(top);
    	treeModel.addTreeModelListener(new TreeModelListener() {
			public void treeNodesChanged(TreeModelEvent e)
			{
				
			}
			public void treeNodesInserted(TreeModelEvent e)
			{
				
			}
			public void treeNodesRemoved(TreeModelEvent e)
			{
				
			}
			public void treeStructureChanged(TreeModelEvent e)
			{
				
			}
    	});
    	
		tree = new Tree(treeModel);
		scrollPane = new ScrollPane(tree);
		tree.setCellRenderer(new DatasetTreeCellRenderer());
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		//tree.setRootVisible(false);
		
		elevationNode = new DatasetTreeNode(elevationCategoryIcon, I18N.get("us.wthr.jdem846.ui.dataSetTree.node.elevation"));
		shapeNode = new DatasetTreeNode(shapesCategoryIcon, I18N.get("us.wthr.jdem846.ui.dataSetTree.node.shapes"));
		imageryNode =  new DatasetTreeNode(imageryCategoryIcon, I18N.get("us.wthr.jdem846.ui.dataSetTree.node.imagery"));
		
		top.add(elevationNode);
		top.add(shapeNode);
		top.add(imageryNode);
		
		// Add Listeners

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			boolean selectionEnabled = true;
			
			public void valueChanged(TreeSelectionEvent e)
			{
				if (!selectionEnabled)
					return;
				
				if (e.getNewLeadSelectionPath() == null) {
					fireDatasetSelected(null, DataSetTypes.UNSUPPORTED, -1);
					return;
				}
				
				TreePath oldPath = e.getOldLeadSelectionPath();
				Object value = e.getPath().getLastPathComponent();
				if (value instanceof DatasetTreeNode) {
					DatasetTreeNode node = (DatasetTreeNode) value;
					if (node.getType() == DatasetTreeNode.TYPE_CATEGORY) {
						selectionEnabled = false;
						tree.setSelectionPath(oldPath);
						selectionEnabled = true;
					} else {
						fireDatasetSelected(node.getDataObject(), node.getType(), node.getIndex());
					}
					
				}
			
			}
		});

		// Set Layout
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		
		updateTreeNodes();
	}
	
	public void updateTreeNodes()
	{
		
		log.info("Updating tree nodes.");
		top.removeAllChildren();
		
		elevationNode.removeAllChildren();
		shapeNode.removeAllChildren();
		imageryNode.removeAllChildren();
		
		if (dataPackage.getDataSources().size() > 0) {
			top.add(elevationNode);
		}
		
		if (dataPackage.getShapeFiles().size() > 0) {
			top.add(shapeNode);
		}
		
		
		
		//elevationNode.removeFromParent();
		
		List<DataSource> dataSources = dataPackage.getDataSources();
		for (int i = 0; i < dataSources.size(); i++) {
			DataSource dataSource = dataSources.get(i);
			log.info("Adding elevation data: " + dataSource.getFilePath());
			elevationNode.add(new DatasetTreeNode(elevationIcon, dataSource, i));
		}
		
		
		List<ShapeFileRequest> shapeFileRequests = dataPackage.getShapeFiles();
		for (int i = 0; i < shapeFileRequests.size(); i++) {
			ShapeFileRequest shapeFileRequest = shapeFileRequests.get(i);
			
			Icon icon = null;
			
			if (shapeFileRequest.getDatasetType() == DataSetTypes.SHAPE_POLYGON)
				icon = polygonIcon;
			else if (shapeFileRequest.getDatasetType() == DataSetTypes.SHAPE_POLYLINE)
				icon = polylineIcon;
			
			shapeNode.add(new DatasetTreeNode(icon, shapeFileRequest, i));
		}
		
		// TODO: Add imagery files
		
		
		treeModel.reload();
		
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
		tree.setRootVisible(false);
		
		
	}

	protected DatasetTreeNode getSelectedTreeNode()
	{
		if (tree.getSelectionPath() == null)
			return null;
		
		Object value = tree.getSelectionPath().getLastPathComponent();
		if (value instanceof DatasetTreeNode) {
			return (DatasetTreeNode) value;
		} else {
			return null;
		}
	}
	
	public int getSelectedDatasetIndex()
	{
		DatasetTreeNode node = getSelectedTreeNode();
		if (node == null || node.getType() == DatasetTreeNode.TYPE_CATEGORY) {
			return -1;
		} else {
			return node.getIndex();
		}
	}
	
	public int getSelectedDatasetType()
	{
		DatasetTreeNode node = getSelectedTreeNode();
		if (node == null || node.getType() == DatasetTreeNode.TYPE_CATEGORY) {
			return DataSetTypes.UNSUPPORTED;
		} else {
			return node.getType();
		}
	}
	
	class DatasetTreeNode extends DefaultMutableTreeNode
	{
		public static final int TYPE_ELEVATION = DataSetTypes.ELEVATION;
		public static final int TYPE_SHAPES_POLYGON = DataSetTypes.SHAPE_POLYGON;
		public static final int TYPE_SHAPES_POLYLINE = DataSetTypes.SHAPE_POLYLINE;
		public static final int TYPE_IMAGERY = DataSetTypes.IMAGERY;
		public static final int TYPE_CATEGORY = DataSetTypes.UNSUPPORTED;
		
		
		private int type = -1;
		private int index = -1;
		private Object dataObject;
		private Icon icon;
		
		public DatasetTreeNode(Icon icon, String label)
		{
			super(label);
			this.icon = icon;
			this.type = TYPE_CATEGORY;
		}
		
		public DatasetTreeNode(Icon icon, DataSource dataSource, int index)
		{
			super((new File(dataSource.getFilePath())).getName());
			this.index = index;
			this.icon = icon;
			this.type = TYPE_ELEVATION;
		}
		
		
		
		public DatasetTreeNode(Icon icon, ShapeFileRequest shapeFileRequest, int index)
		{
			super((new File(shapeFileRequest.getPath()).getName()));
			this.index = index;
			this.icon = icon;
			this.type = shapeFileRequest.getDatasetType();
		}
		
		// TODO: Add imagery constructor
		
		
		public int getIndex()
		{
			return index;
		}
		
		public int getType()
		{
			return type;
		}
		
		public Object getDataObject()
		{
			return dataObject;
		}
		
		public Icon getIcon()
		{
			return icon;
		}
	}
	
	class DatasetTreeCellRenderer extends DefaultTreeCellRenderer 
	{

	    public DatasetTreeCellRenderer() {

			
			
	    }
	    
	    
	    public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {

	    	super.getTreeCellRendererComponent(
		                tree, value, sel,
		                expanded, leaf, row,
		                hasFocus);
	    	
	    	
	    	if (value instanceof DatasetTreeNode) {
	    		DatasetTreeNode node = (DatasetTreeNode) value;
	    		if (node.getIcon() != null)
	    			this.setIcon(node.getIcon());
	    	}

	    	
	    	return this;
		}
	    
	}
	
	
	public void addDatasetSelectionListener(DatasetSelectionListener listener)
	{
		datasetSelectionListeners.add(listener);
	}
	
	public boolean removeDatasetSelectionListener(DatasetSelectionListener listener)
	{
		return datasetSelectionListeners.remove(listener);
	}
	
	protected void fireDatasetSelected(Object dataObject, int type, int index)
	{
		for (DatasetSelectionListener listener : datasetSelectionListeners) {
			listener.onDatasetSelected(dataObject, type, index);
		}
	}
	

	
	public interface DatasetSelectionListener
	{
		public void onDatasetSelected(Object dataObject, int type, int index);
	}
}
