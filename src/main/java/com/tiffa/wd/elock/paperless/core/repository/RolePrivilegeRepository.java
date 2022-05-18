package com.tiffa.wd.elock.paperless.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tiffa.wd.elock.paperless.core.entity.RolePrivilege;
import com.tiffa.wd.elock.paperless.core.entity.RolePrivilegePk;

@Repository
public interface RolePrivilegeRepository extends JpaRepository<RolePrivilege, RolePrivilegePk> {

    @Modifying
    @Query(value = "DELETE FROM se_role_privilege sr WHERE sr.role_id = :roleId", nativeQuery = true)
    void deleteAllPrivilegesByRoleId(@Param("roleId") Integer roleId);
    
    @Modifying
    @Query(value = "DELETE FROM se_role_privilege sr WHERE sr.role_id = :roleId AND sr.privilege_id = :privilegeId", nativeQuery = true)
    void deleteByRoleIdAndPrivilegeId(@Param("roleId") Integer roleId, @Param("privilegeId") Integer privilegeId);
    
    
}
