package  com.ocp.auth_service.Repository;



import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.ocp.auth_service.entity.Session;

@Repository
public interface SessionRepository
	extends JpaRepository<Session, UUID> {

	Session findByDevice(String device);
	Session findByUserAgent(String device);
    long countByActiveTrue();

}
