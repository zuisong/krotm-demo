package cn.mmooo.demo.config

import cn.mmooo.demo.ktorm.*
import org.ktorm.expression.*
import org.ktorm.support.mysql.*

open class RawSqlSupportSqlFormatter(originFormatter: MySqlFormatter) : MySqlFormatter(
    database = originFormatter.database,
    beautifySql = originFormatter.beautifySql,
    indentSize = originFormatter.indentSize,
) {
    override fun visitUnknown(expr: SqlExpression): SqlExpression {
        when (expr) {
            is RawSql -> {
                println("---$expr")
                write(" " + expr.rawSql + " ")
                return expr
            }
        }
        return super.visitUnknown(expr)
    }


}
