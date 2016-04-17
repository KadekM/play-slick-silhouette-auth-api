package service

trait Hasher {
  /**
    * Hash readable string into readable string
    * @param text to hash
    * @return hashed text
    */
  def hash(text: String): String
}
