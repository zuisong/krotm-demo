package test

import cn.mmooo.demo.*
import cn.mmooo.demo.entity.*
import org.junit.jupiter.api.*
import org.ktorm.database.*
import org.ktorm.dsl.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.test.context.*
import org.springframework.jdbc.core.namedparam.*

@SpringBootTest(classes = [Application::class])
class BaseConfig {


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

    @Autowired
    lateinit var jdbcTemplate: NamedParameterJdbcTemplate

}
