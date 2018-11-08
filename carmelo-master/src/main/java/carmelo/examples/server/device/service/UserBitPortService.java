package carmelo.examples.server.device.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import carmelo.examples.server.device.dao.UserBitPortDao;
import carmelo.examples.server.device.domain.UserBitPort;

@Component
public class UserBitPortService {
	
	@Autowired
	private UserBitPortDao ubDao;
	
	
	@Transactional
	public int apply(int userId) {
		UserBitPort ub = UserBitPort.createUserBitPort(userId);
		ubDao.save(ub);
		return ub.getId();
	}

}
