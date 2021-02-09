package hu.bsstudio.raktr.repository;

import hu.bsstudio.raktr.model.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface RentRepository extends JpaRepository<Rent, Long> {
}