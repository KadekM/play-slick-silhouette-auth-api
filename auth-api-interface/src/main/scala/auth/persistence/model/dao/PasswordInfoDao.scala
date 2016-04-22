package auth.persistence.model.dao

import com.mohiva.play.silhouette.api.util.{ PasswordInfo ⇒ SilhouettePasswordInfo }
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO

trait PasswordInfoDao extends DelegableAuthInfoDAO[SilhouettePasswordInfo]
