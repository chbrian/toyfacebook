package client

import java.security.{KeyPairGenerator, SecureRandom}

import akka.actor.{ActorLogging, Actor}

/**
  * Created by xiaoyong on 12/12/2015.
  */

class User extends Actor with ActorLogging {
  val random = SecureRandom.getInstance("SHA1PRNG", "SUN")
  val keyGen = KeyPairGenerator.getInstance("RSA")
  keyGen.initialize(1024, random)
  val pairKey = keyGen.generateKeyPair()
  val privateKey = pairKey.getPrivate()
  val publicKey = pairKey.getPublic()



}
