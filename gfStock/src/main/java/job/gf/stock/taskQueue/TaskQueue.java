package job.gf.stock.taskQueue;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class TaskQueue {
	Queue<TaskAction> queue = null;
	
	public TaskQueue(){
		queue=new PriorityQueue<TaskAction>(32,new Comparator<TaskAction>(){
			@Override
			public int compare(TaskAction one, TaskAction two) {
				if(one.getNextTime()>two.getNextTime()){
					return 1;
				}else{
					return -1;
				}
			}
		});
	}
	
	public void addTask(TaskAction ta){
		queue.offer(ta);
	}
	
	public void clear(){
		queue.clear();
	}
	
	public long nextTime(){
		if(queue.size()>0){
			TaskAction ta = queue.peek();
			return ta.getNextTime();
		}
		
		return -1;
	}
	
	public TaskAction removeTask(){
		if(queue.size()>0){
			return queue.poll();
		}
		
		return null;
	}
	
	public int size(){
		return queue.size();
	}
	
	public boolean isEmpty(){
		return queue.size()==0;
	}
}
