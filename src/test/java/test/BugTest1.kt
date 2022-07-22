package test

import cn.mmooo.demo.entity.Department
import cn.mmooo.demo.entity.Employee
import org.junit.jupiter.api.Test
import org.ktorm.dsl.*
import org.ktorm.schema.*
import java.time.LocalDate

class BugTest1 : BaseConfig() {
    @Test
    fun test1() {
        val d = Departments.aliased("d")
        val e = Employees.aliased("e")
        database.update(
            d
        ) {
            set(d.location, "localtion1")
            where {
                exists(database.from(e)
                    .select(e.id)
                    .whereWithConditions {
                        it.add(e.name eq d.name)
                        it.add(e.id eq 1)
                    }

                )
            }
        }



    }
}



```
             UPDATE t_department SET location = 'localtion1' WHERE EXISTS
             (SELECT id AS e_id FROM t_employee WHERE (name = name) AND (id = 1))
```
             UPDATE t_department SET location = 'localtion1' WHERE EXISTS
             (SELECT e.id AS e_id FROM t_employee as e WHERE (e.name = t_department.name) AND (e.id = 1))

        """.trimIndent()


open class Departments(
    alias: String? = null,
) : BaseTable<Department>(
    tableName = "t_department", alias = alias,
    entityClass = Department::class
) {
    val id: Column<Int> = int("id").primaryKey()

    val name: Column<String> = varchar("name")

    val location: Column<String> = varchar("location")

    override fun aliased(alias: String): Departments = Departments(alias)

    public override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): Department {
        TODO()
    }

    companion object : Departments()
}

open class Employees(
    alias: String? = null,
) : BaseTable<Employee>(tableName = "t_employee", alias = alias, entityClass = Employee::class) {
    val id: Column<Int> = int("id").primaryKey()

    val name: Column<String> = varchar("name")

    val job: Column<String> = varchar("job")

    val managerId: Column<Int> = int("managerId")

    val hireDate: Column<LocalDate> = date("hire_date")

    val salary: Column<Long> = long("salary")

    val departmentId: Column<Int> = int("departmentId")

    override fun aliased(alias: String): Employees = Employees(alias)

    public override fun doCreateEntity(row: QueryRowSet, withReferences: Boolean): Employee {
       TODO()
    }

    companion object : Employees()
}
