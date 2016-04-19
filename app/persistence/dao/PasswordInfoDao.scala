package persistence.dao

import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

trait PasswordInfoDao extends DelegableAuthInfoDAO[PasswordInfo]
