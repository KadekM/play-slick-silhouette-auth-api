package persistence.model

import java.sql.Timestamp
import java.time.LocalDateTime

import auth.persistence.HasAuthDbProfile
import model.core.UserToken
import model.core.UserToken.UserTokenAction
import slick.lifted.ProvenShape

trait TablesDefinitions extends ModelMappingSupport with HasAuthDbProfile {
  import driver.api._

  sealed class UserTokenMapping(tag: Tag) extends Table[UserToken](tag, "usertokens") {
    def token: Rep[String] = column[String]("token", O.PrimaryKey)
    def userUuid: Rep[String] = column[String]("useruuid")
    // TODO: Localdatetime fails on postgres
    //def expiresOn: Rep[LocalDateTime] = column[LocalDateTime]("expireson")
    def expiresOn: Rep[Timestamp] = column[Timestamp]("expireson")
    def tokenAction: Rep[UserTokenAction] = column[UserTokenAction]("tokenaction")

    def from(x: UserToken): Option[(String, String, Timestamp, UserTokenAction)] = Some {
      (x.token, x.userUuid, Timestamp.valueOf(x.expiresOn), x.tokenAction)
    }

    def to(x:(String, String, Timestamp, UserTokenAction)): UserToken = UserToken(x._1, x._2, x._3.toLocalDateTime, x._4)

    override def * : ProvenShape[UserToken] =
      (token, userUuid, expiresOn, tokenAction) <> (to,from)
  }

  val userTokensQuery = TableQuery[UserTokenMapping]
}
