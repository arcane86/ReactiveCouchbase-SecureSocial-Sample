package controllers

import play.api.mvc._

object Application extends Controller with securesocial.core.SecureSocial {

    def index = Action {
        Redirect("/userAware")
    }

    def secured = SecuredAction {
        implicit request =>
            Ok(views.html.index(true, Some(request.user)))
    }

    def userAware = UserAwareAction {
        implicit request =>
            Ok(views.html.index(false, request.user))
    }
}
