package Models

import play.api.libs.json._
import play.api.Play.current
import securesocial.core._
import securesocial.core.IdentityId
import org.reactivecouchbase.play.crud.CouchbaseCrudSourceController
import org.reactivecouchbase.play.PlayCouchbase
import play.api.Play
import scala.collection.JavaConversions._

/**
 * The representation of a persisted SecureSocialUser into Couchbase.
 *
 * @param idKey the key of this user into Couchbase
 * @param secureSocialUser persisted secureSocialUser
 */
case class User(idKey: String,
                secureSocialUser: SecureSocialUser)

/**
 * The mapping of the SecureSocial Identity trait as a case class.
 * Used for json formating, case matching and implicit conversion.
 *
 *
 * @param identityId Identity.identityId
 * @param firstName Identity.firstName
 * @param lastName Identity.lastName
 * @param fullName Identity.fullName
 * @param email Identity.email
 * @param avatarUrl Identity.avatarUrl
 * @param authMethod Identity.authMethod
 * @param oAuth1Info Identity.oAuth1Info
 * @param oAuth2Info Identity.oAuth2Info
 * @param passwordInfo Identity.passwordInfo
 */
case class SecureSocialUser(identityId: IdentityId,
                            firstName: String,
                            lastName: String,
                            fullName: String,
                            email: Option[String],
                            avatarUrl: Option[String],
                            authMethod: AuthenticationMethod,
                            oAuth1Info: Option[OAuth1Info] = None,
                            oAuth2Info: Option[OAuth2Info] = None,
                            passwordInfo: Option[PasswordInfo] = None) extends Identity

/**
 * The companion object for User.
 * Provides json formating and implicit conversion.
 */
object User {

    implicit val identityIdJsonFormater = Json.format[IdentityId]
    implicit val authMethodJsonFormater = Json.format[AuthenticationMethod]
    implicit val oAuth1InfoJsonFormater = Json.format[OAuth1Info]
    implicit val oAuth2InfoJsonFormater = Json.format[OAuth2Info]
    implicit val passwordInfoJsonFormater = Json.format[PasswordInfo]
    implicit val secureSocialUserJsonFormater = Json.format[SecureSocialUser]
    implicit val userJsonFormater = Json.format[User]

    implicit def userToIdentity(user: User): Identity = user.secureSocialUser
    implicit def identityIdToIdKey(identityId: IdentityId): String = identityId.providerId + "_" + identityId.userId
    implicit def identityToUser(identity: Identity): User = User(identity.identityId, SecureSocialUser(identity.identityId, identity.firstName, identity.lastName,
        identity.fullName, identity.email, identity.avatarUrl, identity.authMethod,
        identity.oAuth1Info, identity.oAuth2Info, identity.passwordInfo))
    implicit def MaybeUserToMaybeIdentity(maybeUser: Option[User]): Option[Identity] = maybeUser match {
        case Some(user) => Some(user.secureSocialUser)
        case None => None
    }
}

/**
 * The CRUD controller for User.
 */
object UserController extends CouchbaseCrudSourceController[User] {

    // Get all existing buckets (alias + name) from configuration
    case class Bucket(alias: String, name: String)
    private val buckets: List[Bucket] = Play.current.configuration.getObjectList("couchbase.buckets").get.toList.map(b =>
        Bucket(b.get("alias").unwrapped.toString, b.get("bucket").unwrapped.toString)
    )

    // Get the bucket for this controller
    val bucketAlias: String = "users"
    def bucket = PlayCouchbase.bucket(buckets.foldRight("default")((b, n) => {
        if (b.alias == bucketAlias) b.name else n
    }))

    val viewPrefix = if (Play.isDev) "dev_" else ""
    override def defaultViewName = "by_idKey"
    override def defaultDesignDocname = s"${viewPrefix}${bucketAlias}"
    override def idKey = "idKey"
}
