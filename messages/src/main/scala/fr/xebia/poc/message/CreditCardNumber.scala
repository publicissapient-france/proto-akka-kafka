package fr.xebia.poc.message

case class CreditCardNumber(digits: String) {
   override def toString = digits.grouped(4).mkString(" ")
}
