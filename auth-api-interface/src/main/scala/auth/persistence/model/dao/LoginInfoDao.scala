package auth.persistence.model.dao

import auth.persistence.SilhouetteLoginInfo

import scala.concurrent.Future

trait LoginInfoDao {
  def save(loginInfo: SilhouetteLoginInfo, userUuid: String): Future[Unit]
}
