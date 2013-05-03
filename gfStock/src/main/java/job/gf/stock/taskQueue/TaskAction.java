package job.gf.stock.taskQueue;

public class TaskAction {
	public static final long SECOND_MS = 1000;
	public static final long MINUTE_MS = 60 * SECOND_MS;
	public static final long HOUR_MS = 60 * MINUTE_MS;
	public static final long DAY_MS = 24 * HOUR_MS;

	public static enum TYPE {
		TIMES, // 运行一定数量次数
		AGAIN// 重复运行
	}

	// 类型
	private TYPE type;
	// 次数
	private long times = 1;
	// 更新时间:ms
	private long updateTime;
	// 下一次时间
	private long nextTime = 0;
	// 一共运行多少次时间
	private long runTimes = 0;

	// 参数用
	private Object params;
	private int action = 0;
	private String name;

	// 计算一次运行时间,有返回true,没有false
	public boolean next() {
		if (type == null)
			return false;

		if (type == TYPE.TIMES) {
			if (runTimes >= times)
				return false;

			runTimes++;
			if (updateTime > 0) {
				if (nextTime > 0) {
					nextTime = nextTime + updateTime;
				} else {
					nextTime = System.currentTimeMillis() + updateTime;
				}
			}

			return true;
		} else if (type == TYPE.AGAIN) {
			runTimes++;
			if (updateTime > 0) {
				if (nextTime > 0) {
					nextTime = nextTime + updateTime;
				} else {
					nextTime = System.currentTimeMillis() + updateTime;
				}
			}

			return true;

		}

		return false;
	}

	TaskAction() {

	}

	public static TaskAction createTimesTaskAction(String name,int times) {
		return createTimesTaskAction(name,times-1, 0);
	}

	public static TaskAction createAgainTaskAction(String name,long updateTime) {
		TaskAction ta = new TaskAction();
		ta.type = TYPE.AGAIN;
		ta.updateTime = updateTime;
		ta.name=name;

		return ta;
	}

	public static TaskAction createTimesTaskAction(String name,int times, long updateTime) {
		TaskAction ta = new TaskAction();
		ta.type = TYPE.TIMES;
		ta.times = times;
		ta.updateTime = updateTime;
		ta.name=name;

		return ta;
	}

	public TYPE getType() {
		return type;
	}

	public long getTimes() {
		return times;
	}

	public void setTimes(long times) {
		this.times = times;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public long getNextTime() {
		return nextTime;
	}

	public void setNextTime(long nextTime) {
		this.nextTime = nextTime;
	}

	public Object getParams() {
		return params;
	}

	public void setParams(Object params) {
		this.params = params;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}


}
