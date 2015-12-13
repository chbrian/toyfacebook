import java.nio.charset.StandardCharsets
import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto._
import javax.crypto.spec.SecretKeySpec

import sun.misc.BASE64Encoder

import scala.collection.mutable.ArrayBuffer

object SecurityTester extends App {

  //
  //  val msg = "Testabwewewrc"
  //  val md = MessageDigest.getInstance("SHA-256")
  //  md.update(msg.getBytes("UTF-8"))
  //  val digest = md.digest()
  //
  //
  //  println(digest)
  //
  //  // 1:
  //  val random1 = SecureRandom.getInstance("SHA1PRNG", "SUN")
  //  // TODO check the parameters
  //  val keyGen1 = KeyPairGenerator.getInstance("RSA")
  //  keyGen1.initialize(1024, random1)
  //  val pairKey1 = keyGen1.generateKeyPair()
  //  val privateKey1 = pairKey1.getPrivate()
  //  val publicKey1 = pairKey1.getPublic()
  //
  //  // 2:
  //  val random2 = SecureRandom.getInstance("SHA1PRNG", "SUN")
  //  val keyGen2 = KeyPairGenerator.getInstance("RSA")
  //  keyGen2.initialize(1024, random2)
  //  val pairKey2 = keyGen2.generateKeyPair()
  //  val privateKey2 = pairKey2.getPrivate()
  //  val publicKey2 = pairKey2.getPublic()
  //
  //
  //  val symKey = KeyGenerator.getInstance("AES").generateKey()
  //
  //  println("publicKey1: " + publicKey1.getEncoded)
  //  println("publicKey1 length: " + publicKey1.getEncoded.length)
  //  println("publicKey2: " + publicKey2.getEncoded)
  //  println("publicKey2 length: " + publicKey2.getEncoded.length)
  //  println("symKey: " + symKey.getEncoded)
  //
  //
  //  val dsaPrivate = Signature.getInstance("SHA256withRSA")
  //  dsaPrivate.initSign(privateKey1)
  //  dsaPrivate.update(digest)
  //  val tmp1 = dsaPrivate.sign
  //  println("signature " + tmp1)
  //
  //  val c1 = Cipher.getInstance("AES/ECB/PKCS5Padding")
  //  c1.init(Cipher.ENCRYPT_MODE, symKey)
  //
  //  println("msg length: " + msg.getBytes.length)
  //  println("tmp1 length: " + tmp1.length)
  //  println("publickey1 length: " + publicKey1.getEncoded.length)
  //  val tmp2 = c1.doFinal(msg.getBytes ++ tmp1 ++ publicKey1.getEncoded)
  //
  //  println("tmp2 " + tmp2)
  //
  //  val c2 = Cipher.getInstance("RSA/ECB/PKCS1Padding")
  //  c2.init(Cipher.WRAP_MODE, publicKey2)
  //  val tmp3 = c2.wrap(symKey)
  //
  //  println("tmp3 length: " + tmp3.length)
  //  val tmp4 = tmp2 ++ tmp3
  //
  //
  //  /// decryption
  //
  //  print(tmp4.length)
  //  val left = tmp4.take(tmp4.length - 128)
  //  val right = tmp4.takeRight(128)
  //
  //  c2.init(Cipher.UNWRAP_MODE, privateKey2)
  //  val symK2 = c2.unwrap(right, "AES", Cipher.SECRET_KEY)
  //  println(symK2.getEncoded)
  //  c1.init(Cipher.DECRYPT_MODE, symK2)
  //  val tmp5 = c1.doFinal(left)
  //
  //  val tmp5Left = tmp5.take(tmp5.length - 162)
  //  val publicKey12 = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(tmp5.takeRight(162)))
  //
  //
  //  val message2 = tmp5Left.take(tmp5Left.length - 128)
  //  md.update(message2)
  //  val digest2 = md.digest()
  //
  //  val dsaPublic = Signature.getInstance("SHA256withRSA")
  //  dsaPublic.initVerify(publicKey12)
  //  dsaPublic.update(digest2)
  //  println(dsaPublic.verify(tmp5Left.takeRight(128)))
  //  println(new String(message2, "UTF-8"))


  // 1:
  val random1 = SecureRandom.getInstance("SHA1PRNG", "SUN")
  // TODO check the parameters
  val keyGen1 = KeyPairGenerator.getInstance("RSA")
  keyGen1.initialize(1024, random1)
  val pairKey1 = keyGen1.generateKeyPair()
  val privateKey1 = pairKey1.getPrivate()
  val publicKey1 = pairKey1.getPublic()

  // 2:
  val random2 = SecureRandom.getInstance("SHA1PRNG", "SUN")
  val keyGen2 = KeyPairGenerator.getInstance("RSA")
  keyGen2.initialize(1024, random2)
  val pairKey2 = keyGen2.generateKeyPair()
  val privateKey2 = pairKey2.getPrivate()
  val publicKey2 = pairKey2.getPublic()

  val msg = "abwoeijo"
  val abc = encrypt(msg, privateKey1, publicKey1, publicKey2)
  println(decrypt(abc, privateKey2, publicKey2))


  def encrypt(msg: String, myPrivateKey: PrivateKey, myPublicKeyA: PublicKey, publicKey: PublicKey): Array[Byte] = {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(msg.getBytes("UTF-8"))
    val digest = md.digest()

    val symKey = KeyGenerator.getInstance("AES").generateKey()

    val dsaPrivate = Signature.getInstance("SHA256withRSA")
    dsaPrivate.initSign(myPrivateKey)
    dsaPrivate.update(digest)
    val signature = dsaPrivate.sign
    println("signature " + signature)
    val cipher1 = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher1.init(Cipher.ENCRYPT_MODE, symKey)
    val tmp1 = cipher1.doFinal(msg.getBytes ++ signature ++ myPublicKeyA.getEncoded)

    val cipher2 = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher2.init(Cipher.WRAP_MODE, publicKey)
    val tmp2 = cipher2.wrap(symKey)

    tmp1 ++ tmp2

  }

  def decrypt(encryptedMsg: Array[Byte], myPrivateKey: PrivateKey, myPublicKey: PublicKey): Option[String] = {
    val tmp1 = encryptedMsg.take(encryptedMsg.length - 128)
    val tmp2 = encryptedMsg.takeRight(128)

    val cipher1 = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher1.init(Cipher.UNWRAP_MODE, myPrivateKey)
    val symK = cipher1.unwrap(tmp2, "AES", Cipher.SECRET_KEY)

    val cipher2 = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher2.init(Cipher.DECRYPT_MODE, symK)
    val tmp3 = cipher2.doFinal(tmp1)
    val tmp4 = tmp3.take(tmp3.length - 162)
    val publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(tmp3.takeRight(162)))

    val msg = tmp4.take(tmp4.length - 128)
    val md = MessageDigest.getInstance("SHA-256")
    md.update(msg)
    val digest = md.digest()

    val dsaPublic = Signature.getInstance("SHA256withRSA")
    dsaPublic.initVerify(publicKey)
    dsaPublic.update(digest)
    if (dsaPublic.verify(tmp4.takeRight(128)))
      Some(new String(msg, "UTF-8"))
    else
      None
  }

}
