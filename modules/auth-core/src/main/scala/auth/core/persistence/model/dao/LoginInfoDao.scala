package auth.core.persistence.model.dao

import java.util.UUID

import auth.core.persistence.SilhouetteLoginInfo

import scala.concurrent.Future

trait LoginInfoDao {
  def save(loginInfo: SilhouetteLoginInfo, userUuid: UUID): Future[Unit]
}
