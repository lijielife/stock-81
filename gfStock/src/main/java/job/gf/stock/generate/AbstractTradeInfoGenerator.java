package job.gf.stock.generate;

public abstract class AbstractTradeInfoGenerator implements ITradeInfoGenerator{
	protected String name;
	
	public AbstractTradeInfoGenerator(String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
}
