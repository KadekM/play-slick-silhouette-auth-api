package auth.api.service

import java.util.UUID

import auth.api.model.exchange.UpdateUser
import auth.direct.service.UserService

import scala.concurrent.Future

trait ExtendedUserService extends UserService {

  /**
    * Updates user of `userUuid` with changed fields
    */
  def update(userUuid: UUID, update: UpdateUser): Future[Boolean]
}
