package cn.mmooo.demo.entity

import org.ktorm.ksp.api.*
import java.time.*


@Table(tableName = "t_department")
data class Department(
    @PrimaryKey
    var id: Int?,
    var name: String,
    var location: String?,
)


@Table(tableName = "t_employee")
data class Employee(
    @PrimaryKey
    var id: Int?,
    var name: String,
    var job: String,
    var managerId: Int?,

    @Column(columnName = "hire_date")
    var hireDate: LocalDate,
    var salary: Long,
    var departmentId: Int?,
)
