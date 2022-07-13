package test

import cn.mmooo.demo.*
import cn.mmooo.demo.entity.*
import org.junit.jupiter.api.*
import org.ktorm.database.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.autoconfigure.liquibase.*
import org.springframework.boot.test.context.*
import java.time.*

@SpringBootTest(classes = [Application::class])
@ImportAutoConfiguration(LiquibaseAutoConfiguration::class)
class sequence {


    @BeforeEach
    fun initData() {
        clearData()
        database.useConnection {
            it.prepareStatement(
                """
insert into t_department(id, name, location)
                   values (1, 'Dept-1', 'Chicago'),
                          (2, 'Dept-2', 'London');

                   insert into t_employee
                       (id, name, job, managerId, hire_date, salary, departmentId)
                   values (1, 'Clark Mgr', 'ceo', null, '2020-01-01', 5000, 1),
                          (2, 'Cameron Emp', 'manager', 1, '2020-01-01', 4000, 1),
                          (3, 'Charlie Emp', 'manager', 1, '2020-01-01', 4000, 1),
                          (4, 'Layton Emp', 'dev', 2, '2020-01-01', 3000, 2),
                          (5, 'Linda Emp', 'dev', 2, '2020-01-01', 3000, 2);
               
               
           """.trimIndent()
            )
                .execute()
        }
    }


    fun clearData() {
        database.deleteAll(Departments)
        database.deleteAll(Employees)
    }


    @Autowired
    lateinit var database: Database


    @Test
    fun query_seq() {
        //language=sql
        val sql = """
SELECT t_employee.id           AS t_employee_id,
       t_employee.name         AS t_employee_name,
       t_employee.job          AS t_employee_job,
       t_employee.managerId    AS t_employee_managerId,
       t_employee.hire_date     AS t_employee_hire_date,
       t_employee.salary       AS t_employee_salary,
       t_employee.departmentId AS t_employee_departmentId
FROM t_employee
WHERE ((t_employee.hire_date >= ?)
    AND (t_employee.id >= ?))
  AND (t_employee.name IS NOT NULL)
ORDER BY t_employee.id
LIMIT ?, ?"""

        //region code
        val employeeList =
            database.employees
                .filter { it.hireDate greaterEq LocalDate.now() }
                .filter { it.id greaterEq 1 }
                .filter { it.name.isNotNull() }
                .sortedBy { it.id }
                .take(10)
                .drop(0)
                .toList()

        println(employeeList)
        //endregion
    }

    @Test
    fun add_seq() {
        //language=sql
        val sql = """
INSERT INTO t_department (name, location)
VALUES (?, ?) """

        //region code
        val dep = Department(
            id = null,
            name = "1111",
            location = "111",
        )
        database.departments.add(dep)
        assert(dep.id != null)

        //endregion
    }

    @Test
    fun update_seq() {
        //language=sql
        val sql = """
INSERT INTO t_department (name, location)
VALUES (?, ?) """

        //region code
        val dep = Department(
            id = 1,
            name = "zhangsan",
            location = null,
        )
        database.departments.update(dep)

        //endregion
    }


}
