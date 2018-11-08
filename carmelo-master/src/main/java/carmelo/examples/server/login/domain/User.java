package carmelo.examples.server.login.domain;

import java.sql.Date;
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
public class User {
	
    private int id;
    private String name;
    private String password;
    private int compositeId;
    private String lastAccessTime;

    @Id  
    @GenericGenerator(name = "generator", strategy = "increment")  
    @GeneratedValue(generator = "generator")  
    @Column(name = "id")  
    public int getId() {
        return id;
    }

    @Column(name = "password")  
    public String getPassword() {
        return password;
    }

    @Column(name = "name")  
    public String getName() {
        return name;
    }
    
    @Column(name = "compositeId")
    public int getCompositeId() {
    	return compositeId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setCompositeId(int compositeId) {
    	this.compositeId = compositeId;
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

	public static User createUser() {
    	User user = new User();
    	user.setName(createString());
    	user.setPassword(createString());
    	//暂时使用随机数，后面要改成由composite表自动生成的id传递进来
//    	user.setCompositeId((new Random().nextInt(Integer.MAX_VALUE)));
    	user.refreshLastAccessTime();
    	return user;
    	
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
