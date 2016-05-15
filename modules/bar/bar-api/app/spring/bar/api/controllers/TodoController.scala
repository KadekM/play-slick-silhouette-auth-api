package spring.bar.api.controllers

import play.api.mvc.{Action, AnyContent, Controller}

class TodoController extends Controller {
  def todo0: Action[AnyContent]                       = ???
  def todo1(x: String): Action[AnyContent]            = ???
  def todo2(x: String, y: String): Action[AnyContent] = ???
}
