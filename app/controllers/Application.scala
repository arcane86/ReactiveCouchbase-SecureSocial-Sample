package controllers

import play.api.mvc._

object Application extends Controller with securesocial.core.SecureSocial {

    def index = Action {
        Redirect("/userAware")
    }

    def secured = SecuredAction {
        implicit request =>
            Ok(views.html.index(request.user.fullName))
    }

    def userAware = UserAwareAction {
        implicit request =>
            val userName = request.user match {
                case Some(user) => user.fullName
                case _ => "guest"
            }
            Ok(views.html.index(userName))
    }
}
