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

package us.wthr.jdem846.ui.base;

import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import us.wthr.jdem846.logging.Log;
import us.wthr.jdem846.logging.Logging;

@SuppressWarnings("serial")
public class Tree extends JTree
{
	private static Log log = Logging.getLog(Tree.class);

	public Tree()
	{
		super();
		
	}

	public Tree(Hashtable<?, ?> value)
	{
		super(value);
		
	}

	public Tree(Object[] value)
	{
		super(value);
		
	}

	public Tree(TreeModel newModel)
	{
		super(newModel);
		
	}

	public Tree(TreeNode root, boolean asksAllowsChildren)
	{
		super(root, asksAllowsChildren);
		
	}

	public Tree(TreeNode root)
	{
		super(root);
		
	}

	public Tree(Vector<?> value)
	{
		super(value);
		
	}
	
	
	
}
