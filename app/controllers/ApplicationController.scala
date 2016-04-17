package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.{Env, Silhouette}
import play.api.mvc.Controller
import utils.auth.DefaultEnv

class ApplicationController @Inject()(silhouette: Silhouette[DefaultEnv]) extends Controller {

}
