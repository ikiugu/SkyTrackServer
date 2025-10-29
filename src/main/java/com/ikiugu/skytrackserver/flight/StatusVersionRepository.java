package com.ikiugu.skytrackserver.flight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusVersionRepository extends JpaRepository<StatusVersion, String> {}
