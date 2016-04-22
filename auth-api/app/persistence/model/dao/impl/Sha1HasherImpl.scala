package persistence.model.dao.impl

import persistence.model.dao.Hasher
import play.api.libs.Codecs

class Sha1HasherImpl extends Hasher {
  private[this] val md = java.security.MessageDigest.getInstance("SHA-1")

  override def hash(text: String): String =
    Codecs.sha1(md.digest(text.getBytes))
}
