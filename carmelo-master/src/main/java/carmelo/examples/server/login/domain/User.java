package carmelo.examples.server.login.domain;

import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity 
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {
	
    private int id;
    private String name;
    private String password;

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

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public static User createUser() {
    	User user = new User();
    	user.setName(createString());
    	user.setPassword(createString());
    	
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
