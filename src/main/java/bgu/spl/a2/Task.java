package bgu.spl.a2;

import java.util.Collection;

/**
 * an abstract class that represents a task that may be executed using the
 * {@link WorkStealingThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the task result type
 */
public abstract class Task<R> {
		protected Deferred<R> result=new Deferred<R>();
		Processor handler;
		boolean isWaiting=false;
		Runnable callback;
		Integer num=0;
    /**
     * start handling the task - note that this method is protected, a handler
     * cannot call it directly but instead must use the
     * {@link #handle(bgu.spl.a2.Processor)} method
     */
    protected abstract void start();

    /**
     *
     * start/continue handling the task
     *
     * this method should be called by a processor in order to start this task
     * or continue its execution in the case where it has been already started,
     * any sub-tasks / child-tasks of this task should be submitted to the queue
     * of the handler that handles it currently
     *
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     *
     * @param handler the handler that wants to handle the task
     */
    /*package*/ final void handle(Processor handler) {
    	this.handler=handler;
    	if(isWaiting==false)
    	{
    		
    		start();
    	}
    	else
    	{
    		callback.run();
    	}
    }

    /**
     * This method schedules a new task (a child of the current task) to the
     * same processor which currently handles this task.
     *
     * @param task the task to execute
     */
    protected final void spawn(Task<?>... task) {
    	for(int i=0;i<task.length;i++)
    	{
    	handler.add(task[i]);
    	}
    }

    /**
     * add a callback to be executed once *all* the given tasks results are
     * resolved
     *
     * Implementors note: make sure that the callback is running only once when
     * all the given tasks completed.
     *
     * @param tasks
     * @param callback the callback to execute once all the results are resolved
     */
     protected synchronized final void whenResolved(Collection<? extends Task<?>> tasks, Runnable callback) {
    	this.num=new Integer(tasks.size());
    	isWaiting=true;
    	this.callback=callback;
    	for(Task<?> currentTask : tasks){
    		currentTask.result.whenResolved(new Substructor(this, handler));
    		}	
    	
    	
    			    
    }
class Substructor implements Runnable
{
	Task<?> toDoWhenSolved;
	Processor proccesor_to_add;
	Substructor(Task<?> toDoWhenSolved,Processor proccesor_to_add){
		this.toDoWhenSolved=toDoWhenSolved;
		this.proccesor_to_add=proccesor_to_add;
	}
	
	 public void run(){
		synchronized(this.toDoWhenSolved){
			this.toDoWhenSolved.num--;
			if(toDoWhenSolved.num==0){
				proccesor_to_add.add(toDoWhenSolved);
			}
		}
		
	}
}


    /**
     * resolve the internal result - should be called by the task derivative
     * once it is done.
     *
     * @param result - the task calculated result
     */
    protected final void complete(R result) {
    	this.result.resolve(result);
    }

    /**
     * @return this task deferred result
     */
    public final Deferred<R> getResult() {
    	return this.result;
    	
    }

}
