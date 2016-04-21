package persistence

import com.mohiva.play.silhouette.api.LoginInfo

// TODO: hardcoded dependency to authPostgres
import persistence.drivers.impl.AuthPostgresDriver.api._

object UserTable {
  lazy val query = TableQuery[UserMapping]
}

object LoginInfoTable {
  lazy val query = TableQuery[LoginInfoMapping]

  def findDbLoginInfo(loginInfo: LoginInfo): Query[LoginInfoMapping, DbLoginInfo, Seq] =
    query.filter(db => db.providerId === loginInfo.providerID && db.providerKey === loginInfo.providerKey)
}

object PasswordInfoTable {
  lazy val query = TableQuery[PasswordInfoMapping]
}
