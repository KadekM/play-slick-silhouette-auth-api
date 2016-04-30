package auth.core.persistence.model.repo

import java.util.UUID

import auth.core.model.core.User
import auth.core.model.core.User.UserState
import com.mohiva.play.silhouette
import slick.dbio.Effect.{Read, Write}
import slick.dbio.{DBIOAction, NoStream}
import slick.profile.FixedSqlStreamingAction

trait UserRepo {
  def find(loginInfo: silhouette.api.LoginInfo): DBIOAction[Option[User], NoStream, Read]

  def find(userUuid: UUID): DBIOAction[Option[User], NoStream, Read]

  def save(user: User): DBIOAction[User, NoStream, Write]

  def setState(userUuid: UUID, newState: UserState): DBIOAction[Boolean, NoStream, Write]

  def list: FixedSqlStreamingAction[Seq[User], User, Read]
}

