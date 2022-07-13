package test

import cn.mmooo.demo.entity.*
import org.junit.jupiter.api.*
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.ktorm.support.mysql.*
import java.time.*
import kotlin.random.*

class DslTest : BaseConfig() {
    @Test
    fun insert() {

        val dep = Department(
            id = null,
            name = "1111",
            location = "111",
        )
        val id = database
            .insertAndGenerateKey(Departments) {
                set(it.name, dep.name)
                set(it.location, dep.location)
            }

        dep.id = id as Int


        val emp = Employee(
            id = Random.nextInt(1000..10000),
            name = "name",
            job = "job",
            managerId = null,
            hireDate = LocalDate.now(),
            salary = 3000L,
            departmentId = dep.id,
        )

        database.insert(Employees) {
            set(it.id, emp.id)
            set(it.name, emp.name)
            set(it.job, emp.job)
            set(it.managerId, emp.managerId)
            set(it.hireDate, emp.hireDate)
            set(it.salary, emp.salary)
            set(it.departmentId, emp.departmentId)
        }

    }

    @Test
    fun update() {
        //language=sql
        val sql = "UPDATE t_employee SET name = UPPER(name), job = ? WHERE id = ? "

        val e = Employees.aliased("e")

        val emp = database.employees
            .take(1)
            .single()

        database.update(e) {
            set(it.name, it.name.toUpperCase())
            set(it.job, "dev")
            where {
                it.id eq emp.id!!
            }
        }
    }


    @Test
    fun delete() {
        val e = Employees.aliased("e")
        database.delete(e) {
            it.id greaterEq 1
        }
    }

    @Test
    fun query_dsl() {
        //language=sql
        val sql = """
SELECT e.id AS e_id, e.name AS e_name, e.departmentId AS e_departmentId, d.id AS d_id
FROM t_employee e
         INNER JOIN t_department d ON e.departmentId = d.id
WHERE (((e.id IS NOT NULL) 
    AND (e.name IS NOT NULL)) 
    AND ((d.name IS NOT NULL) 
    AND (e.departmentId IS NOT NULL)))
    AND ((e.salary >= ?) 
    AND (e.hire_date < ?)) 
"""


        //region code
        val e = Employees.aliased("e")
        val d = Departments.aliased("d")
        database.from(e)
            .innerJoin(d, on = e.departmentId eq d.id )
            .select(e.id, e.name, e.departmentId, d.id)
            .whereWithConditions {
                it.add(e.id.isNotNull())
                it.add(e.name.isNotNull())
                it.add(d.name.isNotNull())
                it.add(e.departmentId.isNotNull())
                it.add(e.salary greaterEq 0L)
                it.add(e.hireDate less LocalDate.now())
            }
            .iterator()
            .forEach {
                println("e.id : " + it[e.id])
                println("e.name : " + it[e.name])
                println("e.departmentId : " + it[e.departmentId])
                println("d.id : " + it[d.id])
                println()
            }
        //endregion


    }

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
    fun query_aggr() {

//language=sql
        val sql = """
SELECT d.name AS d_name, AVG(e.salary) AS avg
FROM t_employee e
         INNER JOIN t_department d ON e.departmentId = d.id
WHERE e.job <> ?
GROUP BY e.departmentId
HAVING avg >= ?
ORDER BY avg 
 """.trimIndent()


        //region test.dsl
        val e = Employees.aliased("e")
        val d = Departments.aliased("d")
        val avg = avg(e.salary).aliased("avg")
        val job2SalaryAvg =
            database.from(e)
                .innerJoin(d, on = e.departmentId eq d.id)
                .select(d.name, avg)
                .groupBy(e.departmentId)
                .whereWithConditions {
                    it.add(e.job notEq "ceo")
                }
                .having(avg greaterEq 200.0)
                .orderBy(avg.asc())
                .asIterable()
                .map {
                    val depName = it[d.name]
                    val avgSalary = it[avg]
                    depName to avgSalary
                }


        job2SalaryAvg.forEach {
            println(it)
        }
        //endregion
    }


}
