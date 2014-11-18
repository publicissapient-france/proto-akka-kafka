package fr.xebia.poc.message

case class BlacklistRequest(request: CreditCardNumber)

sealed trait BlacklistReply

case object CardAccepted extends BlacklistReply

case object CardRefused extends BlacklistReply
