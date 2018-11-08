package carmelo.examples.server.device.domain;

import java.text.SimpleDateFormat;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity 
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserComposite implements IPort {
	
	private int id;
	private int userId;
	private int parentId;
	private String name;
	private long[] children;
	private String lastAccessTime;
	
	
	@Id  
    @GenericGenerator(name = "generator", strategy = "increment")  
    @GeneratedValue(generator = "generator")  
    @Column(name = "id") 
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Column(name = "userId")
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	@Column(name = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	@Column(name = "parentId")
	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	@Column(name = "children")
	public long[] getChildren() {
		return children;
	}
	
	public void setChildren(long[] children) {
		this.children = children;
	}
	
    @Column(name = "lastAccessTime")
    public String getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(String lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}
	
	public void refreshLastAccessTime() {
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.lastAccessTime  = dateformat.format(System.currentTimeMillis());
	}
	
    public static UserComposite createUserComposite(int userId) {
    	UserComposite userComposite = new UserComposite();
    	
    	userComposite.setUserId(userId);
    	userComposite.setName(createString());
    	userComposite.refreshLastAccessTime();
    	
    	return userComposite;
    	
    }
    
	private static String createString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= 7; i++) {
			int randomNum = new Random().nextInt(10 + 26 + 26);
			char c;
			if (randomNum <= 9)
				c = (char) ('0' + randomNum);
			else if (randomNum <= 35)
				c = (char) ('A' + randomNum - 10);
			else
				c = (char) ('a' + randomNum - 36);
			builder.append(c);
		}
		builder.append(System.currentTimeMillis());
		return builder.toString();
	}
	

}
