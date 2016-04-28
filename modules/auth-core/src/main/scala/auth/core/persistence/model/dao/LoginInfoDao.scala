package auth.core.persistence.model.dao

import auth.core.persistence.SilhouetteLoginInfo

import scala.concurrent.Future

trait LoginInfoDao {
  def save(loginInfo: SilhouetteLoginInfo, userUuid: String): Future[Unit]
}
