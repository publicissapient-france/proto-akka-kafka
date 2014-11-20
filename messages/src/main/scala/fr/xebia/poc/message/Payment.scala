package fr.xebia.poc.message

sealed trait PaymentReply

case object PaymentAccepted extends PaymentReply

case object PaymentRefused extends PaymentReply