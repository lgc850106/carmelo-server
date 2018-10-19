package carmelo.common;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringContext {
	
	private static BeanFactory beanFactory = new ClassPathXmlApplicationContext("applicationContext.xml");
	
	public static BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
	@SuppressWarnings("unchecked")
	public static Object getBean(@SuppressWarnings("rawtypes") Class clazz) {
		return beanFactory.getBean(clazz);
	}
	

}
