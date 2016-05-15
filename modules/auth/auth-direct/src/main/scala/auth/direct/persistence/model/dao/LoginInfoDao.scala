package auth.direct.persistence.model.dao

import java.util.UUID

import auth.direct.persistence.SilhouetteLoginInfo

import scala.concurrent.Future

trait LoginInfoDao {

  /**
    * Saves authentication login information assigned to user `userUuid`
    */
  def save(loginInfo: SilhouetteLoginInfo, userUuid: UUID): Future[Unit]
}
