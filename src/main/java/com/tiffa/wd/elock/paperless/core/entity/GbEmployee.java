package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "gb_employee")
@Table(name = "gb_employee")
public class GbEmployee implements Serializable {

    private static final long serialVersionUID = 4460155681154593524L;

    @Id
    private GbEmployeePK pk;

    @Column(name = "pre_name_id", length = 3)
    private String preNameId;

    @Column(name = "t_first_name", length = 40)
    private String tFirstName;

    @Column(name = "e_first_name", length = 40)
    private String eFirstName;

    @Column(name = "t_last_name", length = 40)
    private String tLastName;

    @Column(name = "e_last_name", length = 40)
    private String eLastName;

    @Column(name = "t_name_concat", length = 100)
    private String tNameConcat;

    @Column(name = "e_name_concat", length = 100)
    private String eNameConcat;

    @Column(name = "emp_status", length = 3)
    private String empStatus;

    @Column(name = "emp_type", length = 3)
    private String empType;

    @Column(name = "sex", length = 1)
    private String sex;

    // @Column(name = "turn_type", length = 3)
    // private String turnType;

    // @Column(name = "turn_sub_id", length = 3)
    // private String turnSubId;

    // @Column(name = "punish_type", length = 6)
    // private String punishType;

    // @Column(name = "job_type", length = 3)
    // private String jobType;

    // @Column(name = "nation_id", length = 3)
    // private String nationId;

    // @Column(name = "religion_id", length = 3)
    // private String religionId;

    // @Column(name = "race_id", length = 3)
    // private String raceId;

    // @Column(name = "addr1", length = 300)
    // private String addr1;

    // @Column(name = "tel_1", length = 100)
    // private String tel1;

    // @Column(name = "addr2", length = 300)
    // private String addr2;

    // @Column(name = "tel_2", length = 100)
    // private String tel2;

    // @Column(name = "addr3", length = 300)
    // private String addr3;

    // @Column(name = "tel_3", length = 100)
    // private String tel3;

    // @Column(name = "district_id", length = 6)
    // private String districtId;

    // @Column(name = "province_id", length = 6)
    // private String provinceId;

    // @Column(name = "personal_id", length = 20)
    // private String personalId;

    // @Column(name = "personal_date")
    // private Data personalDate;

    // @Column(name = "personal_exp_date")
    // private Data personalExpDate;

    // @Column(name = "card_prov_id", length = 6)
    // private String cardProvId;

    // @Column(name = "card_dist_id", length = 6)
    // private String cardDistId;

    // @Column(name = "position_level_id", length = 5)
    // private String positionLevelId;

    // @Column(name = "position_id", length = 5)
    // private String positionId;

    // @Column(name = "division_id", length = 15)
    // private String divisionId;

    // @Column(name = "bank_id", length = 3)
    // private String bankId;

    // @Column(name = "branch_id", length = 3)
    // private String branchId;

    // @Column(name = "birthday")
    // private Date birthday;

    // @Column(name = "age")
    // private Number age;

    // @Column(name = "high")
    // private Number high;

    // @Column(name = "weight")
    // private Number weight;

    // @Column(name = "date_in")
    // private Date dateIn;

    // @Column(name = "qtyParent")
    // private Number qty_parent;

    // @Column(name = "qty")
    // private Number qty;

    // @Column(name = "flag_military", length = 1)
    // private String flagMilitary;

    // @Column(name = "military_date")
    // private Number militaryDate;

    // @Column(name = "military_year")
    // private Number militaryYear;

    // @Column(name = "military_select")
    // private Number militarySelect;

    // @Column(name = "military_desc", length = 20)
    // private String militaryDesc;

    // @Column(name = "military_org", length = 20)
    // private String militaryOrg;

    // @Column(name = "military_end")
    // private Number militaryEnd;

    // @Column(name = "taff_id", length = 2)
    // private String taffId;

    // @Column(name = "status", length = 1)
    // private String status;

    // @Column(name = "flag_work_prov", length = 1)
    // private String flagWorkProv;

    // @Column(name = "work_date")
    // private Data workDate;

    // @Column(name = "status_test")
    // private Date statusTest;

    // @Column(name = "status_interview", length = 1)
    // private String statusInterview;

    // @Column(name = "position_date")
    // private Date positionDate;

    // @Column(name = "level_date")
    // private Date levelDate;

    // @Column(name = "blood_type", length = 3)
    // private String bloodType;

    // @Column(name = "picture_id", length = 1)
    // private String pictureId;

    // @Column(name = "remark_tov", length = 50)
    // private String remarkTov;

    // @Column(name = "user_id")
    // private Number userId;

    // @Column(name = "upd_date", length = 10)
    // private String updDate;

    // @Column(name = "turnover_date")
    // private Date turnoverDate;

    // @Column(name = "remark_tov2", length = 50)
    // private String remarkTov2;

    // @Column(name = "remark_tov3", length = 50)
    // private String remarkTov3;

    // @Column(name = "finish_train_date")
    // private Date finishTrainDate;

    // @Column(name = "train_score", length = 7)
    // private String trainScore;

    // @Column(name = "status_train", length = 1)
    // private String statusTrain;

    // @Column(name = "status_card", length = 1)
    // private String statusCard;

    // @Column(name = "put_date")
    // private Date putDate;

    // @Column(name = "division_date")
    // private Date divisionDate;

    // @Column(name = "type_id", length = 3)
    // private String typeId;

    // @Column(name = "po_box", length = 20)
    // private String poBox;

    // @Column(name = "new_emp_date")
    // private Date newEmpDate;

    // @Column(name = "new_trunover_date", length = 100)
    // private Date newTrunoverDate;

    // @Column(name = "work_type", length = 3)
    // private String workType;

    // @Column(name = "flag_emp_fund", length = 1)
    // private String flagEmpFund;

    // @Column(name = "emp_func_date")
    // private Date empFuncDate;

    // @Column(name = "emp_fund_date")
    // private Date empFundDate;

    // @Column(name = "position_no", length = 10)
    // private String positionNo;

    // @Column(name = "position_division", length = 5)
    // private String positionDivision;

    // @Column(name = "vocation_accu")
    // private Number vocationAccu;

    // @Column(name = "group_work_id", length = 3)
    // private String groupWorkId;

    // @Column(name = "step_no")
    // private Number stepNo;

    // @Column(name = "amount")
    // private Number amount;

    // @Column(name = "homeadd", length = 30)
    // private String homeadd;

    // @Column(name = "moo", length = 2)
    // private String moo;

    // @Column(name = "street", length = 50)
    // private String street;

    // @Column(name = "district", length = 50)
    // private String district;

    // @Column(name = "amphur", length = 50)
    // private String amphur;

    // @Column(name = "province", length = 50)
    // private String province;

    // @Column(name = "special_name", length = 100)
    // private String specialName;

    // @Column(name = "teach_isced_id", length = 5)
    // private String teachIscedId;

    // @Column(name = "budget_id", length = 1)
    // private String budgetId;

    // @Column(name = "zipcode", length = 5)
    // private String zipcode;

    // @Column(name = "emp_flag", length = 1)
    // private String empFlag;

}
