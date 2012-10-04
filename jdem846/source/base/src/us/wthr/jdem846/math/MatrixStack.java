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
	
	public MatrixStack(Matrix top)
	{
		if (top != null) {
			Matrix copy = top.copy();
			push(copy);
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
		Matrix matrix = new Matrix();
		if (this.top != null) {
			this.top.matrix.copyTo(matrix);
		} else {
			matrix.loadIdentity();
		}
		
		push(matrix);
		return matrix;
	}
	
	public void push(Matrix add)
	{
		StackNode newNode = new StackNode();
		newNode.matrix = add;
		
		newNode.next = this.top;
		this.top = newNode;
		this.depth++;
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
