package fr.xebia.poc.message

import java.util.UUID

case class Payment(creditCardNumber: CreditCardNumber, amount: Double, name: String) {
   val uuid: String = UUID.randomUUID().toString
 }
