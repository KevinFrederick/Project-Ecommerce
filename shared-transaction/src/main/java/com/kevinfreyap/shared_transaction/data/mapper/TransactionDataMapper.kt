package com.kevinfreyap.shared_transaction.data.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kevinfreyap.shared_transaction.data.source.local.entity.TransactionEntity
import com.kevinfreyap.shared_transaction.domain.model.TransactionAddress
import com.kevinfreyap.shared_transaction.domain.model.TransactionItem
import com.kevinfreyap.shared_transaction.domain.model.TransactionReceipt
import kotlin.jvm.java

object TransactionDataMapper {
    private val gson = Gson()

    fun mapOrderDomainToEntity(domain: TransactionReceipt): TransactionEntity {
        return TransactionEntity(
            transactionId = domain.orderId,
            datePlaced = domain.datePlaced,
            totalPaid = domain.totalPaid,
            subtotal = domain.subtotal,
            shippingFee = domain.shippingFee,
            discountAmount = domain.discountAmount,
            transactionStatus = domain.transactionStatus,
            shippingAddressJson = gson.toJson(domain.shippingAddress),
            itemsPurchasedJson = gson.toJson(domain.itemsPurchased),
            paymentMethod = domain.paymentMethod
        )
    }

    fun mapTransactionEntityToDomain(entity: TransactionEntity): TransactionReceipt {
        val itemsPurchasedType = object : TypeToken<List<TransactionItem>>() {}.type

        return TransactionReceipt(
            orderId = entity.transactionId,
            datePlaced = entity.datePlaced,
            totalPaid = entity.totalPaid,
            subtotal = entity.subtotal,
            shippingFee = entity.shippingFee,
            discountAmount = entity.discountAmount,
            transactionStatus = entity.transactionStatus,
            shippingAddress = gson.fromJson(entity.shippingAddressJson, TransactionAddress::class.java),
            itemsPurchased = gson.fromJson(entity.itemsPurchasedJson, itemsPurchasedType),
            paymentMethod = entity.paymentMethod
        )
    }

    fun mapTransactionsEntityToDomain(entities: List<TransactionEntity>): List<TransactionReceipt> {
        return entities.map { mapTransactionEntityToDomain(it) }
    }
}