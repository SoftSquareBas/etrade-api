package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.UserRole;
import com.tiffa.wd.elock.paperless.core.entity.UserRolePk;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRolePk> {

    @Modifying
    @Query(value = "DELETE FROM se_user_role ur WHERE ur.user_id = :userId", nativeQuery = true)
    void deleteAllRolesByUserId(@Param("userId") Integer userId);
    
    @Modifying
    @Query(value = "DELETE FROM se_user_role ur WHERE ur.user_id = :userId AND ur.role_id = :roleId", nativeQuery = true)
    void deleteAllByUserIdAndRoldId(@Param("userId") Integer userId, @Param("roleId") Integer roleId);
    
}
