package cn.mmooo.demo.config

import org.ktorm.database.*
import org.ktorm.expression.*
import org.ktorm.support.mysql.*
import org.springframework.context.annotation.*
import javax.sql.*

@Configuration
class KtormConfig {

    @Bean
    fun dateBase(dataSource: DataSource): Database {
        return Database.connectWithSpringSupport(
            dataSource = dataSource,
            dialect = object : MySqlDialect() {
                override fun createSqlFormatter(
                    database: Database,
                    beautifySql: Boolean,
                    indentSize: Int,
                ): SqlFormatter = RawSqlSupportSqlFormatter(MySqlFormatter(database, beautifySql, indentSize))
            })
    }
}
