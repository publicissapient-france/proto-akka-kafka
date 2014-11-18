package fr.xebia.poc.message

case class TransactionRequest(request: CreditCardNumber)

sealed trait TransactionReply

case object TransactionAccepted extends TransactionReply

case object TransactionRefused extends TransactionReply