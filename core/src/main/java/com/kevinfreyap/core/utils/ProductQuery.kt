package com.kevinfreyap.core.utils

import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import com.kevinfreyap.core.domain.model.filter.SearchFilter
import javax.inject.Inject

class ProductQuery @Inject constructor() {
    fun searchFilterQuery(query: String, filter: SearchFilter): SimpleSQLiteQuery {
        val sb = StringBuilder()
        val args = mutableListOf<Any>()

        sb.append("SELECT * FROM product WHERE title LIKE ? ")
        args.add("%$query%")

        if (filter.minPrice != null) {
            sb.append("AND price >= ? ")
            args.add(filter.minPrice)
        }

        if (filter.maxPrice != null) {
            sb.append("AND price <= ? ")
            args.add(filter.maxPrice)
        }

        if (filter.category != null) {
            sb.append("AND categoryName = ? ")
            args.add(filter.category)
        }

        Log.d("Query", sb.toString())
        Log.d("Query", args.toString())

        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }
}