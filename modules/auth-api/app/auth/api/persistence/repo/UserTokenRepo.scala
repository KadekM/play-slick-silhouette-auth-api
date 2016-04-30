package auth.api.persistence.repo

import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import slick.dbio.Effect.{Read, Write}
import slick.dbio.{DBIOAction, NoStream}

trait UserTokenRepo {
  def issue(userUuid: UUID, action: UserTokenAction): DBIOAction[UserToken, NoStream, Write]

  def find(token: String): DBIOAction[Option[UserToken], NoStream, Read]

  def remove(token: String): DBIOAction[Boolean, NoStream, Write]
}
