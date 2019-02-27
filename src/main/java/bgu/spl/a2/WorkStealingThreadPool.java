package bgu.spl.a2;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {
	
	
	
	/*insert to the queues in start using put(e)
	  *  take from end using takeLast()   
	  *  STEAL half from the start using takeFirst()
	  *https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/BlockingDeque.html
	  */
	private Vector<ConcurrentLinkedDeque<Task<?>>> tasks_pool;

	
	private Processor[] processors_pool;
	private VersionMonitor monitor;
	private Thread[] thread_pool;
	boolean isShutDown;
	Thread mainThread; 
    /**
     * creates a {@link WorkStealingThreadPool} which has nthreads
     * {@link Processor}s. Note, threads should not get started until calling to
     * the {@link #start()} method.
     *
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this
     * thread pool
     */
    public WorkStealingThreadPool(int nthreads) {
    	isShutDown=false;
    	processors_pool=new Processor[nthreads];
    	thread_pool=new Thread[nthreads];
    	
    	tasks_pool = new Vector<ConcurrentLinkedDeque<Task<?>>>(nthreads);
		for(int i=0;i<nthreads;i++)
    	{
    		this.tasks_pool.add(i,new ConcurrentLinkedDeque<Task<?>>());
    		this.processors_pool[i]=new Processor(i, this);
    		thread_pool[i]=new Thread(processors_pool[i]);
    	}
		this.monitor=new VersionMonitor();
    }

    /**
     * submits a task to be executed by a processor belongs to this thread pool
     *
     * @param task the task to execute
     */
    public void submit(Task<?> task) {
    	int rand=(new Random()).nextInt((this.processors_pool.length));
    	submit(task,rand);
    }

     void submit(Task<?> task,int proc_id) {
    	 
		tasks_pool.get(proc_id).addFirst(task);
		this.monitor.inc();
    }

    /**
     * closes the thread pool - this method interrupts all the threads and wait
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     *
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is
     * interrupted
     * @throws UnsupportedOperationException if the thread that attempts to
     * shutdown the queue is itself a processor of this queue
     */
    public void shutdown() throws InterruptedException {
    	if(isShutDown)
    		throw new InterruptedException("attempt to shut down after first shutDown");
    	isShutDown=true;
    	for(int i=0;i<processors_pool.length;i++)
    	{
    		if(thread_pool[i]==Thread.currentThread()){
    			throw new UnsupportedOperationException("the thread that attempts to shutdown the queue is itself a processor of this queue");
    		}
    	}
    	for(int i=0;i<processors_pool.length;i++)
    	{
    		thread_pool[i].interrupt();
    	}
    	
    	for(int i=0;i<processors_pool.length;i++)
    	{
    		thread_pool[i].join();
    	}
    	
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
    	for(Thread thread : thread_pool){
    		thread.start();
    	}
    
    }

    Task<?> fetch(int processor_id) {
		Task<?> result = null;
		while(!isShutDown&&result==null){
		if(tasks_pool.get(processor_id).isEmpty())
			this.steal(processor_id);
		//at this point, this proccesor has at least one task(weather it's his or he stole it)
		if(!isShutDown)
		result=tasks_pool.get(processor_id).pollLast();
		//if it failed to fetch a non-null task, because until we got to the poll other proccesor/s stole of of this
		//proccesor Tasks, return to the begining of the while
		}
		return result;
	}
    
	private void steal(int processor_id){

		int ver = monitor.getVersion();
		int steal_id= (processor_id+1)%(this.processors_pool.length);
		while(steal_id!=processor_id&&tasks_pool.get(processor_id).size()==0){
			boolean contin=true;
			if(contin&&tasks_pool.get(steal_id).size()>1){
				for(int i=0; i<tasks_pool.get(steal_id).size()/2; i++){
					Task<?> stealing=tasks_pool.get(steal_id).pollFirst();
					if(stealing==null)
						contin=false;
					else
						tasks_pool.get(processor_id).addFirst(stealing);
				}
			}
			
			steal_id= (steal_id+1)%(this.processors_pool.length);
		}
		if(tasks_pool.get(processor_id).size()<=0){
			try {
				monitor.await(ver);
			} catch (InterruptedException e) {

			}
		}
	}
}
