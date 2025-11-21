package com.kevinfreyap.core

import androidx.sqlite.db.SimpleSQLiteQuery
import javax.inject.Inject

class ProductQuery @Inject constructor() {
    fun searchFilterQuery(query: String): SimpleSQLiteQuery {
        val sb = StringBuilder()
        val args = mutableListOf<Any>()

        sb.append("SELECT * FROM product WHERE title LIKE ?")
        args.add("%$query%")

        sb.append(" ORDER BY id ASC")
        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }
}