package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.UserPrivilege;
import com.tiffa.wd.elock.paperless.core.entity.UserPrivilegePk;

@Repository
public interface UserPrivilegeRepository extends JpaRepository<UserPrivilege, UserPrivilegePk> {

    @Modifying
    @Query(value = "DELETE FROM se_user_privilege sup WHERE sup.user_id = :userId", nativeQuery = true)
    void deleteAllPrivilesgeByUserId(@Param("userId") Integer userId);
    
    @Modifying
    @Query(value = "DELETE FROM se_user_privilege sup WHERE sup.user_id = :userId AND sup.privilege_id = :privilegeId", nativeQuery = true)
    void deleteAllByUserIdAndPrivilegeId(@Param("userId") Integer userId, @Param("privilegeId") Integer privilegeId);
    
}
