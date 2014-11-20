package fr.xebia.poc.message

sealed trait Feature

case object BlacklistEnabled extends Feature

case object GatewayBankEnabled extends Feature
