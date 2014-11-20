package fr.xebia.poc.message

case class Client(clientId: String) {
  override def toString = clientId
}
