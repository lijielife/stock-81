package job.gf.stock;

public class MarketInfo {
	// 大盘数据
	private double startShanghaiIndex = 100;
	private double shanghaiIndex = 100;
	private double highShanghaiIndex = 100;
	private double lowShangghaiIndex = 100;
	private long index=0;

	public double getShanghaiIndex() {
		return shanghaiIndex;
	}

	public void setShanghaiIndex(double shanghaiIndex) {
		if(shanghaiIndex>highShanghaiIndex){
			highShanghaiIndex=shanghaiIndex;
		}else if(shanghaiIndex<lowShangghaiIndex){
			lowShangghaiIndex=shanghaiIndex;
		}
		this.shanghaiIndex = shanghaiIndex;
	}

	public double getStartShanghaiIndex() {
		return startShanghaiIndex;
	}

	public void setStartShanghaiIndex(double startShanghaiIndex) {
		this.startShanghaiIndex = startShanghaiIndex;
	}

	public double getHighShanghaiIndex() {
		return highShanghaiIndex;
	}

	public void setHighShanghaiIndex(double highShanghaiIndex) {
		this.highShanghaiIndex = highShanghaiIndex;
	}

	public double getLowShangghaiIndex() {
		return lowShangghaiIndex;
	}

	public void setLowShangghaiIndex(double lowShangghaiIndex) {
		this.lowShangghaiIndex = lowShangghaiIndex;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

}
