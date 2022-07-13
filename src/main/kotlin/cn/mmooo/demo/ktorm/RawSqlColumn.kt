package cn.mmooo.demo.ktorm

import org.ktorm.expression.*
import org.ktorm.schema.*

data class RawSqlColumn<T : Any>(
    override val rawSql: String,
    override val sqlType: SqlType<T>,
) : ScalarExpression<T>(), ColumnDeclaring<T>, RawSql {

    override val extraProperties: Map<String, Any>
        get() = emptyMap()
    override val isLeafNode: Boolean
        get() = true
}
