package job.gf.stock.msg;

import java.util.Date;

/*
 * 通知,重要信息发布等的载体
 */
public class Notice {
	// 类型:信息
	public static final String TYPE_INFO = "info";
	// 类型:提示
	public static final String TYPE_TIP = "tip";

	private String type;
	private String title;
	private String content;
	private Date publicTime;
	
	private Notice(){}
	
	public static Notice createTipNotice(String title,String content){
		Notice n = new Notice();
		n.type=TYPE_TIP;
		n.setTitle(title);
		n.setContent(content);
		
		return n;
	}

	public String getType() {
		return type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getPublicTime() {
		return publicTime;
	}

	public void setPublicTime(Date publicTime) {
		this.publicTime = publicTime;
	}

}
