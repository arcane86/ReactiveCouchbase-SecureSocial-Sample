package services

import securesocial.core._
import securesocial.core.providers.Token
import securesocial.core.IdentityId
import play.api.Application
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import com.couchbase.client.protocol.views.{ComplexKey, Stale, Query}
import scala.util.{Failure, Success}
import play.Logger
import Models.{User, UserController}
import Models.User._

/**
 * The SecureSocial UserServicePlugin custom implementation for Reactive Couchbase
 *
 * @param application the Play application
 */
class SecureSocialUserService(application: Application) extends UserServicePlugin(application) {

    implicit val context = scala.concurrent.ExecutionContext.Implicits.global

    /**
     * Finds a user that matches the specified id.
     *
     * @param id the user id
     * @return an optional user
     */
    def find(id: IdentityId): Option[Identity] = {
        val mayBeUser: Future[Option[Identity]] = UserController.bucket.get[User](id).map(u => u)
        Await.result(mayBeUser, 10 seconds)
    }

    /**
     * Finds a user by email and provider id.
     *
     * @param email the user email
     * @param providerId the provider id
     * @return an optional user
     */
    def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
        val query = new Query().setIncludeDocs(true).setLimit(1)
                .setRangeStart(ComplexKey.of(email, providerId)).setRangeEnd(ComplexKey.of(s"$email\uefff", s"$providerId\uefff"))
                .setStale(Stale.FALSE)
        val mayBeUser: Future[Option[Identity]] = UserController.bucket.find[User](UserController.defaultDesignDocname, "by_emailAndProvider")(query).map(_.headOption)
        Await.result(mayBeUser, 10 seconds)
    }

    /**
     * Saves the user; gets called when a user logs in.
     *
     * @param identity the identity to save
     * @return the saved Identity
     */
    def save(identity: Identity): Identity = {
        UserController.bucket.set[User](identity.identityId, identity).onComplete {
            case Success(u) =>
                if (Logger.isDebugEnabled()) {
                    Logger.debug("Saved an user for Identity %s".format(identity))
                }
            case Failure(u) =>
                if (Logger.isErrorEnabled) {
                    Logger.error("Unable to save an user for Identity %s".format(identity))
                }
        }
        identity
    }

    /**
     * Cache for active tokens.
     */
    private var tokens = Map[String, Token]()

    /**
     * Saves a token.
     * This is needed for users that are creating an account in the system instead of using one in a 3rd party system.
     *
     * @param token the token to save
     * @return a string with a uuid that will be embedded in the welcome email.
     */
    def save(token: Token) = {
        tokens += (token.uuid -> token)
    }

    /**
     * Finds a token
     *
     * @param token the token id
     * @return
     */
    def findToken(token: String): Option[Token] = {
        tokens.get(token)
    }

    /**
     * Deletes a token
     *
     * @param uuid the token id
     */
    def deleteToken(uuid: String) {
        tokens -= uuid
    }

    /**
     * Deletes all tokens
     *
     */
    def deleteTokens() {
        tokens = Map()
    }

    /**
     * Deletes all expired tokens
     *
     */
    def deleteExpiredTokens() {
        tokens = tokens.filter(!_._2.isExpired)
    }
}
