package us.wthr.jdem846.math;

public class MatrixStack {
	
	private StackNode top = null;
	private int depth = 0;
	
	public MatrixStack()
	{
		this(false);
	}
	
	public MatrixStack(boolean withInitialIdentity)
	{
		if (withInitialIdentity) {
			this.push();
		}
	}
	
	public int depth()
	{
		return depth;
	}
	
	public Matrix top()
	{
		if (top != null) {
			return top.matrix;
		} else {
			return null;
		}
	}
	
	public Matrix push()
	{
		StackNode newNode = new StackNode();
		newNode.matrix = new Matrix();
		
		if (this.top != null) {
			this.top.matrix.copyTo(newNode.matrix);
		} else {
			newNode.matrix.loadIdentity();
		}
		
		newNode.next = this.top;
		this.top = newNode;
		this.depth++;
		return newNode.matrix;
	}
	
	public Matrix pop()
	{
		if (this.top != null) {
			Matrix m = this.top.matrix;
			this.top = this.top.next;
			this.depth--;
			return m;
		} else {
			return null;
		}
	}
	
	
	private class StackNode 
	{
		public Matrix matrix = null;
		public StackNode next = null;
		
	}
}
