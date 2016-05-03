package auth.api.persistence

import java.sql.Timestamp
import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import auth.core.model.core.User.UserState
import auth.core.persistence.HasAuthDbProfile
import auth.core.persistence.model.{AuthModelMappingSupport, CoreAuthTablesDefinitions}
import slick.lifted.ProvenShape

trait TablesDefinitions extends ModelMappingSupport with CoreAuthTablesDefinitions with HasAuthDbProfile {
  import driver.api._

  sealed class UserTokenMapping(tag: Tag) extends Table[UserToken](tag, "userTokens") {
    def token: Rep[String] = column[String]("token", O.PrimaryKey)
    def userUuid: Rep[UUID] = column[UUID]("users_uuid")
    // TODO: Localdatetime fails on postgres
    //def expiresOn: Rep[LocalDateTime] = column[LocalDateTime]("expireson")
    def expiresOn: Rep[Timestamp] = column[Timestamp]("expiresOn")
    def tokenAction: Rep[UserTokenAction] = column[UserTokenAction]("tokenAction")

    def usersFk = foreignKey("fk_users_uuid", userUuid, usersQuery)(_.uuid)

    def from(x: UserToken): Option[(String, UUID, Timestamp, UserTokenAction)] = Some {
      (x.token, x.userUuid, Timestamp.valueOf(x.expiresOn), x.tokenAction)
    }

    def to(x:(String, UUID, Timestamp, UserTokenAction)): UserToken = UserToken(x._1, x._2, x._3.toLocalDateTime, x._4)

    override def * : ProvenShape[UserToken] =
      (token, userUuid, expiresOn, tokenAction) <> (to,from)
  }

  val userTokensQuery = TableQuery[UserTokenMapping]
}
