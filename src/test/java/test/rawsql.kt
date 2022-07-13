package test

import cn.mmooo.demo.*
import cn.mmooo.demo.entity.*
import cn.mmooo.demo.ktorm.*
import org.junit.jupiter.api.*
import org.ktorm.database.*
import org.ktorm.dsl.*
import org.ktorm.schema.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.autoconfigure.liquibase.*
import org.springframework.boot.test.context.*

//@JdbcTest
@SpringBootTest(classes = [Application::class])
@ImportAutoConfiguration(LiquibaseAutoConfiguration::class)
class rawsql {


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
            ).execute()
        }
    }


    fun clearData() {
        database.deleteAll(Departments)
        database.deleteAll(Employees)
    }


    @Autowired
    lateinit var database: Database


    @Test
    fun rawSqlDemo() {
        //language=sql
        val sql = """
SELECT cast(e.hire_date as datetime) AS hireDateTime,
       CASE
           WHEN id > 2 THEN 'The id is greater than 2'
           WHEN id = 2 THEN 'The id is 2'
           ELSE 'The id is under 2'
        END                          AS idDesc,
       e.id                          AS e_id
FROM t_employee e
WHERE 1 = 1
"""

        /**
         * - binary column 的形式可以写成 binary(column)
         * - cast(函数参数特殊无法被支持) , case when(语法特殊) 等
         *      可以扩展 mysqlSqlFormatter 实现
         *
         */
        //region code
        val e = Employees.aliased("e")
        val hireDatetime =
            RawSqlColumn(
                sqlType = LocalDateTimeSqlType,
                rawSql = "cast( ${e.hireDate} as datetime )"
            )
                .aliased("hireDateTime")


        val two = 2
        val idDesc = RawSqlColumn(
            sqlType = VarcharSqlType, rawSql = """
    CASE
        WHEN id > $two THEN 'The id is greater than $two'
        WHEN id = $two THEN 'The id is $two'
        ELSE 'The id is under $two'
    END
    """
        ).aliased("idDesc")

        val query = database
            .from(e)
            .select(hireDatetime, idDesc, e.id)
            .whereWithConditions {
                it += (RawSqlColumn(sqlType = IntSqlType, rawSql = "1") eq 1)
            }
        val datas =
            query
                .asIterable()
                .map {
                    listOf(
                        it[e.id],
                        it[hireDatetime],
                        it[idDesc],
                    )
                }

        datas.forEach { println(it) }
        //endregion

    }


}
