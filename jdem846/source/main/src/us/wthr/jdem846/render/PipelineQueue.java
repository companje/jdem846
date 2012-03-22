package us.wthr.jdem846.render;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("serial")
public class PipelineQueue<T> extends ConcurrentLinkedQueue<T>
{

	private boolean closed = false;
	
	
	public PipelineQueue()
	{
		
	}


	@Override
	public boolean add(T e)
	{
		if (!closed) {
			return super.add(e);
		} else {
			return false;
		}
	}


	@Override
	public boolean offer(T e)
	{
		if (!closed) {
			return super.offer(e);
		} else {
			return false;
		}
	}


	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		if (!closed) {
			return super.addAll(c);
		} else {
			return false;
		}
	}
	
	public void close()
	{
		closed = true;
	}
	
	public boolean isClosed()
	{
		return closed;
	}
	
	
	
}
