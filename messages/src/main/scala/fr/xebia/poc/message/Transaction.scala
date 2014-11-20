package fr.xebia.poc.message

import java.util.UUID

case class Transaction(uuid: UUID, clientId: String, card: String, price: Double)

case class TransactionRequest(uuid: UUID, client: Client, card: CreditCardNumber, price: Double)

sealed trait TransactionReply

case object TransactionAccepted extends TransactionReply

case object TransactionRefused extends TransactionReply