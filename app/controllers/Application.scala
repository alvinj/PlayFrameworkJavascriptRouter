package controllers

import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import routes.javascript._
import model.User

case class UserSignUpForm(email: String, password: String)

object Application extends Controller {

  /**
   * A play form that matches the signup form template
   */
  val signupForm: Form[UserSignUpForm] = Form(
    mapping(
      "email" -> email,
      "password" -> tuple(
        "main" -> text(minLength = 6),
        "confirm" -> text).verifying(
          //  Add an additional constraint: both passwords must match
          "Passwords don't match", passwords => passwords._1 == passwords._2)) {
        // Binding: Create a User from the mapping result (ignore the second password and the accept field)
        (email, passwords) => UserSignUpForm(email, passwords._1)
      } {
        // Unbinding: Create the mapping values from an existing User value
        user => Some(user.email, (user.password, ""))
      })

  /**
   * Show the signup form template.
   */
  def showSignUpForm = Action {
    Ok(views.html.signUpForm(signupForm, "Sign Up Form"))
  }

  /**
   * This method gets called by the signup form, but as the documentation 
   * states, that's not the purpose of this example. This code is just here
   * to keep Play happy, and show how this method might be stubbed out.
   */
  def createUser = Action { implicit request =>
    signupForm.bindFromRequest.fold(
      errors => BadRequest(views.html.signUpForm(errors, "There is some error")),
      signupForm => {
        // would do the normal stuff to create a user account here and redirect the user to another page.
        Ok(views.html.signUpForm(Application.signupForm, ""))
      })
  }

  /**
   * this method is called by the template ajax code
   * to determine whether the email address is already in the database.
   * real code would check the database to see if the email address is already in there.
   * to keep it simple, this method always returns true, which is what the JavaScript is
   * looking for.
   */
  def doesEmailAddressExist(email: String) = Action { implicit request =>
    Ok("true")
  }  
  
  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(Routes.javascriptRouter("jsRoutes")(routes.javascript.Application.doesEmailAddressExist)).as("text/javascript")
  }
  
}


