package auth.api.persistence

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

import auth.api.model.core.UserToken
import auth.api.model.core.UserToken.UserTokenAction
import auth.direct.persistence.HasAuthDbProfile
import auth.direct.persistence.model._
import slick.dbio.Effect._
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext

trait TablesDefinitions
    extends ModelMappingSupport with CoreAuthTablesDefinitions with HasAuthDbProfile {
  import driver.api._

  sealed class UserTokenMapping(tag: Tag) extends Table[UserToken](tag, "userTokens") {
    def token: Rep[String]  = column[String]("token", O.PrimaryKey)
    def userUuid: Rep[UUID] = column[UUID]("users_uuid")
    // TODO: Localdatetime fails on postgres
    //def expiresOn: Rep[LocalDateTime] = column[LocalDateTime]("expireson")
    def expiresOn: Rep[Timestamp]         = column[Timestamp]("expiresOn")
    def tokenAction: Rep[UserTokenAction] = column[UserTokenAction]("tokenAction")

    def usersFk = foreignKey("fk_users_uuid", userUuid, usersQuery)(_.uuid)

    def from(x: UserToken): Option[(String, UUID, Timestamp, UserTokenAction)] = Some {
      (x.token, x.userUuid, Timestamp.valueOf(x.expiresOn), x.tokenAction)
    }

    def to(x: (String, UUID, Timestamp, UserTokenAction)): UserToken =
      UserToken(x._1, x._2, x._3.toLocalDateTime, x._4)

    override def * : ProvenShape[UserToken] =
      (token, userUuid, expiresOn, tokenAction) <> (to, from)
  }

  val userTokensQuery = TableQuery[UserTokenMapping]

  class Api()(implicit ec: ExecutionContext) extends super.Api()(ec) {
    object UserTokens {
      def issue(tokenHash: String,
                userUuid: UUID,
                action: UserTokenAction,
                forHours: Long): DBIOAction[UserToken, NoStream, Write] = {
        val token = UserToken(tokenHash, userUuid, LocalDateTime.now.plusHours(forHours), action)
        val act   = userTokensQuery += token
        act.map(_ ⇒ token)
      }

      def find(token: String): DBIOAction[Option[UserToken], NoStream, Read] =
        userTokensQuery.filter(_.token === token).result.headOption

      def remove(token: String): DBIOAction[Boolean, NoStream, Write] =
        userTokensQuery.filter(_.token === token).delete.map(deleted ⇒ deleted > 0)
    }
  }
}
